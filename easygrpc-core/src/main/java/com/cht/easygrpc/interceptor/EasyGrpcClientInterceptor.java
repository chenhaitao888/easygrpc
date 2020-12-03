package com.cht.easygrpc.interceptor;

import com.cht.easygrpc.support.stub.AbstractGrpcStub;

/**
 * @author : chenhaitao934
 */
public interface EasyGrpcClientInterceptor {

    Object interceptCall(AbstractGrpcStub grpcStub);
}
