package com.cht.easygrpc.stream;

import com.cht.easygrpc.exception.UnknownGenericTypeException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author : chenhaitao934
 */
public abstract class AbstractEasyGrpcStreamObserver<V> implements EasyGrpcStreamObserver<V>{


    @Override
    public Type getReturnType() {
        return getGenericType();
    }

    private Type getGenericType() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return parameterizedType.getActualTypeArguments()[0];
        }
        throw new UnknownGenericTypeException();
    }
}
