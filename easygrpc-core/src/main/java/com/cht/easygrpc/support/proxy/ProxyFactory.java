package com.cht.easygrpc.support.proxy;

import com.cht.easygrpc.constant.ExtRpcConfig;
import com.cht.easygrpc.exception.EasyGrpcException;
import com.cht.easygrpc.spi.SPI;
import com.cht.easygrpc.support.EasyGrpcStub;

/**
 * @author : chenhaitao934
 */
@SPI(key = ExtRpcConfig.RPC_PROXY, value = "jdk")
public interface ProxyFactory {

    <T> T getProxy(EasyGrpcStub<T> stub) throws EasyGrpcException;
}
