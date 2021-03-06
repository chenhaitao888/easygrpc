package com.cht.easygrpc.remoting;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.concurrent.CustomizeThreadPollExecutor;
import com.cht.easygrpc.discovery.EasyGrpcNameResolverProvider;
import com.cht.easygrpc.loadbalance.EasyGrpcLoadBalanceProvider;
import com.cht.easygrpc.loadbalance.RandomLoadBalancer;
import com.cht.easygrpc.registry.EasyGrpcRegistry;
import com.cht.easygrpc.registry.EasyGrpcServiceNode;
import com.cht.easygrpc.registry.Registry;
import io.grpc.*;

import java.util.List;
import java.util.Map;
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

    private final ConcurrentHashMap<String/*serviceName*/, EasyGrpcNameResolverProvider> providerConcurrentHashMap =
            new ConcurrentHashMap<>();

    private static final int MAX_INBOUND_SIZE = 100 * 1024 * 1024;

    public EasyGrpcChannelManager(EasyGrpcContext context) {
        CustomizeThreadPollExecutor executor = new CustomizeThreadPollExecutor(context.getServerConfig().getServiceName(),
                context.getServerConfig().getWorkThreads(),
                context.getServerConfig().getWorkThreads(), 30, context.getServerConfig().getQueueCapacity(),
                false, null);
        this.threadPoolExecutor = (ThreadPoolExecutor) executor.initializeExecutor(executor, new ThreadPoolExecutor.AbortPolicy());
        //initProvider(context.getClientConfig().getClientName());
        //initChannel(context.getClientConfig().getClientName());
    }

    public void initProvider(String serviceName, Registry grpcRegistry) {
        checkArgument(serviceName != null, "serviceName is null");
        EasyGrpcServiceNode node = new EasyGrpcServiceNode(grpcRegistry.getServiceData(serviceName));
        EasyGrpcServiceNode.Data serverData = node.getData();
        List<Map<String, Object>> servers = grpcRegistry.assembleServers(serverData);
        EasyGrpcNameResolverProvider resolverProvider = providerConcurrentHashMap.get(serviceName);
        if(resolverProvider == null){
            resolverProvider = new EasyGrpcNameResolverProvider(servers, 0);
            providerConcurrentHashMap.put(serviceName, resolverProvider);
        }
    }

    public void initChannel(String serviceName) {
        checkArgument(serviceName != null, "serviceName is null");
        ManagedChannel managedChannel = serviceChannelMap.get(serviceName);
        if(managedChannel == null){
            synchronized (serviceChannelMap){
                managedChannel = createChannel(serviceName);
                serviceChannelMap.put(serviceName, managedChannel);
            }
        }
    }

    private ManagedChannel createChannel(String serviceName) {
        NameResolverRegistry.getDefaultRegistry().register(providerConcurrentHashMap.get(serviceName));
        //LoadBalancerRegistry.getDefaultRegistry().register(new EasyGrpcLoadBalanceProvider(serviceName));
        LoadBalancerRegistry.getDefaultRegistry().register(new RandomLoadBalancer.Provider());
        ManagedChannel channel = ManagedChannelBuilder.forTarget(serviceName)
                .executor(threadPoolExecutor)
                .defaultLoadBalancingPolicy("customize")
                .usePlaintext()
                .maxInboundMessageSize(MAX_INBOUND_SIZE)
                .build();
        return channel;
    }

    public EasyGrpcNameResolverProvider getResolverProvider(String serviceName){
        return providerConcurrentHashMap.get(serviceName);
    }

    public void putResolverProvider(String serviceName, EasyGrpcNameResolverProvider resolverProvider){
        providerConcurrentHashMap.put(serviceName, resolverProvider);
    }

    public ManagedChannel getManageChannel(String serviceName){
        return serviceChannelMap.get(serviceName);
    }


}
