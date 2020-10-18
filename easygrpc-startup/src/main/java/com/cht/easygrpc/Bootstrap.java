package com.cht.easygrpc;

import com.cht.easygrpc.registry.EasyGrpcRegistry;
import com.cht.easygrpc.remoting.conf.EasyGrpcCommonConfig;
import com.cht.easygrpc.remoting.conf.EasyGrpcServerConfig;
import com.cht.easygrpc.support.AliveKeeping;

/**
 * @author : chenhaitao934
 * @date : 5:21 下午 2020/10/12
 */
public class Bootstrap {

    public static void main(String[] args) {
        EasyGrpcContext context = new EasyGrpcContext();
        EasyGrpcCommonConfig commonConfig = new EasyGrpcCommonConfig();
        commonConfig.setAppId("EasyGrpcTest");
        commonConfig.setRegistryAddress("192.168.1.6:2181");
        context.setCommonConfig(commonConfig);
        EasyGrpcServerConfig serverConfig = new EasyGrpcServerConfig();
        serverConfig.setIp("192.168.1.6");
        serverConfig.setPort(8888);
        serverConfig.setServiceName("EasyGrpcTest");
        context.setServerConfig(serverConfig);
        EasyGrpcRegistry registry = new EasyGrpcRegistry(context);
        registry.register();
        AliveKeeping.start();
    }
}
