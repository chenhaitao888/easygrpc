package com.cht.easygrpc.remoting.iface;

import com.cht.easygrpc.exception.EasyGrpcException;
import com.cht.easygrpc.exception.ServiceException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * @author : chenhaitao934
 * @date : 4:08 下午 2020/10/9
 */
public class InvokeImplHandler implements IInvokeHandler {

    private final Object impl;

    public InvokeImplHandler(Object impl) {
        this.impl = impl;
    }

    @Override
    public Object invoke(Method method, Map<String, Object> args, Map<String, String> metas) throws ServiceException {
        try {
            return method.invoke(impl, getArgs(method, args));
        } catch (IllegalAccessException e) {
            throw new ServiceException("InvokeImplHandler.invoke throws IllegalAccessException",e);
        } catch (InvocationTargetException e) {
            throw new ServiceException("InvokeImplHandler.invoke throws InvocationTargetException", e.getCause());
        }
    }

    @Override
    public Object invoke(Method method, Object... args) throws ServiceException {
        try {
            return method.invoke(impl, args);
        } catch (IllegalAccessException e) {
            throw new ServiceException("InvokeImplHandler.invoke throws IllegalAccessException",e);
        } catch (InvocationTargetException e) {
            throw new ServiceException("InvokeImplHandler.invoke throws InvocationTargetException", e.getCause());
        }
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
