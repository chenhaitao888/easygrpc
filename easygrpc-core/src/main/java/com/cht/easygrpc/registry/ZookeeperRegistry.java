package com.cht.easygrpc.registry;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.exception.RegistryException;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author : chenhaitao934
 * @date : 12:59 上午 2020/10/12
 */
public abstract class ZookeeperRegistry extends AbstractRegistry{

    private CuratorFramework client;



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


    protected CuratorFramework getClient() {
        return client;
    }





}
