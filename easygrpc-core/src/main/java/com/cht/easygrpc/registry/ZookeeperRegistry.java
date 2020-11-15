package com.cht.easygrpc.registry;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.constant.EasyGrpcLS;
import com.cht.easygrpc.discovery.EasyGrpcNameResolverProvider;
import com.cht.easygrpc.enums.EventStatus;
import com.cht.easygrpc.exception.EasyGrpcException;
import com.cht.easygrpc.exception.RegistryException;
import com.cht.easygrpc.helper.CollectionHelper;
import com.cht.easygrpc.helper.EventHelper;
import com.cht.easygrpc.helper.PathHelper;
import com.cht.easygrpc.remoting.EasyGrpcChannelManager;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode.POST_INITIALIZED_EVENT;

/**
 * @author : chenhaitao934
 * @date : 12:59 上午 2020/10/12
 */
public abstract class ZookeeperRegistry extends AbstractRegistry{

    protected CuratorFramework client;

    protected PathChildrenCache serverCache;

    protected List<NodeCache> nodeCaches;

    protected LeaderSelector leaderSelector;

    protected String serverNodePath;

    protected String suriveNodePath;

    private static final Stat EMPTY_STAT = new Stat();

    protected AtomicReference<State> state = new AtomicReference<>();

    private RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, Integer.MAX_VALUE);


    public ZookeeperRegistry(EasyGrpcContext context) {
        super(context);
        this.appId = context.getCommonConfig().getAppId();
        this.state.set(State.LATENT);
        String registryAddress = context.getCommonConfig().getRegistryAddress();
        this.client = CuratorFrameworkFactory.newClient(registryAddress, retryPolicy);
        this.client.start();
    }


    protected String createNode(String path, boolean ephemeral, boolean sequential){
        try {
            if(ephemeral){
                return creatEphemeralNode(path, sequential);
            }else {
                return createPersistentNode(path, sequential);
            }
        } catch (Exception e) {
            throw new RegistryException("create node failure", e);
        }
    }
    
    protected boolean checkExists(String path){
        try {
            Stat stat = getClient().checkExists().forPath(path);
            return stat == null ? false : true;
        } catch (Exception e) {
            throw new RegistryException("check node exist failure", e);
        }
    }

    protected String createNodeData(String path, boolean ephemeral, boolean sequential, byte[] data) {
        try {
            if(ephemeral){
                return creatEphemeralNode(path, data, sequential);
            }else {
                return createPersistentNode(path, data, sequential);
            }
        } catch (Exception e) {
            throw new RegistryException("create node and data failure", e);
        }
    }

    private String createPersistentNode(String path, boolean sequential) {
        return createPersistentNode(path, null, sequential);
    }

    private String createPersistentNode(String path, byte[] data, boolean sequential) {
        String res;
        try {
            if(data != null){
                if(sequential){
                    res = getClient().create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(path, data);
                }else {
                    res = getClient().create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, data);
                }
            }else{
                if(sequential){
                    res = getClient().create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(path);
                }else {
                    res = getClient().create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                }
            }
            return res;
        } catch (Exception e) {
            throw new RegistryException("create persistent node failure", e);
        }
    }

    private String creatEphemeralNode(String path, boolean sequential) {
        return creatEphemeralNode(path, null, sequential);
    }

    private String creatEphemeralNode(String path, byte[] data, boolean sequential) {
        String res;
        try {
            if(data != null){
                if(sequential){
                    res =
                            getClient().create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path, data);
                }else {
                    res = getClient().create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path,
                            data);
                }
            }else {
                if(sequential){
                    res =
                            getClient().create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path);
                }else {
                    res = getClient().create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
                }
            }
            return res;
        } catch (Exception e) {
            throw new RegistryException("create ephemeral node failure", e);
        }
    }

    protected ChildData getData(String path) {
        try {
            Stat stat = client.checkExists().forPath(path);
            if(stat == null){
                return null;
            }
            return new ChildData(path, EMPTY_STAT, client.getData().forPath(path));
        } catch (Exception e) {
            throw new RegistryException("get data from failure", e);
        }
    }

    @Override
    public ChildData getServerData(){
        try {
            return new ChildData(serverNodePath, EMPTY_STAT, client.getData().forPath(serverNodePath));
        } catch (Exception e) {
            throw new RegistryException("get data from failure", e);
        }
    }

    @Override
    public ChildData getServiceData(String serviceName) {
        String servicePath = getServerPath(serviceName);
        try {
            Stat stat = client.checkExists().forPath(servicePath);
            if(stat == null){
                return new ChildData(servicePath, EMPTY_STAT, null);
            }
            return new ChildData(servicePath, EMPTY_STAT, client.getData().forPath(servicePath));
        } catch (Exception e) {
            throw new RegistryException("get data from failure", e);
        }
    }

    protected Stat setData(String path, byte[] data) {
        try {
            return getClient().setData().forPath(path, data);
        } catch (Exception e) {
            throw new RegistryException("set data to zk failure", e);
        }
    }


    protected CuratorFramework getClient() {
        return client;
    }

    class ServerCacheListener implements PathChildrenCacheListener {

        @Override
        public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
            if(!leaderSelector.hasLeadership()){
                return;
            }
            if(!EventHelper.modifyEvent(event)){
                return;
            }
            EasyGrpcServiceNode serviceNode = new EasyGrpcServiceNode(event.getData());
            EasyGrpcServiceNode.Data data = serviceNode.getData();
            EasyGrpcServiceNode node = new EasyGrpcServiceNode(getData(serverNodePath));
            EasyGrpcServiceNode.Data serverData = node.getData();
            if(data == null){
                return;
            }
            if(EventHelper.addEvent(event)){
                handleServerNode(serverData, data, EventStatus.ADD);
            }
            if(EventHelper.removeEvent(event)){
                handleServerNode(serverData, data, EventStatus.REMOVE);
            }

        }

        private void handleServerNode(EasyGrpcServiceNode.Data serverData, EasyGrpcServiceNode.Data data,
                                      EventStatus status) {
            String ip = data.getIp();
            int port = data.getPort();

            switch (status) {
                case ADD:
                    String suriveAddress = ip + ":" + port;
                    List<String> address = serverData.getAddress();
                    if(CollectionHelper.isNotEmpty(address) && address.contains(suriveAddress)){
                        return;
                    }
                    serverData.addAdress(suriveAddress);
                    break;
                case REMOVE:
                    serverData.removeAdress(ip + ":" + port);
                    break;
            }
            updateServerNode(serverNodePath, serverData);
        }
    }

    class ServerNodeListener implements NodeCacheListener{

        private NodeCache nodeCache;

        public ServerNodeListener(NodeCache nodeCache) {
            this.nodeCache = nodeCache;
        }

        @Override
        public void nodeChanged() throws Exception {
            if(!leaderSelector.hasLeadership()){
                return;
            }
            EasyGrpcServiceNode serviceNode = new EasyGrpcServiceNode(nodeCache.getCurrentData());
            EasyGrpcServiceNode.Data data = serviceNode.getData();
            refreshServerNode(data);
        }
    }


    private void updateServerNode(String serverNodePath, EasyGrpcServiceNode.Data serverData) {
        EasyGrpcServiceNode node = new EasyGrpcServiceNode(serverNodePath, serverData);
        setData(node.getPath(), node.getDataBytes());
    }

    class MasterSlaveLeadershipSelectorListener extends AbstractLeadershipSelectorListener {

        @Override
        public void acquireLeadership() throws Exception {
            checkUnavailableNodes();
            EasyGrpcServiceNode node = new EasyGrpcServiceNode(getData(suriveNodePath));
            node.getData().setNodeState("Master");
            updateServerNode(suriveNodePath, node.getData());
            serverCache.start();
        }

        private void checkUnavailableNodes() {
            List<String> serviceNodes = getAllServiceNodes();
            EasyGrpcServiceNode node = new EasyGrpcServiceNode(getData(serverNodePath));
            EasyGrpcServiceNode.Data data = node.getData();
            if(CollectionHelper.isNotEmpty(serviceNodes)){
                data.setAddress(null);
                serviceNodes.forEach(e -> data.addAdress(e));
            }
            updateServerNode(serverNodePath, data);

        }

        @Override
        public void relinquishLeadership() {
            try {
                if (serverCache != null) {
                    serverCache.close();
                }
            } catch (Exception e) {
                LOGGER.warn("server cache close failed.", e);
            }
        }
    }

    private void refreshServerNode(EasyGrpcServiceNode.Data data) {

        EasyGrpcChannelManager channelManager = context.getEasyGrpcChannelManager();
        int lbStrategy = context.getCommonConfig().getLbStrategy() == 0 ? EasyGrpcLS.RANDOM : context.getCommonConfig().getLbStrategy();
        List<Map<String, Object>> servers = assembleServers(data);
        EasyGrpcNameResolverProvider resolverProvider = channelManager.getResolverProvider(data.getServiceName());
        if(resolverProvider == null){
            resolverProvider = new EasyGrpcNameResolverProvider(servers, lbStrategy);
            channelManager.putResolverProvider(data.getServiceName(), resolverProvider);
        }
        resolverProvider.refreshServerList(lbStrategy, servers);
    }

    protected List<String> getAllServiceNodes() {
        return getChildren(PathHelper.getParentPath(suriveNodePath));
    }

    protected List<String> getChildren(String parentPath) {
        if (!checkExists(parentPath)) {
            return new ArrayList<>();
        }
        try {
            List<String> children = client.getChildren().forPath(parentPath);
            return children;
        } catch (Exception e) {
            throw new EasyGrpcException(e);
        }

    }

    protected abstract class AbstractLeadershipSelectorListener implements LeaderSelectorListener {
        private final AtomicInteger leaderCount = new AtomicInteger();

        private Object mutex = new Object();

        public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
            String node = context.getServerConfig().getIp() + ":"
                    + context.getServerConfig().getPort();
            LOGGER.info(node + " is now the leader ,and has been leader " + this.leaderCount.getAndIncrement() + " time(s) before.");
            boolean isJoined = isJoined();
            try {
                if (isJoined) {
                    acquireLeadership();
                }
            } catch (Throwable e) {
                relinquishLeadership();
                return;
            }
            try {
                synchronized (mutex) {
                    mutex.wait();
                }
            } catch (InterruptedException e) {
                LOGGER.error( node + " has been interrupted", e);
            }
        }

        public void stateChanged(CuratorFramework client, ConnectionState newState) {
            if (!newState.isConnected()) {
                relinquishLeadership();
                synchronized (mutex) {
                    mutex.notify();
                }
            }
        }

        public abstract void acquireLeadership() throws Exception;

        public abstract void relinquishLeadership();
    }

    protected enum State { LATENT, JOINED, EXITED}

    protected boolean isJoined() {
        return this.state.get() == State.JOINED;
    }

    public void join() {
        if(state.compareAndSet(State.LATENT, State.JOINED)){
            leaderSelector.start();
            if(CollectionHelper.isNotEmpty(nodeCaches)){
                nodeCaches.forEach(e -> {
                    try {
                        e.start(true);
                    } catch (Exception exception) {
                        LOGGER.error("start node cache failure", e);
                    }
                });
            }
        }

    }

    @Override
    public List<Map<String, Object>> assembleServers(EasyGrpcServiceNode.Data serverData) {
        List<Map<String, Object>> servers = new ArrayList<>();
        if(serverData == null){
            return servers;
        }
        List<String> address = serverData.getAddress();
        LOGGER.info("{} service 's addresses: {}", serverData.getServiceName(), address);
        if(CollectionHelper.isEmpty(address)){
            return servers;
        }
        address.forEach(e -> {
            String[] split = e.split(":");
            Map<String, Object> stringObjectMap = serverData.buildMap(split[0], Integer.parseInt(split[1]));
            servers.add(stringObjectMap);
        });
        return servers;
    }
}
