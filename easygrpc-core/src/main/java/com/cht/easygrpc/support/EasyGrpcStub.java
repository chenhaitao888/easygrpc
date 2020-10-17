package com.cht.easygrpc.support;

import com.cht.easygrpc.exception.EasyGrpcException;

/**
 * @author : chenhaitao934
 */
public interface EasyGrpcStub<T> {

    Class<T> getInterface();

    T call(Invocation grpcInvocation) throws EasyGrpcException;
}
