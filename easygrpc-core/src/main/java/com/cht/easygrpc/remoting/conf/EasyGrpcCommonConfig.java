package com.cht.easygrpc.remoting.conf;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : chenhaitao934
 * @date : 1:05 上午 2020/10/12
 */
public class EasyGrpcCommonConfig {

    private String registryAddress;

    private String appId;

    private int lbStrategy;

    private Map<String, String> parameters = new HashMap<>();

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

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }
}
