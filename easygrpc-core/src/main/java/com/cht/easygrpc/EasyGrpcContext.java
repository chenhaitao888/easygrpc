package com.cht.easygrpc;

import com.cht.easygrpc.remoting.EasyGrpcChannelManager;
import com.cht.easygrpc.remoting.EasyGrpcCircuitBreakerManager;
import com.cht.easygrpc.remoting.EasyGrpcManager;
import com.cht.easygrpc.remoting.conf.ConfigContext;
import com.cht.easygrpc.remoting.conf.EasyGrpcClientConfig;
import com.cht.easygrpc.remoting.conf.EasyGrpcCommonConfig;
import com.cht.easygrpc.remoting.conf.EasyGrpcServerConfig;
import com.cht.easygrpc.runner.RpcRunnerPool;
import com.cht.easygrpc.support.proxy.ProxyFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : chenhaitao934
 * @date : 2:51 下午 2020/10/9
 */
public class EasyGrpcContext {
    private EasyGrpcServerConfig serverConfig;

    private List<EasyGrpcClientConfig> clientConfigs;

    private RpcRunnerPool rpcRunnerPool;

    private EasyGrpcCommonConfig commonConfig;

    private EasyGrpcChannelManager easyGrpcChannelManager;

    private ConfigContext configContext;

    private ProxyFactory proxyFactory;

    private final ThreadLocal<Map> TRACK = ThreadLocal.withInitial(HashMap<String, String>::new);

    public static final String RPC_CONTEXT_KEY_CLIENT_APPID = "appid";
    public static final String RPC_CONTEXT_KEY_CLIENT_SERVICE_NAME = "serviceName";

    private EasyGrpcCircuitBreakerManager circuitBreakerManager;

    public EasyGrpcServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(EasyGrpcServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public String getAppId() {
        return getStorage().get(RPC_CONTEXT_KEY_CLIENT_APPID);
    }

    private Map<String, String> getStorage() {
        return TRACK.get();
    }

    public RpcRunnerPool getRpcRunnerPool() {
        return rpcRunnerPool;
    }

    public void setRpcRunnerPool(RpcRunnerPool rpcRunnerPool) {
        this.rpcRunnerPool = rpcRunnerPool;
    }

    public List<EasyGrpcClientConfig> getClientConfigs() {
        return clientConfigs;
    }

    public void setClientConfigs(List<EasyGrpcClientConfig> clientConfigs) {
        this.clientConfigs = clientConfigs;
    }

    public EasyGrpcCommonConfig getCommonConfig() {
        return commonConfig;
    }

    public void setCommonConfig(EasyGrpcCommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    public EasyGrpcChannelManager getEasyGrpcChannelManager() {
        return easyGrpcChannelManager;
    }

    public void setEasyGrpcChannelManager(EasyGrpcChannelManager easyGrpcChannelManager) {
        this.easyGrpcChannelManager = easyGrpcChannelManager;
    }

    public ConfigContext getConfigContext() {
        return configContext;
    }

    public void setConfigContext(ConfigContext configContext) {
        this.configContext = configContext;
    }

    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public void setProxyFactory(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    public EasyGrpcCircuitBreakerManager getCircuitBreakerManager() {
        return circuitBreakerManager;
    }

    public void setCircuitBreakerManager(EasyGrpcCircuitBreakerManager circuitBreakerManager) {
        this.circuitBreakerManager = circuitBreakerManager;
    }
}
