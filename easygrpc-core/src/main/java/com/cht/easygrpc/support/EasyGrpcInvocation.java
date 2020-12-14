package com.cht.easygrpc.support;

import com.cht.easygrpc.exception.EasyGrpcException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcInvocation implements Invocation{

    private String methodName;
    private String ifaceName;
    private String serviceName;
    private transient Class<?>[] parameterTypes;

    private transient Class<?> returnType;

    private Method method;

    private Object[] arguments;

    private String reqId;

    private String rpcId;



    public EasyGrpcInvocation(Method method, String ifaceName, Object[] arguments){
        this(method.getName(), ifaceName, arguments);
        this.returnType = method.getReturnType();
        this.method = method;
    }

    public EasyGrpcInvocation(Method method, Map<String, Object> args){
        this.returnType = method.getReturnType();
        this.method = method;
        this.arguments = getArgs(method, args);
    }

    public EasyGrpcInvocation(Method method, Object[] arguments){
        this.returnType = method.getReturnType();
        this.method = method;
        this.arguments = arguments;
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

    @Override
    public String getIfaceMethodKey() {
        return ifaceName + "." + methodName;
    }

    @Override
    public String getRpcId() {
        return rpcId;
    }

    @Override
    public String getReqId() {
        return reqId;
    }

    @Override
    public void setRpcId(String rpcId) {
        this.rpcId = rpcId;
    }

    @Override
    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    @Override
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String getUniqueName() {
        return this.serviceName + "_" + this.ifaceName + "_" + this.methodName;
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


    private Object[] getArgs(Method method, Map<String, Object> args) {
        Parameter[] params = method.getParameters();
        Object[] argArray = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            String para = "arg" + i;
            if (!args.containsKey(para) && !args.containsKey(params[i].getName())) {
                throw new EasyGrpcException("Parameter Map of Method(" + method.getName() + ") doesn't Contain Parameter(" + params[i].getName() + ")!");
            }
            argArray[i] = args.get(para) != null ? args.get(para) : args.get(params[i].getName());
        }
        return argArray;
    }
}
