package com.cht.easygrpc.helper;

import io.grpc.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author : chenhaitao934
 */
public class SubChannelHelper {
    private static final Attributes.Key<Ref<ConnectivityStateInfo>> STATE_INFO_KEY = Attributes.Key.create("state-info");
    private static final Attributes.Key<Ref<Integer>> WEIGHT_KEY = Attributes.Key.create("weight");
    private static final Attributes.Key<Ref<String>> TAG_KEY = Attributes.Key.create("tag");
    private static final Attributes.Key<Ref<String>> REGION_KEY = Attributes.Key.create("region");

    public static LoadBalancer.Subchannel createSubChannel(LoadBalancer.Helper helper,
                                                    EquivalentAddressGroup addressGroup,
                                                    Attributes attributes) {
        final Attributes newAttributes = attributes.toBuilder()
                .set(STATE_INFO_KEY, new Ref<>(ConnectivityStateInfo.forNonError(ConnectivityState.IDLE)))
                .build();
        final LoadBalancer.Subchannel subchannel = helper.createSubchannel(addressGroup, newAttributes);
        subchannel.requestConnection();
        return subchannel;
    }

    public static void updateAttributes(LoadBalancer.Subchannel subchannel, Attributes attributes) {
        setAttributeValue(subchannel, WEIGHT_KEY, attributes);
        setAttributeValue(subchannel, TAG_KEY, attributes);
        setAttributeValue(subchannel, REGION_KEY, attributes);
    }

    private static <T> void setAttributeValue(LoadBalancer.Subchannel subchannel, Attributes.Key<Ref<T>> key, Attributes newAttributes) {
        final Ref<T> newValueRef = newAttributes.get(key);
        if (newValueRef != null) {
            setAttributeValue(subchannel, key, (Attributes) newValueRef.value);
        }
    }

    public static ConnectivityStateInfo getSubchannelStateInfoRef(
            LoadBalancer.Subchannel subchannel) {
        return checkNotNull(subchannel.getAttributes().get(STATE_INFO_KEY).value, "STATE_INFO");
    }

    public static void setStateInfo(LoadBalancer.Subchannel subchannel, ConnectivityStateInfo value){
        setAttributeValue(subchannel, STATE_INFO_KEY, value);
    }

    public static int getWeight(LoadBalancer.Subchannel subchannel) {
        return getAttributeValue(subchannel, WEIGHT_KEY, 0);
    }

    private static <T> T getAttributeValue(LoadBalancer.Subchannel subchannel, Attributes.Key<Ref<T>> key, T defaultValue) {
        final Ref<T> ref = subchannel.getAttributes().get(key);
        return ref == null ? defaultValue : ref.value;
    }

    public static String getTag(LoadBalancer.Subchannel subchannel) {
        return getAttributeValue(subchannel, TAG_KEY, null);
    }

    public static String getRegion(LoadBalancer.Subchannel subchannel) {
        return getAttributeValue(subchannel, REGION_KEY, null);
    }

    private static <T> void setAttributeValue(LoadBalancer.Subchannel subchannel, Attributes.Key<Ref<T>> key, T newValue) {
        final Ref<T> targetRef = subchannel.getAttributes().get(key);
        if (targetRef != null) {
            targetRef.value = newValue;
        }
    }

    static final class Ref<T> {
        T value;

        Ref(T value) {
            this.value = value;
        }
    }
}
