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

    public static <T> EasyGrpcStub intercept(AbstractGrpcStub stub,
                                             Class<T> type, EasyGrpcClientInterceptor... interceptors) {
        return intercept(stub, type, Arrays.asList(interceptors));
    }

    public static <T> EasyGrpcStub intercept(AbstractGrpcStub stub, Class<T> type, List<? extends EasyGrpcClientInterceptor> interceptors) {
        Preconditions.checkNotNull(stub, "stub");
        for (EasyGrpcClientInterceptor interceptor : interceptors) {
            stub = new EasyGrpcClientInterceptors.InterceptorEasyGrpcStub(stub, interceptor, type);
        }
        return stub;

    }

    private static class InterceptorEasyGrpcStub extends AbstractGrpcStub {


        public <T> InterceptorEasyGrpcStub(AbstractGrpcStub stub, EasyGrpcClientInterceptor interceptor, Class<T> type){
            super(stub, interceptor, type);
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
