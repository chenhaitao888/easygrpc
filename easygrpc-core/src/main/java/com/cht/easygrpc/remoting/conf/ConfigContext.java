package com.cht.easygrpc.remoting.conf;

import com.cht.easygrpc.exception.EasyGrpcException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : chenhaitao934
 */
public class ConfigContext {
    private final Map<String, EasyGrpcClientConfig> clientConfs = new ConcurrentHashMap<>();

    private static Map<String, EasyGrpcMethodConfig> methodConfigMap = new ConcurrentHashMap<>();

    private static Map<String, Integer> stub = new ConcurrentHashMap<>();

    private static boolean isServer = false;

    public EasyGrpcClientConfig getClientConfig(String clientName) {
        EasyGrpcClientConfig clientConfig = clientConfs.get(clientName);
        if (clientConfig == null) {
            throw new EasyGrpcException(String.format("clientConfig is null. serviceName:%s.", clientName));
        }
        return clientConfig;
    }

    public void putClientConfig(EasyGrpcClientConfig clientConfig){
        clientConfs.put(clientConfig.getClientName(), clientConfig);
    }

    private String getMethodKey(String serviceName, String ifaceName, String methodName) {
        return serviceName + "_" + ifaceName + "_" + methodName;
    }

    public EasyGrpcMethodConfig getMethodConfig(String clientName, String iface, String method) {
        EasyGrpcMethodConfig methodConfig = methodConfigMap.get(getMethodKey(clientName, iface, method));
        if (methodConfig != null) {
            return methodConfig;
        }
        return methodConfigMap.computeIfAbsent(clientName, key -> {
            EasyGrpcClientConfig clientConfig = getClientConfig(key);
            return EasyGrpcMethodConfig.newInstance(iface, method, clientConfig);
        });
    }

    public void putStubType(String iface, Integer type){
        stub.put(iface, type);
    }

    public Integer getStubType(String iface){
        return stub.get(iface);
    }

    public static boolean isServer() {
        return isServer;
    }
}
