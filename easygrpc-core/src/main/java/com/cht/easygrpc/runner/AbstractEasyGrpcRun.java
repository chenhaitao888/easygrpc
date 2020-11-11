package com.cht.easygrpc.runner;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.EasyGrpcRequest;
import com.cht.easygrpc.domain.InterfaceInfo;
import com.cht.easygrpc.domain.MethodAliasInfo;
import com.cht.easygrpc.domain.MethodInfo;
import com.cht.easygrpc.domain.ServiceInfo;
import com.cht.easygrpc.exception.EasyGrpcException;
import com.cht.easygrpc.helper.EasyRpcParseHelper;
import com.cht.easygrpc.helper.JacksonHelper;
import com.cht.easygrpc.stream.EasyGrpcStreamObserver;
import com.cht.easygrpc.support.EasyGrpcInvocation;
import com.cht.easygrpc.support.stub.EasyGrpcStub;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.HashMap;
import java.util.Map;

import static com.cht.easygrpc.constant.LogConstants.TAG_METHOD;
import static com.cht.easygrpc.constant.LogConstants.TAG_SERVICE;

/**
 * @author : chenhaitao934
 */
public abstract class AbstractEasyGrpcRun {

    protected ServiceInfo serviceInfo;

    protected Map<String, EasyGrpcStub> serviceStubMap;

    protected EasyGrpcContext context;

    protected Map<String, MethodAliasInfo> methodAliasInfoMap = new HashMap<>();

    protected MethodInfo getMethodInfo(String iface, String method) {
        final MethodInfo methodInfo = getAliasMethod(iface, method);
        if (methodInfo != null) {
            return methodInfo;
        }
        InterfaceInfo interfaceInfo = serviceInfo.getInterfaceInfo(iface);
        return interfaceInfo.getMethodInfo(method);
    }

    private MethodInfo getAliasMethod(String iface, String method) {
        final MethodAliasInfo methodAliasInfo = methodAliasInfoMap.get(getIfaceMethodKey(iface, method));
        if (methodAliasInfo == null || !methodAliasInfo.isEnable()) {
            return null;
        }

        final String aliasIface = methodAliasInfo.getAliasIface();
        final String aliasMethod = methodAliasInfo.getAliasMethod();
        try {
            InterfaceInfo interfaceInfo = serviceInfo.getInterfaceInfo(aliasIface);
            final MethodInfo methodInfo = interfaceInfo.getMethodInfo(aliasMethod);
            if (methodInfo != null) {
                return methodInfo;
            }
        } catch (EasyGrpcException ex) {
            logForAliasNotFound(iface, method, aliasIface, aliasMethod);
        }
        return null;
    }

    private void logForAliasNotFound(String iface, String method, String aliasIface, String aliasMethod) {
        Map<String, Object> condition = initCondition();
        condition.put("iface", iface);
        condition.put(TAG_SERVICE, getServiceName());
        condition.put(TAG_METHOD, method);

        final String message = String.format(
                "alias method can not found: %s.%s(%s.%s)",
                iface, method,
                aliasIface, aliasMethod
        );
        condition.put("error", message);

        // 添加日志 todo
    }

    private String getServiceName() {
        return context.getServerConfig().getServiceName();
    }

    private  Map<String, Object> initCondition() {
        Map<String, Object> condition = new HashMap<>();
        condition.put("service", serviceInfo.getName());
        condition.put("clientAppId", context.getAppId());
        return condition;
    }

    private String getIfaceMethodKey(String iface, String method) {
        return iface + "." + method;
    }

    protected String serializeResult(Object result) {
        try {
            return JacksonHelper.getMapper().writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new EasyGrpcException("Fail to Serialize Result!", e);
        }
    }

}
