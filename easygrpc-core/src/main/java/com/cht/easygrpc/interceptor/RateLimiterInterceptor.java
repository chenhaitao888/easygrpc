package com.cht.easygrpc.interceptor;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.support.Invocation;
import com.cht.easygrpc.support.stub.AbstractGrpcStub;

/**
 * @author : chenhaitao934
 */
public class RateLimiterInterceptor extends AbstractInterceptor{

    public RateLimiterInterceptor(EasyGrpcContext context) {
        super(context);
    }

    @Override
    public Object interceptCall(Invocation invocation, AbstractGrpcStub nextStub) throws Exception {

        return null;
    }
}
