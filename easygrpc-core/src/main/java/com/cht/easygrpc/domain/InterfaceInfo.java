package com.cht.easygrpc.domain;

import com.cht.easygrpc.exception.EasyGrpcException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : chenhaitao934
 * @date : 2:03 下午 2020/10/9
 */
public class InterfaceInfo {
    private final Class<?> clazz;
    private final Map<String, MethodInfo> methodInfos = new HashMap<>();

    public InterfaceInfo(Class<?> clazz) {
        this.clazz = clazz;
        verifyInterfaceClass();
        initMethodInfos();
    }

    private void verifyInterfaceClass() {
        if (clazz == null) {
            throw new EasyGrpcException("Interface is Null!");
        }
        if (!clazz.isInterface()) {
            throw new EasyGrpcException(clazz.getName() + " isn't an Interface!");
        }
    }

    private void initMethodInfos() {
        for (Method method : clazz.getMethods()) {
            String methodName = method.getName();
            if (methodInfos.containsKey(methodName)) {
                throw new EasyGrpcException("Duplicate Method Name(" + methodName + ") Found in Interface(" + clazz.getName() + ")!");
            }
            methodInfos.put(methodName, new MethodInfo(method));
        }
    }

    public MethodInfo getMethodInfo(String methodName) {
        MethodInfo methodInfo = methodInfos.get(methodName);
        if (methodInfo == null) {
            throw new EasyGrpcException("Method(" + methodName + ") doesn't Exsit in Interface(" + clazz.getName() + ")!");
        }
        return methodInfo;
    }

    public Map<String, MethodInfo> getMethods(){
        return this.methodInfos;
    }
}
