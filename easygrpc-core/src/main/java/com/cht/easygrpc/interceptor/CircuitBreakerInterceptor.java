package com.cht.easygrpc.interceptor;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.constant.ExtRpcConfig;
import com.cht.easygrpc.ec.EventCenter;
import com.cht.easygrpc.ec.EventInfo;
import com.cht.easygrpc.remoting.EasyGrpcCircuitBreakerManager;
import com.cht.easygrpc.support.Invocation;
import com.cht.easygrpc.support.instance.EasyGrpcInjector;
import com.cht.easygrpc.support.stub.AbstractGrpcStub;
import com.cht.easygrpc.support.stub.EasyGrpcStub;
import io.grpc.*;

import javax.annotation.Nullable;

/**
 * @author : chenhaitao934
 */
public class CircuitBreakerInterceptor implements EasyGrpcClientInterceptor {

    protected EventCenter eventCenter = EasyGrpcInjector.getInstance(EventCenter.class);

    private EasyGrpcCircuitBreakerManager circuitBreakerManager;

    public CircuitBreakerInterceptor(EasyGrpcContext context) {
        this.circuitBreakerManager = context.getCircuitBreakerManager();
    }

    @Override
    public Object interceptCall(Invocation invocation, AbstractGrpcStub nextStub) throws Exception {
        // todo circuit breaker

        Object result = null;
        try {
            result = nextStub.doCall(invocation);
        } catch (Exception e) {
            sendCallFailure(invocation);
        }
        return result;
    }

    private void sendCallFailure(Invocation invocation) {
        eventCenter.publishSync(new EventInfo(ExtRpcConfig.CLIENT_CALL_FAILURE));
    }
}
