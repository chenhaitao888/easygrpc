package com.cht.easygrpc.remoting;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.EasyGrpcRequest;
import com.cht.easygrpc.EasyGrpcResponse;
import com.cht.easygrpc.EasyGrpcServiceGrpc;
import com.cht.easygrpc.exception.NoAvailableWorkThreadException;
import com.cht.easygrpc.exception.RemotingException;
import com.cht.easygrpc.remoting.iface.IServiceInitializer;
import io.grpc.Server;
import io.grpc.stub.StreamObserver;

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
                context.getRpcRunnerPool().execute(request, responseObserver, initializer);
            } catch (NoAvailableWorkThreadException e) {
                e.printStackTrace();
            }
        }
    }
}
