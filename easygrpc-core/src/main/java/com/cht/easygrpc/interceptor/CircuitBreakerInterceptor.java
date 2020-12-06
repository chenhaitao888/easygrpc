package com.cht.easygrpc.interceptor;

import com.cht.easygrpc.support.Invocation;
import com.cht.easygrpc.support.stub.AbstractGrpcStub;
import com.cht.easygrpc.support.stub.EasyGrpcStub;
import io.grpc.*;

import javax.annotation.Nullable;

/**
 * @author : chenhaitao934
 */
public class CircuitBreakerInterceptor implements EasyGrpcClientInterceptor {


    @Override
    public Object interceptCall(Invocation invocation, AbstractGrpcStub nextStub) throws Exception {
        // todo circuit breaker
        Object result = nextStub.doCall(invocation);
        return result;
    }
}
