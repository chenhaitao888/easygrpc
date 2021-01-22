package com.cht.easygrpc.loadbalance;

import com.cht.easygrpc.helper.SubChannelHelper;
import io.grpc.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.grpc.ConnectivityState.*;
import static io.grpc.ConnectivityState.READY;

/**
 * @author : chenhaitao934
 */
public abstract class AbstractEasyGrpcLoadBalance extends LoadBalancer {

    private final Helper helper;

    private final Map<EquivalentAddressGroup, Subchannel> subchannels =
            new HashMap<>();

    private static final Status EMPTY_OK = Status.OK.withDescription("no subchannels ready");

    private ConnectivityState currentState;

    private AbstractEasyGrpcPicker currentPicker = new EmptyPicker(EMPTY_OK);

    public AbstractEasyGrpcLoadBalance(Helper helper) {
        this.helper = checkNotNull(helper, "helper");;
    }

    @Override
    public void handleResolvedAddresses(ResolvedAddresses resolvedAddresses) {
        List<EquivalentAddressGroup> servers = resolvedAddresses.getAddresses();
        Set<EquivalentAddressGroup> currentAddrs = subchannels.keySet();
        Map<EquivalentAddressGroup, EquivalentAddressGroup> latestAddrs = stripAttrs(servers);
        Set<EquivalentAddressGroup> removedAddrs = setsDifference(currentAddrs, latestAddrs.keySet());
        for (Map.Entry<EquivalentAddressGroup, EquivalentAddressGroup> latestEntry :
                latestAddrs.entrySet()) {
            EquivalentAddressGroup strippedAddressGroup = latestEntry.getKey();
            EquivalentAddressGroup originalAddressGroup = latestEntry.getValue();
            Subchannel existingSubchannel = subchannels.get(strippedAddressGroup);
            Subchannel subchannel;
            if (existingSubchannel != null) {
                subchannel = existingSubchannel;
                SubChannelHelper.updateAttributes(subchannel, originalAddressGroup.getAttributes());
            } else {
                subchannel = SubChannelHelper.createSubChannel(helper, strippedAddressGroup, originalAddressGroup.getAttributes());
                subchannels.put(strippedAddressGroup, subchannel);
                subchannel.start(state -> processSubchannelState(subchannel, state));
            }
            subchannel.requestConnection();
        }

        ArrayList<Subchannel> removedSubchannels = new ArrayList<>();
        for (EquivalentAddressGroup addressGroup : removedAddrs) {
            removedSubchannels.add(subchannels.remove(addressGroup));
        }

        // 更新picker在shutdown subchannel之前，从而减少picker subchannel和shutdown subchannel之间的竞争
        updateBalancingState();

        for (Subchannel removedSubchannel : removedSubchannels) {
            shutdownSubchannel(removedSubchannel);
        }
    }

    @Override
    public void handleNameResolutionError(Status error) {
        updateBalancingState(TRANSIENT_FAILURE,
                currentPicker instanceof AbstractReadyPicker ? currentPicker : new EmptyPicker(error));
    }

    @Override
    public void shutdown() {
        for (Subchannel subchannel : getSubchannels()) {
            shutdownSubchannel(subchannel);
        }
    }

    private void processSubchannelState(Subchannel subchannel, ConnectivityStateInfo stateInfo) {
        if (subchannels.get(stripAttrs(subchannel.getAddresses())) != subchannel) {
            return;
        }
        if (stateInfo.getState() == IDLE) {
            subchannel.requestConnection();
        }
        ConnectivityStateInfo subchannelStateRef = SubChannelHelper.getSubchannelStateInfoRef(subchannel);
        if (subchannelStateRef.getState().equals(TRANSIENT_FAILURE)) {
            if (stateInfo.getState().equals(CONNECTING) || stateInfo.getState().equals(IDLE)) {
                return;
            }
        }
        SubChannelHelper.setStateInfo(subchannel, stateInfo);
        updateBalancingState();
    }

    private void shutdownSubchannel(Subchannel subchannel) {
        subchannel.shutdown();
        SubChannelHelper.setStateInfo(subchannel, ConnectivityStateInfo.forNonError(SHUTDOWN));
    }

    private void updateBalancingState() {
        List<Subchannel> activeList = getSubchannels()
                .stream()
                .filter(channel -> SubChannelHelper.getSubchannelStateInfoRef(channel).getState() == READY)
                .collect(Collectors.toList());
        if (activeList.isEmpty()) {
            boolean isConnecting = false;
            Status aggStatus = EMPTY_OK;
            for (Subchannel subchannel : getSubchannels()) {
                ConnectivityStateInfo stateInfo = SubChannelHelper.getSubchannelStateInfoRef(subchannel);
                /**
                 * IDLE不是由于IDLE_TIMEOUT引起，LB已关闭的话，RRLB将立即请求子信道上的IDLE
                 */
                if (stateInfo.getState() == CONNECTING || stateInfo.getState() == IDLE) {
                    isConnecting = true;
                }
                if (aggStatus == EMPTY_OK || !aggStatus.isOk()) {
                    aggStatus = stateInfo.getStatus();
                }
            }
            // 如果所有子通道均为TRANSIENT_FAILURE,则返回任意子通道关联的状态，否则返回OK
            updateBalancingState(isConnecting ? CONNECTING : TRANSIENT_FAILURE, new EmptyPicker(aggStatus));
        } else {
            updateBalancingState(READY, createPicker(activeList));
        }
    }

    protected abstract AbstractReadyPicker createPicker(List<Subchannel> activeList);

    private void updateBalancingState(ConnectivityState state, AbstractEasyGrpcPicker picker) {
        if (state != currentState || !picker.isEquivalentTo(currentPicker)) {
            helper.updateBalancingState(state, picker);
            currentState = state;
            currentPicker = picker;
        }
    }


    private static Map<EquivalentAddressGroup, EquivalentAddressGroup> stripAttrs(
            List<EquivalentAddressGroup> groupList) {
        Map<EquivalentAddressGroup, EquivalentAddressGroup> addrs = new HashMap<>(groupList.size() * 2);
        for (EquivalentAddressGroup group : groupList) {
            addrs.put(stripAttrs(group), group);
        }
        return addrs;
    }

    private static EquivalentAddressGroup stripAttrs(EquivalentAddressGroup eag) {
        return new EquivalentAddressGroup(eag.getAddresses());
    }

    private Collection<Subchannel> getSubchannels() {
        return subchannels.values();
    }

    private static <T> Set<T> setsDifference(Set<T> a, Set<T> b) {
        Set<T> aCopy = new HashSet<>(a);
        aCopy.removeAll(b);
        return aCopy;
    }
}
