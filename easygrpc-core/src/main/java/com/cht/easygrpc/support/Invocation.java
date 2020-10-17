package com.cht.easygrpc.support;

import java.lang.reflect.Method;

/**
 * @author : chenhaitao934
 */
public interface Invocation {
    String getMethodName();
    String getIfaceName();
    Class<?>[] getParameterTypes();
    Object[] getArguments();
    Class<?> getReturnType();

    Method getMethod();
}
