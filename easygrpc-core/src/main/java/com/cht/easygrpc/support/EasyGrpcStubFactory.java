package com.cht.easygrpc.support;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.enums.GrpcStubType;
import com.cht.easygrpc.exception.EasyGrpcException;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcStubFactory {

    public static <T> EasyGrpcStub<T> createGrpcStub(Class<T> type, EasyGrpcContext context){
        Integer stubType = context.getConfigContext().getStubType(type.getName());
        if(null == stubType){
            throw new EasyGrpcException(type.getName() + "'s stub type == null");
        }
        if(stubType == GrpcStubType.BLOCK.getCode()){
            return new EasyGrpcBlockStub<>(type, context);
        }else {
            throw new EasyGrpcException(stubType + " not match easy grpc stub");
        }
    }
}
