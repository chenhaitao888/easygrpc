package com.cht.easygrpc.interceptor;

import com.cht.easygrpc.EasyGrpcContext;

/**
 * @author : chenhaitao934
 */
public abstract class AbstractInterceptor implements EasyGrpcClientInterceptor{

    protected EasyGrpcContext context;

    public AbstractInterceptor(EasyGrpcContext context) {
        this.context = context;
    }

    @Override
    public EasyGrpcContext getContext() {
        return context;
    }
}
