package com.cht.easygrpc.registry;

import com.cht.easygrpc.EasyGrpcContext;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;

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
    protected void doRegister(Node node) {
        this.serverNodePath = createNode(getServerPath(), false, true);

        EasyGrpcServiceNode.Data data = new EasyGrpcServiceNode.Data(context.getServerConfig().getIp(),
                context.getServerConfig().getPort(), "service");
        this.serverCache = new PathChildrenCache(client, getFullPath(data), true);
        this.serverCache.getListenable().addListener(new ServerCacheListener());
        this.leaderSelector = new LeaderSelector(client, getSelectorPath(), new LeaderSelectorListenerAdapter(){

            @Override
            public void takeLeadership(CuratorFramework client) throws Exception {

            }
        });

    }

}
