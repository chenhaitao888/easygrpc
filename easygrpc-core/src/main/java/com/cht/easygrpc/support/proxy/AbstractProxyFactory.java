package com.cht.easygrpc.support.proxy;

import com.cht.easygrpc.exception.EasyGrpcException;
import com.cht.easygrpc.support.stub.EasyGrpcStub;

import java.util.HashSet;
import java.util.Set;

/**
 * @author : chenhaitao934
 */
public abstract class AbstractProxyFactory implements ProxyFactory {


    @Override
    public <T> T getProxy(EasyGrpcStub<T> stub) throws EasyGrpcException {
        Set<Class<?>> interfaces = new HashSet<>();
        interfaces.add(stub.getInterface());
        return getProxy(stub, interfaces.toArray(new Class<?>[0]));
    }

    public abstract <T> T getProxy(EasyGrpcStub<T> stub, Class<?>[] ifaces);
}
