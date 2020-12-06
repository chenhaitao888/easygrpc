package com.cht.easygrpc.interceptor;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.support.Invocation;
import com.cht.easygrpc.support.stub.AbstractGrpcStub;
import com.cht.easygrpc.support.stub.EasyGrpcStub;
import com.google.common.base.Preconditions;
import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.stub.AbstractStub;

import java.util.Arrays;
import java.util.List;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcClientInterceptors {

    public static EasyGrpcStub intercept(AbstractGrpcStub stub, EasyGrpcClientInterceptor... interceptors) {
        return intercept(stub, Arrays.asList(interceptors));
    }

    public static EasyGrpcStub intercept(AbstractGrpcStub stub, List<? extends EasyGrpcClientInterceptor> interceptors) {
        Preconditions.checkNotNull(stub, "stub");
        for (EasyGrpcClientInterceptor interceptor : interceptors) {
            stub = new EasyGrpcClientInterceptors.InterceptorEasyGrpcStub(stub, interceptor);
        }
        return stub;

    }

    private static class InterceptorEasyGrpcStub extends AbstractGrpcStub {


        public InterceptorEasyGrpcStub(AbstractGrpcStub stub, EasyGrpcClientInterceptor interceptor){
            super(stub, interceptor);
        }

        @Override
        public Object doCall(Invocation invocation) throws Exception {
            return interceptor.interceptCall(invocation, stub);
        }

        @Override
        protected AbstractStub createEasyGrpcServiceStub(Channel manageChannel, Invocation invocation, long timeout) {
            return null;
        }
    }
}
