package com.cht.easygrpc.remoting;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.concurrent.CustomizeThreadPollExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : chenhaitao934
 * @date : 4:48 下午 2020/10/9
 */
public abstract class AbstractRemoting {

    protected ThreadPoolExecutor threadPoolExecutor;

    protected EasyGrpcContext context;

    public AbstractRemoting(EasyGrpcContext context) {
        this.context = context;
        CustomizeThreadPollExecutor executor = new CustomizeThreadPollExecutor(context.getServerConfig().getServiceName(),
                context.getServerConfig().getWorkThreads(),
                context.getServerConfig().getWorkThreads(), 30, context.getServerConfig().getQueueCapacity(),
                false, null);
        threadPoolExecutor = (ThreadPoolExecutor) executor.initializeExecutor(executor, new ThreadPoolExecutor.AbortPolicy());
    }
}
