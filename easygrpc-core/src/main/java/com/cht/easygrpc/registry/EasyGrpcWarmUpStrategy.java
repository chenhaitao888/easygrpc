package com.cht.easygrpc.registry;

import com.cht.easygrpc.support.stub.EasyGrpcStub;

/**
 * @author : chenhaitao934
 */
public interface EasyGrpcWarmUpStrategy {

    default WarmUp defaultWarmUp() {
        return WarmUp.defaultWarmUp;
    }
}
