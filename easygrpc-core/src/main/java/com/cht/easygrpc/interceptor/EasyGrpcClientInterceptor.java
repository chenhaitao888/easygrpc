package com.cht.easygrpc.interceptor;

import com.cht.easygrpc.support.Invocation;
import com.cht.easygrpc.support.stub.AbstractGrpcStub;
import com.cht.easygrpc.support.stub.EasyGrpcStub;

/**
 * @author : chenhaitao934
 */
public interface EasyGrpcClientInterceptor {

    Object interceptCall(Invocation invocation, AbstractGrpcStub nextStub) throws Exception;
}
