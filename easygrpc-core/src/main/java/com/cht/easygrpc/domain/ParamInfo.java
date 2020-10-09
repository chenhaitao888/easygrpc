package com.cht.easygrpc.domain;

import com.cht.easygrpc.helper.JacksonHelper;
import com.cht.easygrpc.stream.EasyGrpcStreamObserver;
import com.fasterxml.jackson.databind.JavaType;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author : chenhaitao934
 * @date : 1:54 下午 2020/10/9
 */
public class ParamInfo {

    private final int index;
    private final JavaType javaType;
    private final Object defaultValue;
    private final Method method;

    public ParamInfo(Parameter param, int index, Method method) {
        this.index = index;
        this.method = method;
        this.javaType = getJavaType(param);
        this.defaultValue = genDefaultValue(javaType.getRawClass());
    }

    private JavaType getJavaType(Parameter param) {
        if (param.getType().equals(EasyGrpcStreamObserver.class)) {
            if (method.getGenericReturnType() instanceof ParameterizedTypeImpl) {
                return JacksonHelper.genJavaType(((ParameterizedTypeImpl) method.getGenericReturnType()).getActualTypeArguments()[0]);
            } else {
                return JacksonHelper.genJavaType(Object.class);
            }
        }
        return JacksonHelper.genJavaType(param.getParameterizedType());
    }

    private Object genDefaultValue(Class<?> clazz) {
        if (clazz == boolean.class) {
            return false;
        } else if (clazz == char.class) {
            return (char) 0;
        } else if (clazz == byte.class) {
            return (byte) 0;
        } else if (clazz == short.class) {
            return (short) 0;
        } else if (clazz == int.class) {
            return (int) 0;
        } else if (clazz == long.class) {
            return (long) 0;
        } else if (clazz == float.class) {
            return (float) 0;
        } else if (clazz == double.class) {
            return (double) 0;
        } else {
            return null;
        }
    }

    public int getIndex() {
        return index;
    }

    public JavaType getJavaType() {
        return javaType;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
