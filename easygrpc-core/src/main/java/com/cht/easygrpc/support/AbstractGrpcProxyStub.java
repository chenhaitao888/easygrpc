package com.cht.easygrpc.support;

import com.cht.easygrpc.exception.EasyGrpcException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author : chenhaitao934
 */
public abstract class AbstractGrpcProxyStub<T> implements EasyGrpcStub<T>{

    private final T proxy;
    private final Class<T> type;

    public AbstractGrpcProxyStub(T proxy, Class<T> type) {
        if (proxy == null) {
            throw new EasyGrpcException("proxy == null");
        }
        if (type == null) {
            throw new EasyGrpcException("interface == null");
        }
        if (!type.isInstance(proxy)) {
            throw new EasyGrpcException(proxy.getClass().getName() + " not implement interface " + type);
        }
        this.proxy = proxy;
        this.type = type;
    }

    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public Object call(Invocation invocation) throws EasyGrpcException {
        try {
            Object value = doCall(proxy, invocation.getMethod(), invocation.getArguments());
            return value;
        } catch (Throwable e) {
            throw new EasyGrpcException("Failed to invoke remote proxy method " + invocation.getMethodName() + ", " +
                    "cause: " + e.getMessage(), e);
        }
    }

    protected abstract Object doCall(T proxy, Method method, Object[] arguments) throws Throwable;
}
