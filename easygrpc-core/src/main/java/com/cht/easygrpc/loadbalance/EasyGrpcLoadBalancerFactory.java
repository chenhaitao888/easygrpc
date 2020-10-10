package com.cht.easygrpc.loadbalance;

import com.cht.easygrpc.constant.EasyGrpcAttr;
import com.cht.easygrpc.constant.EasyGrpcLS;
import com.google.common.collect.Lists;
import io.grpc.*;

import javax.annotation.Nullable;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.cht.easygrpc.loadbalance.SubchannelGroup.SUBCHANNEL_GROUP_SIZE;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.grpc.ConnectivityState.TRANSIENT_FAILURE;

/**
 * @author : chenhaitao934
 * @date : 3:46 下午 2020/10/10
 */
public class EasyGrpcLoadBalancerFactory extends LoadBalancer.Factory{
    private final String serviceName;
    public EasyGrpcLoadBalancerFactory(String serviceName) {
        this.serviceName = serviceName;
    }


    @Override
    public LoadBalancer newLoadBalancer(LoadBalancer.Helper helper) {
        return null;
    }

    class EasyGrpcLoadBanlancer extends LoadBalancer{
        private final Helper helper;
        private final Map<EquivalentAddressGroup, SubchannelGroup> subchannels = new ConcurrentHashMap<>();
        private int lbStrategy;
        private final Lock lock = new ReentrantLock();


        EasyGrpcLoadBanlancer(Helper helper) {
            this.helper = checkNotNull(helper, "helper");
        }

        @Override
        public void handleResolvedAddresses(ResolvedAddresses resolvedAddresses) {
            try {
                lock.lock();
                List<EquivalentAddressGroup> servers = resolvedAddresses.getAddresses();
                Attributes attributes = resolvedAddresses.getAttributes();
                Set<EquivalentAddressGroup> currentAddrs = subchannels.keySet();
                Set<EquivalentAddressGroup> latestAddrs = stripAttrs(servers);
                Set<EquivalentAddressGroup> addedAddrs = setsDifference(latestAddrs, currentAddrs);
                Set<EquivalentAddressGroup> removedAddrs = setsDifference(currentAddrs, latestAddrs);

                // Shutdown subchannels for removed addresses.
                for (EquivalentAddressGroup addressGroup : removedAddrs) {
                    SubchannelGroup subchannelGroup = subchannels.remove(addressGroup);
                    subchannelGroup.shutdown();
                }

                for (Map.Entry<EquivalentAddressGroup, SubchannelGroup> entry : subchannels.entrySet()) {
                    EquivalentAddressGroup targetAddress = entry.getKey();
                    Attributes newAttributes = getAttributes(servers, targetAddress);
                    entry.getValue().setWeight(getWeightFromAttributes(newAttributes));
                    entry.getValue().setTag(getTagFromAttributes(newAttributes));
                }

                // Create new subchannels for new addresses.
                for (EquivalentAddressGroup addressGroup : addedAddrs) {
                    Attributes newAttributes = getAttributes(servers, addressGroup);
                    SubchannelGroup subchannelGroup = SubchannelGroup.createSubchannelGroup(helper, addressGroup,
                            getWeightFromAttributes(newAttributes), attributes.get(SUBCHANNEL_GROUP_SIZE), getTagFromAttributes(newAttributes));
                    subchannels.put(addressGroup, subchannelGroup);
                }

                Integer strategy = attributes.get(EasyGrpcAttr.GROUP_LBSTRATEGY);
                if (strategy != null) {
                    lbStrategy = strategy;
                }
                updateBalancingState(getAggregatedError());
            } finally {
                lock.unlock();
            }
        }


        private Set<EquivalentAddressGroup> stripAttrs(List<EquivalentAddressGroup> groupList) {
            Set<EquivalentAddressGroup> addrs = new HashSet<>();
            for (EquivalentAddressGroup group : groupList) {
                addrs.add(new EquivalentAddressGroup(group.getAddresses()));
            }
            return addrs;
        }

