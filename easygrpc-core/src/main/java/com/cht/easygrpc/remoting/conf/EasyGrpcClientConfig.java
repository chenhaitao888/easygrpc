package com.cht.easygrpc.remoting.conf;

import java.util.List;

/**
 * @author : chenhaitao934
 * @date : 5:40 下午 2020/10/10
 */
public class EasyGrpcClientConfig {

    private String clientName;

    private int workThreads = 50;

    private int timeoutInMillis = 5000;

    private List<String> ifaceNames;

    private String tag;

    private int queueCapacity = 1000;

    private int stubType;

    private EasyGrpcCircuitBreakerConfig circuitBreakerConfig;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getWorkThreads() {
        return workThreads;
    }

    public void setWorkThreads(int workThreads) {
        this.workThreads = workThreads;
    }

    public int getTimeoutInMillis() {
        return timeoutInMillis;
    }

    public void setTimeoutInMillis(int timeoutInMillis) {
        this.timeoutInMillis = timeoutInMillis;
    }

    public List<String> getIfaceNames() {
        return ifaceNames;
    }

    public void setIfaceNames(List<String> ifaceNames) {
        this.ifaceNames = ifaceNames;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public int getStubType() {
        return stubType;
    }

    public void setStubType(int stubType) {
        this.stubType = stubType;
    }

    public EasyGrpcCircuitBreakerConfig getCircuitBreakerConfig() {
        return circuitBreakerConfig;
    }

    public void setCircuitBreakerConfig(EasyGrpcCircuitBreakerConfig circuitBreakerConfig) {
        this.circuitBreakerConfig = circuitBreakerConfig;
    }
}
