package com.cht.easygrpc.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author : chenhaitao934
 */
public class InvokerInvocationHandler implements InvocationHandler {

    private EasyGrpcStub<?> grpcStub;

    public InvokerInvocationHandler(EasyGrpcStub<?> grpcStub) {
        this.grpcStub = grpcStub;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(grpcStub, args);
        }
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            if ("toString".equals(methodName)) {
                return grpcStub.toString();
            } else if ("hashCode".equals(methodName)) {
                return grpcStub.hashCode();
            }
        } else if (parameterTypes.length == 1 && "equals".equals(methodName)) {
            return grpcStub.equals(args[0]);
        }
        return grpcStub.call(new EasyGrpcInvocation(method, grpcStub.getInterface().getName(), args));
    }
}
