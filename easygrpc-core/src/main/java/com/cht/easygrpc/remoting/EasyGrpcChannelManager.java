package com.cht.easygrpc.remoting;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.concurrent.CustomizeThreadPollExecutor;
import io.grpc.ManagedChannel;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author : chenhaitao934
 * @date : 5:32 下午 2020/10/10
 */
public class EasyGrpcChannelManager {

    private ThreadPoolExecutor threadPoolExecutor;

    private final ConcurrentHashMap<String/*serviceName*/, ManagedChannel> serviceChannelMap =
            new ConcurrentHashMap<>();

    public EasyGrpcChannelManager(ThreadPoolExecutor threadPoolExecutor, EasyGrpcContext context) {
        CustomizeThreadPollExecutor executor = new CustomizeThreadPollExecutor(context.getServerConfig().getServiceName(),
                context.getServerConfig().getWorkThreads(),
                context.getServerConfig().getWorkThreads(), 30, context.getServerConfig().getQueueCapacity(),
                false, null);
        this.threadPoolExecutor = (ThreadPoolExecutor) executor.initializeExecutor(executor, new ThreadPoolExecutor.AbortPolicy());

        initChannel(context.getClientConfig().getClientName());
    }

    private void initChannel(String serviceName) {
        checkArgument(serviceName != null, "serviceName is null");
        synchronized (this){

        }
    }


}
