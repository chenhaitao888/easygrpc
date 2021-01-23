package com.cht.easygrpc.remoting;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.concurrent.CustomizeThreadPollExecutor;
import com.cht.easygrpc.discovery.EasyGrpcNameResolverProvider;
import com.cht.easygrpc.loadbalance.RandomLoadBalancer;
import com.cht.easygrpc.loadbalance.RandomLoadBalancerProvider;
import com.cht.easygrpc.registry.EasyGrpcServiceNode;
import com.cht.easygrpc.registry.Registry;
import com.cht.easygrpc.remoting.conf.EasyGrpcClientConfig;
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

    public void initChannel(EasyGrpcClientConfig clientConfig) {
        checkArgument(clientConfig.getClientName() != null, "serviceName is null");
        ManagedChannel managedChannel = serviceChannelMap.get(clientConfig.getClientName());
        if(managedChannel == null){
            synchronized (serviceChannelMap){
                managedChannel = createChannel(clientConfig);
                serviceChannelMap.put(clientConfig.getClientName(), managedChannel);
            }
        }
    }

    private ManagedChannel createChannel(EasyGrpcClientConfig clientConfig) {
        NameResolverRegistry.getDefaultRegistry().register(providerConcurrentHashMap.get(clientConfig.getClientName()));
        //LoadBalancerRegistry.getDefaultRegistry().register(new EasyGrpcLoadBalanceProvider(serviceName));
        //LoadBalancerRegistry.getDefaultRegistry().register(new RandomLoadBalancer.Provider());
        ManagedChannel channel = ManagedChannelBuilder.forTarget(clientConfig.getClientName())
                .executor(threadPoolExecutor)
                .defaultLoadBalancingPolicy(clientConfig.getLbStrategy())
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
