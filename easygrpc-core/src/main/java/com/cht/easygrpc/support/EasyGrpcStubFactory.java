package com.cht.easygrpc.support;

import com.cht.easygrpc.EasyGrpcContext;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcStubFactory {

    public static <T> EasyGrpcStub<T> createGrpcStub(Class<T> type, EasyGrpcContext context){
        return new EasyGrpcBlockStub<>(type, context);
    }
}
