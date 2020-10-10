package com.cht.easygrpc.discovery;

import com.cht.easygrpc.constant.EasyGrpcAttr;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;

import java.util.Collections;
import java.util.List;

import static com.cht.easygrpc.loadbalance.SubchannelGroup.SUBCHANNEL_GROUP_SIZE;

/**
 * @author : chenhaitao934
 * @date : 2:54 下午 2020/10/10
 */
public class EasyGrpcNameResolver extends NameResolver {
    // 负载策略
    private int lbStrategy;
    private List<EquivalentAddressGroup> servers;

    private Listener2 listener;

    public EasyGrpcNameResolver() {
    }

    public EasyGrpcNameResolver(int lbStrategy, List<EquivalentAddressGroup> servers) {
        this.lbStrategy = lbStrategy;
        this.servers = servers;
    }

    public void init(int lbStrategy, List<EquivalentAddressGroup> initServices) {
        this.lbStrategy = lbStrategy;
        this.servers = initServices;
    }

    @Override
    public String getServiceAuthority() {
        return "";
    }

    @Override
    public void start(Listener2 listener) {
        this.listener = listener;
        resolve(lbStrategy);
    }

    private void resolve(int lbStrategy) {
        Attributes.Builder attributesBuilder = Attributes.newBuilder();
        attributesBuilder.set(SUBCHANNEL_GROUP_SIZE, 5);
        attributesBuilder.set(EasyGrpcAttr.GROUP_LBSTRATEGY, lbStrategy);

        if (listener != null) {
            listener.onResult(ResolutionResult.newBuilder()
                    .setAddresses(servers)
                    .setAttributes(attributesBuilder.build())
                    .build());
        }
    }

    @Override
    public void refresh() {
        super.refresh();
        resolve(lbStrategy);
    }

    public void refreshWithServers(int lbStrategy, List<EquivalentAddressGroup> servers) {
        this.servers = servers;
        this.lbStrategy = lbStrategy;
        resolve(lbStrategy);
    }

    @Override
    public void shutdown() {

    }
}
