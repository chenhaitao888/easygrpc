package com.cht.easygrpc.registry;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.helper.JsonHelper;
import com.cht.easygrpc.helper.PathHelper;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.leader.LeaderSelector;

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
            EasyGrpcServiceNode.Data data = new EasyGrpcServiceNode.Data();
            this.serverNodePath = createNodeData(getServerPath(), false, false, JsonHelper.toBytes(data));
        }else {
            this.serverNodePath = getServerPath();
        }
        EasyGrpcServiceNode.Data data = new EasyGrpcServiceNode.Data(context.getServerConfig().getIp(),
                context.getServerConfig().getPort(), "service");
        this.suriveNodePath = createNodeData(getFullPath(data), true, false, JsonHelper.toBytes(data));
        this.serverCache = new PathChildrenCache(client, PathHelper.getParentPath(getFullPath(data)), true);
        this.serverCache.getListenable().addListener(new ServerCacheListener());
        this.leaderSelector = new LeaderSelector(client, getSelectorPath(), new MasterSlaveLeadershipSelectorListener());
        this.leaderSelector.autoRequeue();
        join();
    }


}
