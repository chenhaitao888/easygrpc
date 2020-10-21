package com.cht.easygrpc.config;

import com.cht.easygrpc.remoting.conf.AbstracConfig;
import com.cht.easygrpc.remoting.conf.EasyGrpcClientConfig;
import com.cht.easygrpc.remoting.conf.EasyGrpcCommonConfig;
import com.cht.easygrpc.remoting.conf.EasyGrpcServerConfig;

import java.util.List;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcConfig extends AbstracConfig<EasyGrpcConfig> {

    private EasyGrpcClientConfig clientConfig;

    private EasyGrpcServerConfig serverConfig;

    private List<EasyGrpcCommonConfig> commonConfig;

    public EasyGrpcClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(EasyGrpcClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public EasyGrpcServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(EasyGrpcServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public List<EasyGrpcCommonConfig> getCommonConfig() {
        return commonConfig;
    }

    public void setCommonConfig(List<EasyGrpcCommonConfig> commonConfig) {
        this.commonConfig = commonConfig;
    }
}
