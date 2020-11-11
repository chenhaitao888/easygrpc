package com.cht.easygrpc.support.proxy;

import com.cht.easygrpc.exception.EasyGrpcException;
import com.cht.easygrpc.support.*;
import com.cht.easygrpc.support.stub.AbstractGrpcProxyStub;
import com.cht.easygrpc.support.stub.EasyGrpcStub;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author : chenhaitao934
 */
public class JdkProxyFactory extends AbstractProxyFactory{


    @Override
    public <T> T getProxy(EasyGrpcStub<T> stub, Class<?>[] ifaces) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), ifaces,
                new InvokerInvocationHandler(stub));
    }

    @Override
    public <T> EasyGrpcStub<T> getStub(T proxy, Class<T> type) throws EasyGrpcException {
        return new AbstractGrpcProxyStub<T>(proxy, type) {
            @Override
            protected Object doCall(T proxy, Method method, Object[] arguments) throws Throwable {
                return method.invoke(proxy, arguments);
            }
        };
    }
}
