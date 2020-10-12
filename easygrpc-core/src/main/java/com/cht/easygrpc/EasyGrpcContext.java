package com.cht.easygrpc;

import com.cht.easygrpc.remoting.EasyGrpcChannelManager;
import com.cht.easygrpc.remoting.conf.EasyGrpcClientConfig;
import com.cht.easygrpc.remoting.conf.EasyGrpcCommonConfig;
import com.cht.easygrpc.remoting.conf.EasyGrpcServerConfig;
import com.cht.easygrpc.runner.RpcRunnerPool;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : chenhaitao934
 * @date : 2:51 下午 2020/10/9
 */
public class EasyGrpcContext {
    private EasyGrpcServerConfig serverConfig;

    private EasyGrpcClientConfig clientConfig;

    private RpcRunnerPool rpcRunnerPool;

    private EasyGrpcCommonConfig commonConfig;

    private EasyGrpcChannelManager easyGrpcChannelManager;

    private final ThreadLocal<Map> TRACK = ThreadLocal.withInitial(HashMap<String, String>::new);

    public static final String RPC_CONTEXT_KEY_CLIENT_APPID = "appid";
    public static final String RPC_CONTEXT_KEY_CLIENT_SERVICE_NAME = "serviceName";

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

    public EasyGrpcClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(EasyGrpcClientConfig clientConfig) {
        this.clientConfig = clientConfig;
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
}
