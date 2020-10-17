package com.cht.easygrpc.support;

import com.cht.easygrpc.exception.EasyGrpcException;

/**
 * @author : chenhaitao934
 */
public class WrapGrpcStub<T> implements EasyGrpcStub<T>{
    private EasyGrpcStub<T> invoker;

    public WrapGrpcStub(EasyGrpcStub<T> invoker) {
        this.invoker = invoker;
    }

    @Override
    public Class<T> getInterface() {
        return invoker.getInterface();
    }

    @Override
    public T call(Invocation grpcInvocation) throws EasyGrpcException {
        return invoker.call(grpcInvocation);
    }
}
