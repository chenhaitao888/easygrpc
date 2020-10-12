package com.cht.easygrpc.registry;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.constant.EasyGrpcLS;
import com.cht.easygrpc.discovery.EasyGrpcNameResolverProvider;
import com.cht.easygrpc.enums.EventStatus;
import com.cht.easygrpc.exception.RegistryException;
import com.cht.easygrpc.helper.EventHelper;
import com.cht.easygrpc.remoting.EasyGrpcChannelManager;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : chenhaitao934
 * @date : 12:59 上午 2020/10/12
 */
public abstract class ZookeeperRegistry extends AbstractRegistry{

    protected CuratorFramework client;

    protected PathChildrenCache serverCache;

    protected LeaderSelector leaderSelector;

    protected String serverNodePath;

    private static final Stat EMPTY_STAT = new Stat();

    private RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, Integer.MAX_VALUE);


    public ZookeeperRegistry(EasyGrpcContext context) {
        super(context);
        this.appId = context.getCommonConfig().getAppId();
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

    private String createNodeData(String path, boolean ephemeral, boolean sequential, byte[] data) {
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
            return new ChildData(path, EMPTY_STAT, client.getData().forPath(path));
        } catch (Exception e) {
            throw new RuntimeException(e);
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
                    serverData.addAdress(ip + ":" + port);
                    break;
                case REMOVE:
                    serverData.removeAdress(ip + ":" + port);
                    break;
            }

            EasyGrpcChannelManager channelManager = context.getEasyGrpcChannelManager();
            EasyGrpcNameResolverProvider resolverProvider = channelManager.getResolverProvider(context.getServerConfig().getServiceName());
            int lbStrategy = context.getCommonConfig().getLbStrategy() == 0 ? EasyGrpcLS.RANDOM : context.getCommonConfig().getLbStrategy();
            List<Map<String, Object>> servers = assembleServers(serverData);
            resolverProvider.refreshServerList(lbStrategy, servers);
        }

        private List<Map<String, Object>> assembleServers(EasyGrpcServiceNode.Data serverData) {
            List<Map<String, Object>> servers = new ArrayList<>();
            List<String> address = serverData.getAddress();
            address.forEach(e -> {
                String[] split = e.split(":");
                Map<String, Object> stringObjectMap = serverData.buildMap(split[0], Integer.parseInt(split[1]));
                servers.add(stringObjectMap);
            });
            return servers;
        }
    }

}
