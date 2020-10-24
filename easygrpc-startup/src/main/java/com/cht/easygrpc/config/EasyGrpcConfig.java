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

    private List<EasyGrpcClientConfig> clientConfig;

    private EasyGrpcServerConfig serverConfig;

    private EasyGrpcCommonConfig commonConfig;

    private String log4jPath;

    public List<EasyGrpcClientConfig> getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(List<EasyGrpcClientConfig> clientConfig) {
        this.clientConfig = clientConfig;
    }

    public EasyGrpcServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(EasyGrpcServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public EasyGrpcCommonConfig getCommonConfig() {
        return commonConfig;
    }

    public void setCommonConfig(EasyGrpcCommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }

    public String getLog4jPath() {
        return log4jPath;
    }

    public void setLog4jPath(String log4jPath) {
        this.log4jPath = log4jPath;
    }
}
