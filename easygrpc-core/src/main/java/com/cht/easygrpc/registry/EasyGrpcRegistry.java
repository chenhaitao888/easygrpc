package com.cht.easygrpc.registry;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.helper.CollectionHelper;
import com.cht.easygrpc.helper.JsonHelper;
import com.cht.easygrpc.helper.PathHelper;
import com.cht.easygrpc.remoting.EasyGrpcCircuitBreakerManager;
import com.cht.easygrpc.remoting.conf.EasyGrpcCircuitBreakerConfig;
import com.cht.easygrpc.remoting.conf.EasyGrpcClientConfig;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.leader.LeaderSelector;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode.POST_INITIALIZED_EVENT;

/**
 * @author : chenhaitao934
 * @date : 11:05 上午 2020/10/12
 */
public class EasyGrpcRegistry extends ZookeeperRegistry {

    private EasyGrpcContext context;

    public EasyGrpcRegistry(EasyGrpcContext context) {
        super(context);
        this.context = context;
    }


    @Override
    protected void doRegister() {
        if(!checkExists(getServerPath())){
            EasyGrpcServiceNode.Data data = new EasyGrpcServiceNode.Data(context.getServerConfig().getServiceName());
            this.serverNodePath = createNodeData(getServerPath(), false, false, JsonHelper.toBytes(data));
        }else {
            this.serverNodePath = getServerPath();
        }

        EasyGrpcServiceNode.Data data = new EasyGrpcServiceNode.Data(context.getServerConfig().getIp(),
                context.getServerConfig().getPort(), "service");
        data.setServiceName(context.getServerConfig().getServiceName());
        this.suriveNodePath = createNodeData(getFullPath(data), true, false, JsonHelper.toBytes(data));
        this.serverCache = new PathChildrenCache(client, PathHelper.getParentPath(getFullPath(data)), true);
        this.serverCache.getListenable().addListener(new ServerCacheListener());

        this.clientCache = new PathChildrenCache(client,
                getClientCachePath(context.getServerConfig().getServiceName()), true);
        this.clientCache.getListenable().addListener(new ClientCacheListener());

        List<EasyGrpcClientConfig> clientConfigs = context.getClientConfigs();
        if(CollectionHelper.isNotEmpty(clientConfigs)){
            clientConfigs.forEach(e -> {
                String clientName = e.getClientName();
                String serverPath = getServerPath(clientName);
                NodeCache nodeCache = new NodeCache(client, serverPath);
                if(nodeCaches == null){
                    nodeCaches = new CopyOnWriteArrayList<>();
                }
                nodeCaches.add(nodeCache);
                nodeCache.getListenable().addListener(new ServerNodeListener(nodeCache));
            });
        }
        this.leaderSelector = new LeaderSelector(client, getSelectorPath(), new MasterSlaveLeadershipSelectorListener());
        this.leaderSelector.autoRequeue();
        join();
    }


    @Override
    public void unRegister() throws Exception {
        if(checkExists(suriveNodePath)){
            getClient().delete().forPath(suriveNodePath);
            LOGGER.info("delete path {}", suriveNodePath);
        }
    }
}
