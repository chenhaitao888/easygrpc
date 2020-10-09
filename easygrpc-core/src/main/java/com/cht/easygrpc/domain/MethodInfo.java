package com.cht.easygrpc.domain;

import com.cht.easygrpc.helper.JacksonHelper;
import com.cht.easygrpc.stream.EasyGrpcStreamObserver;
import com.fasterxml.jackson.databind.JavaType;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : chenhaitao934
 * @date : 1:53 下午 2020/10/9
 */
public class MethodInfo {

    private final Method method;
    private final Map<String, ParamInfo> paramInfos;
    private final Map<String, Object> defaultArgs;
    private final JavaType returnJavaType;

    public MethodInfo(Method method) {
        this.method = method;
        this.paramInfos = genParamInfos(method);
        this.defaultArgs = genDefaultArgs();
        this.returnJavaType = genReturnJavaType(method);
    }

    private Map<String, ParamInfo> genParamInfos(Method method) {
        Map<String, ParamInfo> paramInfos = new HashMap<>();
        Parameter[] params = method.getParameters();
        for (int i = 0; i < params.length; i++) {
            //GEN PARAS, JUST
            paramInfos.put("arg" + i, new ParamInfo(params[i], i, method));
            paramInfos.put(params[i].getName(), new ParamInfo(params[i], i, method));
        }
        return paramInfos;
    }

    private Map<String, Object> genDefaultArgs() {
        Map<String, Object> defaultArgs = new HashMap<>();
        for (Map.Entry<String, ParamInfo> entry : paramInfos.entrySet()) {
            defaultArgs.put(entry.getKey(), entry.getValue().getDefaultValue());
        }
        return defaultArgs;
    }

    private JavaType genReturnJavaType(Method method) {
        if (method.getReturnType().equals(EasyGrpcStreamObserver.class)) {
            if (method.getParameters()[0].getParameterizedType() instanceof ParameterizedTypeImpl) {
                return JacksonHelper.genJavaType(((ParameterizedTypeImpl) method.getParameters()[0].getParameterizedType()).getActualTypeArguments()[0]);
            } else {
                return JacksonHelper.genJavaType(Object.class);
            }
        }
        return JacksonHelper.genJavaType(method.getGenericReturnType());
    }

    public Method getMethod() {
        return method;
    }

    public String getIface() {
        return method.getDeclaringClass().getName();
    }

    public Map<String, ParamInfo> getParamInfos() {
        return paramInfos;
    }

    public Map<String, Object> getDefaultArgs() {
        return new HashMap<String, Object>(defaultArgs);
    }

    public JavaType getReturnJavaType() {
        return returnJavaType;
    }
}
