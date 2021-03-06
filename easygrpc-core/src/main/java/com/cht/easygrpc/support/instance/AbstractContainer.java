package com.cht.easygrpc.support.instance;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.exception.EasyGrpcException;
import com.cht.easygrpc.interceptor.CircuitBreakerInterceptor;
import com.cht.easygrpc.interceptor.EasyGrpcClientInterceptors;
import com.cht.easygrpc.support.stub.AbstractGrpcStub;
import com.cht.easygrpc.support.stub.EasyGrpcStub;
import com.cht.easygrpc.support.stub.EasyGrpcStubFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : chenhaitao934
 */
public abstract class AbstractContainer implements Container{

    protected final Map<Class<?> , EasyGrpcContext> stubType = new ConcurrentHashMap<>();


    protected <T> T genIntance(Class<T> clazz){
        EasyGrpcContext context = stubType.get(clazz);
        if(context == null){
            throw new EasyGrpcException(clazz.getSimpleName() + "'s context == null");
        }
        EasyGrpcStub<T> grpcStub = EasyGrpcStubFactory.createGrpcStub(clazz, context);
        EasyGrpcStub<T> intercept = EasyGrpcClientInterceptors.intercept((AbstractGrpcStub) grpcStub,
                grpcStub.getInterface(), new CircuitBreakerInterceptor(context));
        return context.getProxyFactory().getProxy(intercept);
    }

    @Override
    public <T> void bindContext(Class<T> clazz, EasyGrpcContext context) {
        if(null == stubType.get(clazz)){
            if(context == null){
                throw new EasyGrpcException(clazz.getSimpleName() + "'s context == null");
            }
            stubType.put(clazz, context);
        }
    }

    protected <T> T genStreamInstance(Class<T> clazz){
        EasyGrpcContext context = stubType.get(clazz);
        if(context == null){
            throw new EasyGrpcException(clazz.getSimpleName() + "'s context == null");
        }
        EasyGrpcStub<T> grpcStreamStub = EasyGrpcStubFactory.createGrpcStreamStub(clazz, context);
        return context.getProxyFactory().getProxy(grpcStreamStub);
    }
}
