package com.cht.easygrpc.loadbalance;

import io.grpc.LoadBalancer;
import io.grpc.LoadBalancerProvider;

import java.util.List;

/**
 * @author : chenhaitao934
 */
public class RandomLoadBalancerProvider extends LoadBalancerProvider {
    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public int getPriority() {
        return 6;
    }

    @Override
    public String getPolicyName() {
        return "random";
    }

    @Override
    public LoadBalancer newLoadBalancer(LoadBalancer.Helper helper) {
        return new AbstractEasyGrpcLoadBalance(helper) {
            @Override
            protected AbstractReadyPicker createPicker(List<Subchannel> activeList) {
                return new EasyGrpcRandomPicker(activeList);
            }
        };
    }
}
