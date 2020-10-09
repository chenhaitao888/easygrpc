package com.cht.easygrpc.runner;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.EasyGrpcRequest;
import com.cht.easygrpc.EasyGrpcResponse;
import com.cht.easygrpc.concurrent.CustomizeThreadPollExecutor;
import com.cht.easygrpc.exception.NoAvailableWorkThreadException;
import com.cht.easygrpc.remoting.iface.IServiceInitializer;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : chenhaitao934
 * @date : 2:55 下午 2020/10/9
 */
public class RpcRunnerPool {

    private EasyGrpcContext context;

    private ThreadPoolExecutor threadPoolExecutor;

    public RpcRunnerPool(EasyGrpcContext context) {
        this.context = context;

        CustomizeThreadPollExecutor executor = new CustomizeThreadPollExecutor("EasyGrpcRunnerPoll",
                context.getServerConfig().getWorkThreads(),
                context.getServerConfig().getWorkThreads(), 30, context.getServerConfig().getQueueCapacity(),
                false, null);
        threadPoolExecutor = (ThreadPoolExecutor) executor.initializeExecutor(executor, new ThreadPoolExecutor.AbortPolicy());
    }

    public void execute(EasyGrpcRequest request, StreamObserver<EasyGrpcResponse> responseObserver, IServiceInitializer initializer) throws NoAvailableWorkThreadException {
        try {
            threadPoolExecutor.execute(new EasyGrpcRunnable(request, responseObserver, context, initializer));
        } catch (RejectedExecutionException e) {
            throw new NoAvailableWorkThreadException(e);
        }
    }
}
