package com.cht.easygrpc.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : chenhaitao934
 */
public class GenericsHelper {

    public static Class getSuperClassGenericType(Class clazz, int index) {
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            throw new IllegalArgumentException("index "+ (index<0 ? " must > 0 " : " over total arguments"));
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }

    public static Class getSuperClassGenericType(Class clazz) {
        return getSuperClassGenericType(clazz, 0);
    }

    public static Class getMethodGenericReturnType(Method method, int index) {
        Type returnType = method.getGenericReturnType();
        if(returnType instanceof ParameterizedType){
            ParameterizedType type = (ParameterizedType) returnType;
            Type[] typeArguments = type.getActualTypeArguments();
            if (index >= typeArguments.length || index < 0) {
                throw new IllegalArgumentException("index "+ (index<0 ? " must > 0 " : " over total arguments"));
            }
            return (Class)typeArguments[index];
        }
        return Object.class;
    }

    public static Class getMethodGenericReturnType(Method method) {
        return getMethodGenericReturnType(method, 0);
    }


    public static List<Class> getMethodGenericParameterTypes(Method method, int index) {
        List<Class> results = new ArrayList<Class>();
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        if (index >= genericParameterTypes.length ||index < 0) {
            throw new IllegalArgumentException("index "+ (index<0 ? " must > 0 " : " over total arguments"));
        }
        Type genericParameterType = genericParameterTypes[index];
        if(genericParameterType instanceof ParameterizedType){
            ParameterizedType aType = (ParameterizedType) genericParameterType;
            Type[] parameterArgTypes = aType.getActualTypeArguments();
            for(Type parameterArgType : parameterArgTypes){
                Class parameterArgClass = (Class) parameterArgType;
                results.add(parameterArgClass);
            }
            return results;
        }
        return results;
    }

    public static List<Class> getMethodGenericParameterTypes(Method method) {
        return getMethodGenericParameterTypes(method, 0);
    }

    public static Class getFieldGenericType(Field field, int index) {
        Type genericFieldType = field.getGenericType();

        if(genericFieldType instanceof ParameterizedType){
            ParameterizedType aType = (ParameterizedType) genericFieldType;
            Type[] fieldArgTypes = aType.getActualTypeArguments();
            if (index >= fieldArgTypes.length || index < 0) {
                throw new IllegalArgumentException("index "+ (index<0 ? " must > 0 " : " over total arguments"));
            }
            return (Class)fieldArgTypes[index];
        }
        return Object.class;
    }

    public static Class getFieldGenericType(Field field) {
        return getFieldGenericType(field, 0);
    }
}
