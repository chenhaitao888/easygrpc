package com.cht.easygrpc;

import com.cht.easygrpc.exception.RemotingException;
import com.cht.easygrpc.remoting.EasyGrpcServer;
import com.cht.easygrpc.remoting.conf.EasyGrpcServerConfig;
import com.cht.easygrpc.remoting.iface.EasyGrpcServiceInitializer;
import com.cht.easygrpc.remoting.iface.EasyGrpcTest;
import com.cht.easygrpc.runner.RpcRunnerPool;
import org.apache.log4j.PropertyConfigurator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author : chenhaitao934
 */
public class ServerStarTest {

    public static void main(String[] args) throws RemotingException {

        String log4jPath = "/Users/chenhaitao/IdeaProjects/EasyGrpc/easygrpc-core/src/main/resources/log4j.properties";
        PropertyConfigurator.configure(log4jPath);
        EasyGrpcContext context = new EasyGrpcContext();
        EasyGrpcServerConfig serverConfig = new EasyGrpcServerConfig();
        serverConfig.setWorkThreads(10);
        serverConfig.setQueueCapacity(100);
        serverConfig.setServiceName("EasyGrpcTest");
        serverConfig.setPort(8888);
        List<Class<?>> interfaces = new ArrayList<>();
        interfaces.add(EasyGrpcTest.class);
        serverConfig.setInterfaces(interfaces);
        context.setServerConfig(serverConfig);
        RpcRunnerPool rpcRunnerPool = new RpcRunnerPool(context);
        context.setRpcRunnerPool(rpcRunnerPool);
        String[] stringSet = {"com.cht.easygrpc.remoting.iface.EasyGrpcTestImpl"};
        EasyGrpcServer grpcServer = new EasyGrpcServer(context);
        grpcServer.start();
    }
}
