package com.cht.easygrpc;

import com.cht.easygrpc.discovery.EasyGrpcNameResolverProvider;
import com.cht.easygrpc.helper.JsonClientHelper;
import com.cht.easygrpc.helper.JsonHelper;
import com.cht.easygrpc.registry.EasyGrpcRegistry;
import com.cht.easygrpc.remoting.EasyGrpcChannelManager;
import com.cht.easygrpc.remoting.conf.ConfigContext;
import com.cht.easygrpc.remoting.conf.EasyGrpcClientConfig;
import com.cht.easygrpc.remoting.conf.EasyGrpcCommonConfig;
import com.cht.easygrpc.remoting.conf.EasyGrpcServerConfig;
import com.cht.easygrpc.remoting.iface.EasyGrpcTest;
import com.cht.easygrpc.support.*;
import com.cht.easygrpc.support.proxy.JdkProxyFactory;
import com.cht.easygrpc.support.proxy.ProxyFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * @author : chenhaitao934
 */
public class ProxyTest {

    public static void main(String[] args) throws InterruptedException, ClassNotFoundException, URISyntaxException {

        EasyGrpcContext context = new EasyGrpcContext();

        EasyGrpcCommonConfig commonConfig = new EasyGrpcCommonConfig();
        commonConfig.setAppId("EasyGrpcTest");
        commonConfig.setRegistryAddress("172.17.210.89:2181");
        context.setCommonConfig(commonConfig);
        EasyGrpcServerConfig serverConfig = new EasyGrpcServerConfig();
        serverConfig.setIp("172.17.210.89");
        serverConfig.setPort(8888);
        serverConfig.setServiceName("EasyGrpcTest");
        context.setServerConfig(serverConfig);
        ConfigContext configContext = new ConfigContext();
        EasyGrpcClientConfig clientConfig = new EasyGrpcClientConfig();
        clientConfig.setClientName("EasyGrpcTest");
        List<String> list = new ArrayList<>();
        list.add("com.cht.easygrpc.remoting.iface.EasyGrpcTest");
        clientConfig.setIfaceNames(list);
        JsonClientHelper.add(clientConfig.getClientName(), Collections.singletonList(Class.forName("com.cht.easygrpc.remoting.iface.EasyGrpcTest")));
        configContext.putClientConfig(clientConfig);
        context.setClientConfig(clientConfig);
        context.setConfigContext(configContext);
        EasyGrpcChannelManager channelManager = new EasyGrpcChannelManager(context);
        List<Map<String, Object>> initAddress = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("host", "172.17.210.89");
        map.put("port", 8888);
        initAddress.add(map);

        EasyGrpcNameResolverProvider provider = new EasyGrpcNameResolverProvider(initAddress, 0);
        channelManager.putResolverProvider(clientConfig.getClientName(), provider);
        channelManager.initChannel(clientConfig.getClientName());
        context.setEasyGrpcChannelManager(channelManager);
        /*EasyGrpcRegistry registry = new EasyGrpcRegistry(context);
        registry.register();

        AliveKeeping.start();*/
        ProxyFactory jdkProxyFactory = new JdkProxyFactory();
        EasyGrpcTest easyGrpcTest;
        EasyGrpcStub<EasyGrpcTest> grpcStub = EasyGrpcStubFactory.createGrpcStub(EasyGrpcTest.class, context);
        easyGrpcTest = jdkProxyFactory.getProxy(grpcStub);
        String adad = easyGrpcTest.hello("adad");
        System.out.println(adad);


    }
}
