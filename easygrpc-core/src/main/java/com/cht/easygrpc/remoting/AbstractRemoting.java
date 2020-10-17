package com.cht.easygrpc.remoting;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.concurrent.CustomizeThreadPollExecutor;
import com.cht.easygrpc.helper.CollectionHelper;
import com.cht.easygrpc.remoting.conf.EasyGrpcClientConfig;
import com.cht.easygrpc.support.EasyGrpcBlockStub;
import com.cht.easygrpc.support.EasyGrpcStub;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : chenhaitao934
 * @date : 4:48 下午 2020/10/9
 */
public abstract class AbstractRemoting {

    protected ThreadPoolExecutor threadPoolExecutor;

    protected EasyGrpcContext context;

    protected final Map<String, String> serviceIface = new ConcurrentHashMap<>();

    public AbstractRemoting(EasyGrpcContext context) {
        this.context = context;
        CustomizeThreadPollExecutor executor = new CustomizeThreadPollExecutor(context.getServerConfig().getServiceName(),
                context.getServerConfig().getWorkThreads(),
                context.getServerConfig().getWorkThreads(), 30, context.getServerConfig().getQueueCapacity(),
                false, null);
        threadPoolExecutor = (ThreadPoolExecutor) executor.initializeExecutor(executor, new ThreadPoolExecutor.AbortPolicy());
        initServiceIface(context);
    }

    private void initServiceIface(EasyGrpcContext context) {
        EasyGrpcClientConfig clientConfig = context.getClientConfig();
        List<String> ifaceNames = clientConfig.getIfaceNames();
        if(CollectionHelper.isNotEmpty(ifaceNames)){
            ifaceNames.forEach(e -> serviceIface.put(e, clientConfig.getClientName()));
        }
    }

}
