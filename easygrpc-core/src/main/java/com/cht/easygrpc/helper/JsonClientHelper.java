package com.cht.easygrpc.helper;

import com.cht.easygrpc.domain.ServiceInfo;
import com.fasterxml.jackson.databind.JavaType;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : chenhaitao934
 * @date : 2:02 下午 2020/10/9
 */
public class JsonClientHelper {
    private final static Map<String, ServiceInfo> serviceInfos = new ConcurrentHashMap<>();

    public static void add(String clientName, List<Class<?>> interfaces) {
        serviceInfos.put(clientName, new ServiceInfo(clientName, interfaces));
    }

    public static JavaType getReturnJavaType(String serviceName, Method method) {
        return serviceInfos.get(serviceName).getInterfaceInfo(method.getDeclaringClass().getName()).getMethodInfo(method.getName()).getReturnJavaType();
    }

}
