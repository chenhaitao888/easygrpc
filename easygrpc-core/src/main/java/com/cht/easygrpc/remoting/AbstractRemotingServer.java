package com.cht.easygrpc.remoting;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.EasyGrpcRequest;
import com.cht.easygrpc.EasyGrpcResponse;
import com.cht.easygrpc.EasyGrpcServiceGrpc;
import com.cht.easygrpc.exception.NoAvailableWorkThreadException;
import com.cht.easygrpc.exception.RemotingException;
import com.cht.easygrpc.remoting.conf.EasyGrpcServerConfig;
import com.cht.easygrpc.remoting.iface.IInvokeHandler;
import com.cht.easygrpc.remoting.iface.IServiceInitializer;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;

import java.util.Map;

/**
 * @author : chenhaitao934
 * @date : 2:11 下午 2020/10/9
 */
public abstract class AbstractRemotingServer extends AbstractRemoting implements EasyGrpcRemotingServer{

    protected IServiceInitializer initializer;

    protected AbstractRemotingServer(EasyGrpcContext context, IServiceInitializer initializer) {
        super(context);
        this.context = context;
        this.initializer = initializer;
    }



    @Override
    public void start() throws RemotingException {
        serverStart(context, initializer);
    }

    @Override
    public void shutdown() throws RemotingException {

    }

    protected void awaitTermination(Server server) throws InterruptedException {
        if(server != null){
            server.awaitTermination();
        }
    }

    protected abstract void serverStart(EasyGrpcContext context, IServiceInitializer initializer) throws RemotingException;
    protected abstract void serverShutdown() throws RemotingException;

    class EasyGrpcProcessor extends EasyGrpcServiceGrpc.EasyGrpcServiceImplBase {

        @Override
        public void call(EasyGrpcRequest request, StreamObserver<EasyGrpcResponse> responseObserver) {
            try {
                context.getRpcRunnerPool().execute(request, responseObserver, initializer);
            } catch (NoAvailableWorkThreadException e) {
                e.printStackTrace();
            }
        }
    }
}
