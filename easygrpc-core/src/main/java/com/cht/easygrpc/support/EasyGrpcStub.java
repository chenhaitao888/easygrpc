package com.cht.easygrpc.support;

import com.cht.easygrpc.constant.ExtRpcConfig;
import com.cht.easygrpc.exception.EasyGrpcException;
import com.cht.easygrpc.spi.SPI;

/**
 * @author : chenhaitao934
 */
public interface EasyGrpcStub<T> {

    Class<T> getInterface();

    Object call(Invocation grpcInvocation) throws EasyGrpcException;
}