        private <T> Set<T> setsDifference(Set<T> a, Set<T> b) {
            Set<T> aCopy = new HashSet<>(a);
            aCopy.removeAll(b);
            return aCopy;
        }

        private Attributes getAttributes(List<EquivalentAddressGroup> servers, EquivalentAddressGroup addressGroup) {
            for (EquivalentAddressGroup server : servers) {
                if (equalsAddrs(server, addressGroup)) {
                    return server.getAttributes();
                }
            }
            return null;
        }


        private boolean equalsAddrs(EquivalentAddressGroup thisOne, EquivalentAddressGroup thatOne) {
            List<SocketAddress> addrs = thisOne.getAddresses();
            List<SocketAddress> thatAddrs = thatOne.getAddresses();
            if (addrs.size() != thatAddrs.size()) {
                return false;
            }
            // Avoids creating an iterator on the underlying array list.
            for (int i = 0; i < addrs.size(); i++) {
                if (!addrs.get(i).equals(thatAddrs.get(i))) {
                    return false;
                }
            }
            return true;
        }

        private Integer getWeightFromAttributes(Attributes attributes) {
            if (attributes == null) {
                return 0;
            }
            Integer weight = attributes.get(EasyGrpcAttr.SERVER_WEIGHT);
            return weight == null ? 0 : weight;
        }

        private String getTagFromAttributes(Attributes attributes) {
            if (attributes == null) {
                return null;
            }
            return attributes.get(EasyGrpcAttr.SERVER_TAG);
        }

        /**
         * If all subchannels are TRANSIENT_FAILURE, return the Status associated with an arbitrary
         * subchannel otherwise, return null.
         */
        @Nullable
        private Status getAggregatedError() {
            Status status = null;
            for (SubchannelGroup subchannelGroup : getSubchannels()) {
                for (Subchannel subchannel : subchannelGroup.getSubchannels()) {
                    ConnectivityStateInfo stateInfo = getSubchannelStateInfoRef(subchannel).get();
                    if (stateInfo.getState() != TRANSIENT_FAILURE) {
                        return null;
                    }
                    status = stateInfo.getStatus();
                }
            }
            return status;
        }

        private AtomicReference<ConnectivityStateInfo> getSubchannelStateInfoRef(
                Subchannel subchannel) {
            return checkNotNull(subchannel.getAttributes().get(SubchannelGroup.STATE_INFO), "STATE_INFO");
        }

        /**
         * Updates picker with the list of active subchannels (state == READY).
         */
        private void updateBalancingState(Status error) {
            try {
                lock.lock();
                List<SubchannelGroup> activeSubchannelGroup = Lists.newArrayListWithCapacity(subchannels.size());
                for (SubchannelGroup subchannelGroup : getSubchannels()) {
                    SubchannelGroup filteredSubchannelGroup = subchannelGroup.getActivitySubchannelGroup();
                    if (filteredSubchannelGroup == null) {
                        continue;
                    }
                    activeSubchannelGroup.add(filteredSubchannelGroup);
                }
                helper.updateBalancingState(ConnectivityState.READY, newPicker(activeSubchannelGroup, error));
            } finally {
                lock.unlock();
            }
        }

        Collection<SubchannelGroup> getSubchannels() {
            return subchannels.values();
        }

        @Override
        public void handleNameResolutionError(Status error) {
            try {
                lock.lock();
                updateBalancingState(error);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void shutdown() {
            try {
                lock.lock();
                for (SubchannelGroup subchannel : getSubchannels()) {
                    subchannel.shutdown();
                }
            } finally {
                lock.unlock();
            }
        }

        private SubchannelPicker newPicker(List<SubchannelGroup> list, Status status) {
            switch (lbStrategy) {
                case EasyGrpcLS.RANDOM:
                default:
                    return new RandomPicker(list, status, serviceName);
                case EasyGrpcLS.ROUND_ROBIN:
                    return new RoundRobinPicker(list, status, serviceName);
            }
        }
    }
}
