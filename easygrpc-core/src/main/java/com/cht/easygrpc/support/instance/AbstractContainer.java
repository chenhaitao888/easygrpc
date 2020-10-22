package com.cht.easygrpc.support.instance;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.enums.GrpcStubType;
import com.cht.easygrpc.exception.EasyGrpcException;
import com.cht.easygrpc.support.EasyGrpcStub;
import com.cht.easygrpc.support.EasyGrpcStubFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : chenhaitao934
 */
public abstract class AbstractContainer implements Container{

    protected Map<Class<?> , EasyGrpcContext> stubType = new ConcurrentHashMap<>();


    protected <T> T genIntance(Class<T> clazz){
        EasyGrpcContext context = stubType.get(clazz);
        if(context == null){
            throw new EasyGrpcException(clazz.getSimpleName() + "'s context == null");
        }
        EasyGrpcStub<T> grpcStub = EasyGrpcStubFactory.createGrpcStub(clazz, context);
        return context.getProxyFactory().getProxy(grpcStub);
    }
}
