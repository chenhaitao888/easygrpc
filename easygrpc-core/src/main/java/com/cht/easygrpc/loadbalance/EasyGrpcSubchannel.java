package com.cht.easygrpc.loadbalance;

import com.cht.easygrpc.helper.SubChannelHelper;
import io.grpc.ConnectivityStateInfo;
import io.grpc.LoadBalancer;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcSubchannel {

    private LoadBalancer.Subchannel subchannel;
    private int weight;
    private ConnectivityStateInfo state;
    private String tag;
    private String region;


    EasyGrpcSubchannel(LoadBalancer.Subchannel subchannel) {
        this.subchannel = subchannel;
        this.weight = SubChannelHelper.getWeight(subchannel);
        this.state = SubChannelHelper.getSubchannelStateInfoRef(subchannel);
        this.tag = SubChannelHelper.getTag(subchannel);
        this.region = SubChannelHelper.getRegion(subchannel);
    }

    public LoadBalancer.Subchannel getSubchannel() {
        return subchannel;
    }

    public void setSubchannel(LoadBalancer.Subchannel subchannel) {
        this.subchannel = subchannel;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public ConnectivityStateInfo getState() {
        return state;
    }

    public void setState(ConnectivityStateInfo state) {
        this.state = state;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
