package com.cht.easygrpc.remoting;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.EasyGrpcRequest;
import com.cht.easygrpc.EasyGrpcResponse;
import com.cht.easygrpc.EasyGrpcServiceGrpc;
import com.cht.easygrpc.annotation.EasyGrpcService;
import com.cht.easygrpc.domain.ServiceInfo;
import com.cht.easygrpc.exception.EasyGrpcException;
import com.cht.easygrpc.exception.NoAvailableWorkThreadException;
import com.cht.easygrpc.exception.RemotingException;
import com.cht.easygrpc.exception.StartupException;
import com.cht.easygrpc.helper.CollectionHelper;
import com.cht.easygrpc.remoting.conf.EasyGrpcServerConfig;
import com.cht.easygrpc.remoting.iface.IServiceInitializer;
import com.cht.easygrpc.support.EasyGrpcStub;
import com.google.common.collect.Lists;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author : chenhaitao934
 * @date : 2:11 下午 2020/10/9
 */
public abstract class AbstractRemotingServer extends AbstractRemoting implements EasyGrpcRemotingServer{

    protected IServiceInitializer initializer;
    protected ServiceInfo serviceInfo;
    protected Map<String, EasyGrpcStub> serviceStubMap = new ConcurrentHashMap<>();

    protected AbstractRemotingServer(EasyGrpcContext context) {
        super(context);
        this.context = context;
        if(this.initializer == null){
            this.initializer = init(context.getServerConfig().getInitializer());
        }
        initializer.init(context.getServerConfig().getServicePackages(),
                context.getServerConfig().getServiceImplPackages(), context.getProxyFactory());
        initIfaces(context.getServerConfig());

        serviceInfo = new ServiceInfo(context.getServerConfig().getServiceName(), context.getServerConfig().getInterfaces());
        genServiceStubs(context.getServerConfig().getInterfaces());
    }

    private void genServiceStubs(List<Class<?>> interfaces) {
        if (!CollectionHelper.isNotEmpty(interfaces)) {
            throw new EasyGrpcException("interfaces is empty.");
        }
        interfaces.forEach(iface -> {
            EasyGrpcStub easyGrpcStub = genServiceStub(iface,
                    EasyGrpcService.class);
            if(null != easyGrpcStub){
                serviceStubMap.put(iface.getName(), easyGrpcStub);
            }
        });
    }

    private EasyGrpcStub genServiceStub(Class<?> iface, Class<? extends Annotation> annotation) {
        EasyGrpcStub easyGrpcStub = initializer.serviceStub(iface, annotation);
        if(easyGrpcStub != null){
            return easyGrpcStub;
        }
        return initializer.getStub(iface);
    }

    private void initIfaces(EasyGrpcServerConfig serverConfig) {
        Set<Class<?>> ifaces = initializer.getService();
        serverConfig.setInterfaces(Lists.newArrayList(ifaces).stream().filter(Objects::nonNull).collect(Collectors.toList()));
    }

    private IServiceInitializer init(Class<?> initializer) {
        if(initializer == null){
            throw new EasyGrpcException("initializer == null");
        }
        try {
            return (IServiceInitializer) initializer.newInstance();
        } catch (ReflectiveOperationException | ClassCastException e) {
            throw new StartupException("Invalid Initializer Class(" + initializer.getName() + ")!", e);
        }
    }


    @Override
    public void start() throws RemotingException {
        serverStart();
    }

    @Override
    public void shutdown(Server server) throws RemotingException {
        serverShutdown(server);
    }

    protected void awaitTermination(Server server) throws InterruptedException {
        if(server != null){
            server.awaitTermination();
        }
    }

    protected abstract void serverStart() throws RemotingException;
    protected abstract void serverShutdown(Server server) throws RemotingException;

    class EasyGrpcProcessor extends EasyGrpcServiceGrpc.EasyGrpcServiceImplBase {


        @Override
        public void call(EasyGrpcRequest request, StreamObserver<EasyGrpcResponse> responseObserver) {
            try {
                context.getRpcRunnerPool().execute(request, responseObserver, serviceInfo, serviceStubMap);
            } catch (NoAvailableWorkThreadException e) {
                throw new EasyGrpcException(e);
            }
        }
    }
}
