package com.cht.easygrpc.support;

import java.lang.reflect.Method;

/**
 * @author : chenhaitao934
 */
public interface Invocation {
    String getMethodName();
    String getIfaceName();
    String getServiceName();
    Class<?>[] getParameterTypes();
    Object[] getArguments();
    Class<?> getReturnType();

    Method getMethod();

    String getIfaceMethodKey();

    String getRpcId();

    String getReqId();

    void setRpcId(String rpcId);

    void setReqId(String reqId);

    void setServiceName(String serviceName);

    String getUniqueName();
}
