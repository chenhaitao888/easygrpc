package com.cht.easygrpc;

import com.cht.easygrpc.config.EasyGrpcConfig;
import com.cht.easygrpc.constant.ExtRpcConfig;
import com.cht.easygrpc.ec.EventCenter;
import com.cht.easygrpc.ec.EventInfo;
import com.cht.easygrpc.exception.EasyGrpcException;
import com.cht.easygrpc.exception.RemotingException;
import com.cht.easygrpc.exception.StartupException;
import com.cht.easygrpc.helper.CollectionHelper;
import com.cht.easygrpc.helper.GenericsHelper;
import com.cht.easygrpc.helper.JsonClientHelper;
import com.cht.easygrpc.helper.NetHelper;
import com.cht.easygrpc.logger.Logger;
import com.cht.easygrpc.logger.LoggerFactory;
import com.cht.easygrpc.registry.EasyGrpcRegistry;
import com.cht.easygrpc.registry.Registry;
import com.cht.easygrpc.remoting.*;
import com.cht.easygrpc.remoting.conf.ConfigContext;
import com.cht.easygrpc.remoting.conf.EasyGrpcClientConfig;
import com.cht.easygrpc.remoting.iface.IServiceInitializer;
import com.cht.easygrpc.runner.RpcRunnerPool;
import com.cht.easygrpc.spi.ServiceProviderInterface;
import com.cht.easygrpc.support.AliveKeeping;
import com.cht.easygrpc.support.instance.Container;
import com.cht.easygrpc.support.instance.EasyGrpcInjector;
import com.cht.easygrpc.support.proxy.ProxyFactory;
import org.apache.log4j.PropertyConfigurator;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author : chenhaitao934
 */
public abstract class AbstractEasyGrpcStarter<Context extends EasyGrpcContext> {

    protected static Logger LOGGER;

    protected Context context;

    protected EasyGrpcConfig grpcConfig;

    protected Class<?> initializer;

    protected Registry registry;

    protected AtomicBoolean started = new AtomicBoolean(false);

    protected Container container = EasyGrpcInjector.getInstance(Container.class);

    protected EventCenter eventCenter = EasyGrpcInjector.getInstance(EventCenter.class);

    protected IServiceInitializer iServiceInitializer;

    protected EasyGrpcRemotingServer remotingServer;

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

        try {
            if(started.compareAndSet(false, true)){

                initConfig();

                initializer();

                beforeRemotingStart();

                initRegistry();

                remotingStart();

                registry();

                afterRemotingStart();

                publishEvent();

                addShutdownHook();

                AliveKeeping.start();

                LOGGER.info("=== Easy Grpc Start success, appId: {} ===",
                        grpcConfig.getCommonConfig().getAppId());

            }

        }catch (Throwable e){
            LOGGER.info("=== Easy Grpc Start failed, appId: {} ===",
                    grpcConfig.getCommonConfig().getAppId(), e);
        }


    }

    protected abstract void publishEvent();


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

                    initProviderAndChannel(clientConfig, registry);

                } catch (Exception e) {
                    throw new EasyGrpcException("after remoting start failure", e);
                }
            });
        }
    }

    private void initProviderAndChannel(EasyGrpcClientConfig clientConfig, Registry registry) {
        context.getEasyGrpcChannelManager().initProvider(clientConfig.getClientName(), registry);
        context.getEasyGrpcChannelManager().initChannel(clientConfig);
    }

    private void createInstance(List<Class<?>> clazz) {
        clazz.forEach(e -> container.createInstance(e, context));
    }

    private void registry() {
        registry.register();
    }

    private void initRegistry() {
        registry = new EasyGrpcRegistry(context);
    }

    private void remotingStart() {
        try {
            remotingServer.start();
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
            context.setCircuitBreakerManager(new EasyGrpcCircuitBreakerManager());
            remotingServer = new EasyGrpcServer(context, iServiceInitializer);
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
        if(null != grpcConfig.getServerConfig()){
            grpcConfig.getServerConfig().setIp(NetHelper.getLocalHost());
        }
        context.setServerConfig(grpcConfig.getServerConfig());
        context.setClientConfigs(clientConfig);
        context.setCommonConfig(grpcConfig.getCommonConfig());
        context.setConfigContext(configContext);
        if(CollectionHelper.isNotEmpty(clientConfig)){
            clientConfig.forEach(e -> configContext.putClientConfig(e));
        }
        String log4jPath = grpcConfig.getLog4jPath();
        if(log4jPath != null){
            PropertyConfigurator.configure(log4jPath);
        }
        LoggerFactory.setLoggerAdapter(grpcConfig.getCommonConfig());
        LOGGER = LoggerFactory.getLogger(AbstractEasyGrpcStarter.class.getName());
    }

    private void addShutdownHook() throws RemotingException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                registry.unRegister();

            } catch (Exception e) {
                LOGGER.error("unregister failure");
                throw new StartupException("unregisterServer server on zookeeper error!", e);
            }
        }));
        remotingServer.shutdown();
    }
}
