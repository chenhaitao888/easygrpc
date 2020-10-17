package com.cht.easygrpc.support;

import java.lang.reflect.Method;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcInvocation implements Invocation{

    private String methodName;
    private String ifaceName;
    private transient Class<?>[] parameterTypes;

    private transient Class<?> returnType;

    private Method method;

    private Object[] arguments;

    public EasyGrpcInvocation(Method method, String serviceName, Object[] arguments){
        this(method.getName(), serviceName, arguments);
        this.returnType = method.getReturnType();
        this.method = method;
    }

    public EasyGrpcInvocation(String methodName, String ifaceName, Object[] arguments) {
        this.methodName = methodName;
        this.ifaceName = ifaceName;
        this.arguments = arguments;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getIfaceName() {
        return ifaceName;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public Class<?> getReturnType() {
        return returnType;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes == null ? new Class<?>[0] : parameterTypes;
    }




    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setIfaceName(String ifaceName) {
        this.ifaceName = ifaceName;
    }
}
