package com.cht.easygrpc.runner;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.EasyGrpcRequest;
import com.cht.easygrpc.EasyGrpcResponse;
import com.cht.easygrpc.domain.InterfaceInfo;
import com.cht.easygrpc.domain.MethodAliasInfo;
import com.cht.easygrpc.domain.MethodInfo;
import com.cht.easygrpc.domain.ServiceInfo;
import com.cht.easygrpc.enums.EasyGrpcResultStatus;
import com.cht.easygrpc.exception.EasyGrpcException;
import com.cht.easygrpc.exception.ServiceException;
import com.cht.easygrpc.exception.StartupException;
import com.cht.easygrpc.helper.EasyRpcParseHelper;
import com.cht.easygrpc.helper.JacksonHelper;
import com.cht.easygrpc.helper.StringHelper;
import com.cht.easygrpc.remoting.iface.IInvokeHandler;
import com.cht.easygrpc.remoting.iface.IServiceInitializer;
import com.cht.easygrpc.remoting.iface.InvokeImplHandler;
import com.cht.easygrpc.support.EasyGrpcInvocation;
import com.cht.easygrpc.support.EasyGrpcStub;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.grpc.stub.StreamObserver;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.cht.easygrpc.constant.LogConstants.TAG_METHOD;
import static com.cht.easygrpc.constant.LogConstants.TAG_SERVICE;

/**
 * @author : chenhaitao934
 * @date : 3:13 下午 2020/10/9
 */
public class EasyGrpcRunnable implements Runnable{

    private EasyGrpcRequest request;
    private StreamObserver<EasyGrpcResponse> responseObserver;
    private EasyGrpcContext context;

    private Map<String, MethodAliasInfo> methodAliasInfoMap = new HashMap<>();

    protected ServiceInfo serviceInfo;

    protected Map<String, EasyGrpcStub> serviceStubMap;


    public EasyGrpcRunnable(EasyGrpcRequest request, StreamObserver<EasyGrpcResponse> responseObserver,
                            EasyGrpcContext context, ServiceInfo serviceInfo, Map<String, EasyGrpcStub> serviceStubMap) {
        this.request = request;
        this.responseObserver = responseObserver;
        this.context = context;
        this.serviceInfo = serviceInfo;
        this.serviceStubMap = serviceStubMap;
    }


    @Override
    public void run() {
        Object resultObj;
        MethodInfo methodInfo;
        EasyGrpcResponse response;
        try {
            methodInfo = getMethodInfo(request.getIface(), request.getMethod());
            Map<String, Object> args = EasyRpcParseHelper.parseArgs(request.getRequestJson(), methodInfo);

            EasyGrpcInvocation invocation = new EasyGrpcInvocation(methodInfo.getMethod(), args);

            resultObj = serviceStubMap.get(methodInfo.getIface()).call(invocation);
            String resultString = serializeResult(resultObj);

            response = EasyGrpcResponse.newBuilder().setCode(EasyGrpcResultStatus.SUCCESS.getCode()).setResultJson(resultString).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            response =
                    EasyGrpcResponse.newBuilder().setCode(EasyGrpcResultStatus.PARSE_ARGS_FAILED.getCode()).setMsg(StringHelper.toString(e)).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Throwable e) {
            if (e instanceof ServiceException && e.getCause() != null) {
                e = e.getCause();
            }
            response = EasyGrpcResponse.newBuilder().setCode(EasyGrpcResultStatus.ERROR.getCode()).setMsg(StringHelper.toString(e)).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }



    public  String serializeResult(Object result) {
        try {
            return JacksonHelper.getMapper().writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new EasyGrpcException("Fail to Serialize Result!", e);
        }
    }

    private MethodInfo getMethodInfo(String iface, String method) {
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

}
