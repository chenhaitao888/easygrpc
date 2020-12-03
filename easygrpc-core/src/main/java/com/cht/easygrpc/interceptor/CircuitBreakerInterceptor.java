package com.cht.easygrpc.interceptor;

import com.cht.easygrpc.support.stub.AbstractGrpcStub;
import io.grpc.*;

import javax.annotation.Nullable;

/**
 * @author : chenhaitao934
 */
public class CircuitBreakerInterceptor implements EasyGrpcClientInterceptor {


    @Override
    public Object interceptCall(AbstractGrpcStub grpcStub) {
        return null;
    }
}
