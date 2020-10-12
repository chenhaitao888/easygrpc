package com.cht.easygrpc.remoting.conf;

/**
 * @author : chenhaitao934
 * @date : 1:05 上午 2020/10/12
 */
public class EasyGrpcCommonConfig {

    private String registryAddress;

    private String appId;

    private int lbStrategy;

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getLbStrategy() {
        return lbStrategy;
    }

    public void setLbStrategy(int lbStrategy) {
        this.lbStrategy = lbStrategy;
    }
}
