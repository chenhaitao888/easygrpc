package com.cht.easygrpc.loadbalance;

import io.grpc.*;

import java.util.*;

import static io.grpc.ConnectivityState.*;

/**
 * @author : chenhaitao934
 */
public class RandomLoadBalancer extends LoadBalancer {

    LoadBalancer.Helper helper;

    private final Map<EquivalentAddressGroup, Subchannel> subchannels =
            new HashMap<>();
    static final Attributes.Key<Ref<ConnectivityStateInfo>> STATE_INFO =
            Attributes.Key.create("state-info");

    public RandomLoadBalancer(LoadBalancer.Helper helper) {
        this.helper = helper;
    }
    @Override
    public void handleResolvedAddresses(ResolvedAddresses resolvedAddresses) {
        List<EquivalentAddressGroup> servers = resolvedAddresses.getAddresses();
        for(EquivalentAddressGroup server : servers){
            List<EquivalentAddressGroup> serverSingletonListt = Collections.singletonList(server);
            Subchannel exists = subchannels.getOrDefault(server, null);
            if(null != exists){
                exists.updateAddresses(serverSingletonListt);
                continue;
            }
            Attributes.Builder subchannelAttrs = Attributes.newBuilder()
                    .set(STATE_INFO,
                            new Ref<>(ConnectivityStateInfo.forNonError(IDLE)));
            final Subchannel subchannel = helper.createSubchannel(CreateSubchannelArgs.newBuilder()
                    .setAddresses(serverSingletonListt)
                    .setAttributes(subchannelAttrs.build())
                    .build());
            subchannels.put(server, subchannel);
            subchannel.start(state -> {
                for(Map.Entry<EquivalentAddressGroup, Subchannel> entry : subchannels.entrySet()){
                    if(subchannel == entry.getValue()){
                        if (state.getState() == SHUTDOWN) {
                            subchannels.remove(entry.getKey());
                        }
                        if (state.getState() == IDLE) {
                            subchannel.requestConnection();
                        }
                        subchannel.getAttributes().get(STATE_INFO).value = state;
                        updateBalancingState();
                        return;
                    }
                }
            });
            subchannel.requestConnection();
        }
        updateBalancingState();
    }
    @Override
    public void handleNameResolutionError(Status error) {
        shutdown();
        helper.updateBalancingState(TRANSIENT_FAILURE, new SubchannelPicker() {
            @Override
            public PickResult pickSubchannel(PickSubchannelArgs args) {
                return PickResult.withError(error);
            }
        });
    }

    private  void updateBalancingState(){
        boolean ready = true;
        for(Subchannel subchannel : this.subchannels.values()){
            if(subchannel.getAttributes().get(STATE_INFO).value.getState() != READY){
                helper.updateBalancingState(CONNECTING, new RandomSubchannelPick(subchannels.values()));
                return;
            }
        }
        helper.updateBalancingState(ConnectivityState.READY, new RandomSubchannelPick(subchannels.values()));
    }

    @Override
    public void shutdown() {
        for(Iterator<Map.Entry<EquivalentAddressGroup, Subchannel>> itr = subchannels.entrySet().iterator(); itr.hasNext();){
            Map.Entry<EquivalentAddressGroup, Subchannel> e = itr.next();
            e.getValue().shutdown();
            itr.remove();
        }

    }

    class RandomSubchannelPick extends SubchannelPicker{
        Subchannel[] subchannels;
        Random random = new Random(System.currentTimeMillis());

        public RandomSubchannelPick(Collection<Subchannel> subchannels) {
            this.subchannels = subchannels.stream().toArray(Subchannel[]::new);
        }

        @Override
        public PickResult pickSubchannel(PickSubchannelArgs args) {
            int idx = random.nextInt(subchannels.length);
            return PickResult.withSubchannel(subchannels[idx]);
        }
    }

    public static class Provider extends LoadBalancerProvider{

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public int getPriority() {
            return 100;
        }

        @Override
        public String getPolicyName() {
            return "random";
        }

        @Override
        public LoadBalancer newLoadBalancer(LoadBalancer.Helper helper) {
            return new RandomLoadBalancer(helper);
        }
    }

    static final class Ref<T> {
        T value;

        Ref(T value) {
            this.value = value;
        }
    }
}
