package com.cht.easygrpc.loadbalance;

import com.cht.easygrpc.helper.CollectionHelper;
import com.cht.easygrpc.helper.StringHelper;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import io.grpc.Attributes;
import io.grpc.ConnectivityStateInfo;
import io.grpc.EquivalentAddressGroup;
import io.grpc.LoadBalancer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.grpc.ConnectivityState.*;

/**
 * @author : chenhaitao934
 * 服务端连接池，一个SubchannelGroup中的所有subchannel连接到同一个服务端节点
 */
public class SubchannelGroup {
    public static final Attributes.Key<AtomicReference<ConnectivityStateInfo>> STATE_INFO = Attributes.Key.create("state-info");
    public static final Attributes.Key<Integer> SUBCHANNEL_GROUP_SIZE = Attributes.Key.create("subchannelGroupSize");

    /**
     * 权重值，用于LB按权重选取服务节点
     */
    private int weight;

    /**
     * 节点标签，根据context中的tag选择匹配服务节点
     */
    private String tag = "";

    /**
     * 是否包含空闲连接
     */
    private boolean readyState;


    private EquivalentAddressGroup address;
    /**
     * 一组Subchannel
     */
    private final List<LoadBalancer.Subchannel> subchannels;

    private AtomicInteger index = new AtomicInteger(0);

    public SubchannelGroup(List<LoadBalancer.Subchannel> subchannels, int weight, EquivalentAddressGroup address, String tag, boolean readyState) {
        this.subchannels = subchannels;
        this.weight = weight;
        this.address = address;
        if (!StringHelper.isEmpty(tag)) {
            this.tag = tag;
        }
        this.readyState = readyState;
    }

    public static SubchannelGroup createSubchannelGroup(LoadBalancer.Helper helper, EquivalentAddressGroup addressGroup, int weight, Integer size, String tag) {
        checkNotNull(helper, "helper");
        checkNotNull(addressGroup, "addressGroup");
        int groupSize = size == null ? 1 : size;

        Attributes subchannelAttrs = Attributes.newBuilder()
                .set(STATE_INFO, new AtomicReference<>(ConnectivityStateInfo.forNonError(IDLE)))
                .build();

        List<LoadBalancer.Subchannel> subchannels = Lists.newArrayListWithCapacity(groupSize);
        for (int i = 0; i < groupSize; i++) {
            LoadBalancer.Subchannel subchannel = checkNotNull(helper.createSubchannel(addressGroup, subchannelAttrs), "subchannel");
            subchannels.add(subchannel);
            subchannel.requestConnection();
        }
        return new SubchannelGroup(subchannels, weight, addressGroup, tag, true);
    }

    /**
     * 轮询选择
     * @return
     */
    public LoadBalancer.Subchannel pickSubchannel() {
        int i = index.accumulateAndGet(subchannels.size(), (prev, size) -> prev + 1 >= size ? 0 : prev + 1);
        return subchannels.get(i);
    }

    /**
     * 选择group内状态ready的subchannels引用
     */
    public SubchannelGroup getActivitySubchannelGroup() {
        List<LoadBalancer.Subchannel> readySubchannels = new ArrayList<LoadBalancer.Subchannel>(subchannels.size());
        List<LoadBalancer.Subchannel> idleSubchannels = new ArrayList<LoadBalancer.Subchannel>(subchannels.size());
        for (LoadBalancer.Subchannel subchannel : subchannels) {
            if (getSubchannelStateInfoRef(subchannel).get().getState() == READY) {
                readySubchannels.add(subchannel);
            } else if (getSubchannelStateInfoRef(subchannel).get().getState() == IDLE || getSubchannelStateInfoRef(subchannel).get().getState() == CONNECTING){
                idleSubchannels.add(subchannel);
            }
        }

        if (CollectionHelper.isNotEmpty(readySubchannels)) {
            return new SubchannelGroup(readySubchannels, this.weight, address, tag, true);
        }
        if (CollectionHelper.isNotEmpty(idleSubchannels)) {
            return new SubchannelGroup(idleSubchannels, this.weight, address, tag, false);
        }
        return null;
    }

    private static AtomicReference<ConnectivityStateInfo> getSubchannelStateInfoRef(
            LoadBalancer.Subchannel subchannel) {
        return checkNotNull(subchannel.getAttributes().get(STATE_INFO), "STATE_INFO");
    }


    public void shutdown() {
        if (CollectionHelper.isEmpty(subchannels)) {
            return;
        }
        subchannels.forEach(LoadBalancer.Subchannel::shutdown);
    }

    public boolean contains(LoadBalancer.Subchannel subchannel) {
        if (subchannel == null || CollectionHelper.isEmpty(subchannels)) {
            return false;
        }
        return subchannels.contains(subchannel);
    }

    public List<LoadBalancer.Subchannel> getSubchannels() {
        return subchannels;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean getReadyState() {
        return readyState;
    }

    public void setReadyState(boolean readyState) {
        this.readyState = readyState;
    }

    public EquivalentAddressGroup getAddress() {
        return address;
    }

    public String getHost() {
        return ((InetSocketAddress)address.getAddresses().get(0)).getAddress().toString().substring(1);
    }

    public int getPort() {
        return ((InetSocketAddress)address.getAddresses().get(0)).getPort();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SubchannelGroup that = (SubchannelGroup) o;

        if (weight != that.weight) {
            return false;
        }
        if (tag != null ? !tag.equals(that.tag) : that.tag != null) {
            return false;
        }
        if (subchannels == that.subchannels) {
            return true;
        }
        if (subchannels == null  || that.subchannels == null) {
            return false;
        }
        return (subchannels.size() == (that.subchannels.size()))
                && Objects.equal(subchannels.get(0).getAddresses(), that.subchannels.get(0).getAddresses());
    }

    @Override
    public int hashCode() {
        int result = weight;
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        result = 31 * result + (subchannels != null ? subchannels.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SubchannelGroup{" +
                "weight=" + weight +
                ", tag='" + tag + '\'' +
                ", readyState=" + readyState +
                ", address=" + address +
                '}';
    }
}
