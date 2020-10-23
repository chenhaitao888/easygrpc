package com.cht.easygrpc;

import com.cht.easygrpc.config.EasyGrpcConfig;
import com.cht.easygrpc.exception.EasyGrpcException;
import com.cht.easygrpc.exception.RemotingException;
import com.cht.easygrpc.helper.CollectionHelper;
import com.cht.easygrpc.helper.GenericsHelper;
import com.cht.easygrpc.helper.JsonClientHelper;
import com.cht.easygrpc.helper.NetHelper;
import com.cht.easygrpc.registry.EasyGrpcRegistry;
import com.cht.easygrpc.registry.Registry;
import com.cht.easygrpc.remoting.EasyGrpcChannelManager;
import com.cht.easygrpc.remoting.EasyGrpcServer;
import com.cht.easygrpc.remoting.conf.ConfigContext;
import com.cht.easygrpc.remoting.conf.EasyGrpcClientConfig;
import com.cht.easygrpc.runner.RpcRunnerPool;
import com.cht.easygrpc.spi.ServiceProviderInterface;
import com.cht.easygrpc.support.AliveKeeping;
import com.cht.easygrpc.support.instance.Container;
import com.cht.easygrpc.support.instance.EasyGrpcInjector;
import com.cht.easygrpc.support.proxy.ProxyFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author : chenhaitao934
 */
public abstract class AbstractEasyGrpcStarter<Context extends EasyGrpcContext> {

    protected Context context;

    protected EasyGrpcConfig grpcConfig;

    protected Class<?> initializer;

    protected Registry registry;

    protected AtomicBoolean started = new AtomicBoolean(false);

    protected Container container = EasyGrpcInjector.getInstance(Container.class);

    public AbstractEasyGrpcStarter() {
        this.context = getContext();
        grpcConfig = loadConfig();
    }

    protected abstract EasyGrpcConfig loadConfig();


    private Context getContext() {
        try {
            return ((Class<Context>)
                    GenericsHelper.getSuperClassGenericType(this.getClass(), 0))
                    .newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void start(){

        if(started.compareAndSet(false, true)){
            initConfig();

            initializer();

            beforeRemotingStart();

            remotingStart();

            initRegistry();

            registry();

            afterRemotingStart();

            AliveKeeping.start();

        }
    }

    private void afterRemotingStart() {
        List<EasyGrpcClientConfig> clientConfigs = grpcConfig.getClientConfig();
        if(CollectionHelper.isNotEmpty(clientConfigs)){
            clientConfigs.forEach(clientConfig -> {
                try {
                    List<Class<?>> clazz = clientConfig.getIfaceNames().stream().map(e -> {
                        try {
                            return Class.forName(e);
                        } catch (ClassNotFoundException classNotFoundException) {
                            throw new EasyGrpcException(e);
                        }
                    }).collect(Collectors.toList());
                    JsonClientHelper.add(clientConfig.getClientName(), clazz);

                    createInstance(clazz);

                    context.getEasyGrpcChannelManager().initProvider(clientConfig.getClientName(), registry);

                } catch (Exception e) {
                    throw new EasyGrpcException("after remoting start failure", e);
                }
            });
        }

    }

    private void createInstance(List<Class<?>> clazz) {
        clazz.forEach(e -> container.createInstance(e));
    }

    private void registry() {
        registry.register();
    }

    private void initRegistry() {
        registry = new EasyGrpcRegistry(context);
    }

    private void remotingStart() {
        EasyGrpcServer grpcServer = new EasyGrpcServer(context);
        try {
            grpcServer.start();
        } catch (RemotingException e) {
            throw new EasyGrpcException("start remote server failure", e);
        }
    }

    private void clientStart() {

    }


    private void beforeRemotingStart() {
        try {
            RpcRunnerPool runnerPool = new RpcRunnerPool(context);
            EasyGrpcChannelManager channelManager = new EasyGrpcChannelManager(context);
            ProxyFactory proxyFactory = ServiceProviderInterface.load(ProxyFactory.class, grpcConfig.getCommonConfig());
            context.setRpcRunnerPool(runnerPool);
            context.setProxyFactory(proxyFactory);
            context.setEasyGrpcChannelManager(channelManager);
        } catch (Exception e) {
            throw new EasyGrpcException("before remoting start failure", e);
        }
    }

    private void initializer() {
        context.getServerConfig().setInitializer(initializer);
    }

    private void initConfig() {
        ConfigContext configContext = new ConfigContext();
        List<EasyGrpcClientConfig> clientConfig = grpcConfig.getClientConfig();
        grpcConfig.getServerConfig().setIp(NetHelper.getLocalHost());
        context.setServerConfig(grpcConfig.getServerConfig());
        context.setClientConfigs(clientConfig);
        context.setCommonConfig(grpcConfig.getCommonConfig());
        context.setConfigContext(configContext);
        if(CollectionHelper.isNotEmpty(clientConfig)){
            clientConfig.forEach(e -> configContext.putClientConfig(e));
        }

    }
}
