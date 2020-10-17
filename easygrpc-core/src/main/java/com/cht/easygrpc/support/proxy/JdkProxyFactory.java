package com.cht.easygrpc.support.proxy;

import com.cht.easygrpc.support.EasyGrpcStub;
import com.cht.easygrpc.support.InvokerInvocationHandler;

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

}
