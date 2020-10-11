package com.cht.easygrpc.registry;

import com.cht.easygrpc.EasyGrpcContext;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author : chenhaitao934
 * @date : 12:59 上午 2020/10/12
 */
public class ZookeeperRegistry extends AbstractRegistry{

    private CuratorFramework client;

    private String appId;

    private RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, Integer.MAX_VALUE);

    public ZookeeperRegistry(EasyGrpcContext context) {
        super(context);
        this.appId = context.getCommonConfig().getAppId();
        String registryAddress = context.getCommonConfig().getRegistryAddress();
        this.client =  CuratorFrameworkFactory.newClient(registryAddress, retryPolicy);
        this.client.start();
    }


    @Override
    protected void doRegister(Node node) {

    }
}
