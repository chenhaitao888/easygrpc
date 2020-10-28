package com.cht.easygrpc.discovery;

import com.cht.easygrpc.constant.EasyGrpcAttr;
import com.cht.easygrpc.helper.CollectionHelper;
import com.google.common.collect.Lists;
import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author : chenhaitao934
 * @date : 4:20 下午 2020/10/10
 */
public class EasyGrpcNameResolverProvider extends NameResolverProvider {
    private List<Map<String, Object>> initAddress;
    private List<EquivalentAddressGroup> equivalentAddressGroups;
    private int lbStrategy;
    private EasyGrpcNameResolver nameResolver = new EasyGrpcNameResolver();

    public EasyGrpcNameResolverProvider(List<Map<String, Object>> initAddress, int lbStrategy) {
        this.initAddress = initAddress;
        this.lbStrategy = lbStrategy;
        equivalentAddressGroups = transInitAddresses();
    }


    @Override
    public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
        nameResolver.init(lbStrategy, equivalentAddressGroups);
        return nameResolver;
    }

    private List<EquivalentAddressGroup> transInitAddresses() {
        if (CollectionHelper.isEmpty(initAddress)) {
            return Lists.newArrayList();
        }
        List<EquivalentAddressGroup> servers = new CopyOnWriteArrayList<>();
        for (Map<String, Object> address : initAddress) {
            SocketAddress socketAddress = new InetSocketAddress((String) address.get("host"), (int) address.get("port"));

            Attributes.Builder attributesBuilder = Attributes.newBuilder();
            attributesBuilder.set(EasyGrpcAttr.SERVER_WEIGHT, (int) address.getOrDefault("weight", 0));
            attributesBuilder.set(EasyGrpcAttr.SERVER_TAG, (String) address.get("tag"));

            EquivalentAddressGroup server = new EquivalentAddressGroup(socketAddress, attributesBuilder.build());
            servers.add(server);
        }
        return servers;
    }

    public void refreshServerList(int lbStrategy, List<Map<String, Object>> servers){
        this.initAddress = servers;
        equivalentAddressGroups = transInitAddresses();
        nameResolver.refreshWithServers(lbStrategy, equivalentAddressGroups);
    }


    @Override
    protected boolean isAvailable() {
        return true;
    }

    @Override
    protected int priority() {
        return 0;
    }

    @Override
    public String getDefaultScheme() {
        return null;
    }
}
