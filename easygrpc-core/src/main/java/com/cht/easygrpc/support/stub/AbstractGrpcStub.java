package com.cht.easygrpc.support.stub;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.EasyGrpcRequest;
import com.cht.easygrpc.EasyGrpcResponse;
import com.cht.easygrpc.EasyGrpcServiceGrpc;
import com.cht.easygrpc.enums.EasyGrpcResultStatus;
import com.cht.easygrpc.exception.EasyGrpcException;
import com.cht.easygrpc.exception.ServiceException;
import com.cht.easygrpc.helper.GrpcParseHelper;
import com.cht.easygrpc.helper.StringHelper;
import com.cht.easygrpc.interceptor.CircuitBreakerInterceptor;
import com.cht.easygrpc.interceptor.CircuitBreakerInterceptor1;
import com.cht.easygrpc.interceptor.EasyGrpcClientInterceptor;
import com.cht.easygrpc.remoting.AbstractRemoting;
import com.cht.easygrpc.remoting.EasyGrpcChannelManager;
import com.cht.easygrpc.remoting.conf.EasyGrpcClientConfig;
import com.cht.easygrpc.remoting.conf.EasyGrpcMethodConfig;
import com.cht.easygrpc.support.Invocation;
import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.stub.AbstractStub;

import java.lang.reflect.Method;
import java.util.Collections;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.cht.easygrpc.enums.EasyGrpcResultStatus.*;

/**
 * @author : chenhaitao934
 */
public abstract class AbstractGrpcStub<T> extends AbstractRemoting implements EasyGrpcStub<T> {

    private final Class<T> ifaces;

    private final Map<String, Object> attachment;

    protected long timeout;

    protected final Map<String, String> baseParameter = new ConcurrentHashMap<>();

    protected final AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    protected final Map<String, EasyGrpcClientConfig> clientConfigMap = new ConcurrentHashMap<>();

    protected final AbstractGrpcStub stub;
    protected final EasyGrpcClientInterceptor interceptor;

    public AbstractGrpcStub(Class<T> type, EasyGrpcContext context) {
        this(type,null, context);
    }

    public AbstractGrpcStub(AbstractGrpcStub stub, EasyGrpcClientInterceptor interceptor, Class<T> type) {
        super(interceptor.getContext());
        this.stub = stub;
        this.interceptor = interceptor;
        this.ifaces = type;
        this.attachment = null;
    }

    public AbstractGrpcStub(Class<T> ifaces, Map<String, Object> attachment, EasyGrpcContext context) {
        super(context);
        if (ifaces == null) {
            throw new IllegalArgumentException("ifaces == null");
        }
        this.ifaces = ifaces;
        this.attachment = attachment == null ? null : Collections.unmodifiableMap(attachment);
        this.stub = null;
        this.interceptor = null;
    }


    @Override
    public Class<T> getInterface() {
        return ifaces;
    }

    @Override
    public Object call(Invocation invocation) throws EasyGrpcException {
        initIdAndServiceName(invocation);
        int resultCode = 0;
        try {
            return doCall(invocation);
        } catch (ServiceException e) {
            resultCode = e.getCode();
            logForError(invocation.getIfaceName(), invocation.getMethodName(), e);
            throw new EasyGrpcException(e);
        } catch (Throwable e) {
            if (isTimeoutException(e)) {
                resultCode = TIMEOUT.getCode();
            } else if (isNoServerNodesException(e)) {
                resultCode = NO_SERVER_NODES.getCode();
            }
            logForError(invocation.getIfaceName(), invocation.getMethodName(), e);
            throw new EasyGrpcException(e);
        }finally {
            logRpc(invocation.getIfaceName(), invocation.getMethodName(), resultCode);
            if (atomicBoolean.get()) {
                baseParameter.clear();
            }
        }
    }

    protected void logForError(String serviceName, String methodName, Throwable e) {
        // todo
    }

    protected void logRpc(String serviceName, String methodName, int resultCode) {
        // todo
    }

    protected void logForResponse(Method method, T result) {
        // todo
    }

    private void initIdAndServiceName(Invocation invocation) {
        if (!context.getConfigContext().isServer()) {
            baseParameter.put("reqId", UUID.randomUUID().toString().replaceAll("-", ""));
            baseParameter.put("rpcId", UUID.randomUUID().toString().replaceAll("-", ""));
            invocation.setReqId(baseParameter.get("reqId"));
            invocation.setRpcId(baseParameter.get("rpcId"));
            invocation.setServiceName(getServiceName(invocation.getIfaceName()));
            atomicBoolean.set(true);
        }
    }

    public abstract Object doCall(Invocation invocation) throws Exception;

    protected abstract AbstractStub createEasyGrpcServiceStub(Channel manageChannel, Invocation invocation, long timeout);

    protected long getTimeout(String serviceName, String method) {
        if (timeout > 0) {
            return timeout;
        }
        EasyGrpcMethodConfig methodConfig = context.getConfigContext().getMethodConfig(serviceName, ifaces.getName(), method);
        if(methodConfig != null && methodConfig.getTimeoutInMillis() > 0){
            return methodConfig.getTimeoutInMillis();
        }
        return context.getConfigContext().getClientConfig(serviceName).getTimeoutInMillis();
    }

    protected AbstractStub createEasyGrpcServiceStub(Invocation invocation, long timeout){
        EasyGrpcChannelManager channelManager = context.getEasyGrpcChannelManager();
        ManagedChannel manageChannel = channelManager.getManageChannel(getServiceName(invocation.getIfaceName()));
        if(manageChannel == null){
            throw new EasyGrpcException("manageChannel is null");
        }
        return createEasyGrpcServiceStub(manageChannel, invocation, timeout);
    }

    protected String getServiceName(String ifaceName) {
        String serviceName = serviceIface.get(ifaceName);
        if(StringHelper.isEmpty(serviceName)){
            throw new EasyGrpcException("undeclared inteface (" + ifaceName + ")");
        }
        return serviceName;
    }

    protected String getIfaceMethodKey(String iface, String method) {
        return iface + "." + method;
    }

    protected EasyGrpcRequest buildRequest(Invocation invocation) {
        String requestJson = GrpcParseHelper.genArgJsons(invocation.getArguments());
        EasyGrpcRequest request = EasyGrpcRequest.newBuilder().setReqId(invocation.getReqId())
                .setRpcId(baseParameter.get("rpcId"))
                .setIface(invocation.getIfaceName())
                .setMethod(invocation.getMethodName())
                .setRequestJson(requestJson)
                .build();
        return request;
    }

    protected Future<EasyGrpcResponse> submit(EasyGrpcServiceGrpc.EasyGrpcServiceBlockingStub serviceStub, EasyGrpcRequest request){
        Future<EasyGrpcResponse> responseFuture = threadPoolExecutor.submit(() -> {
            try {
                return serviceStub.call(request);
            } finally {
                baseParameter.clear();
            }
        });
        return responseFuture;

    }

    protected void checkResponseCode(EasyGrpcResponse easyGrpcResponse) throws ServiceException {
        if (easyGrpcResponse == null || easyGrpcResponse.getCode() != EasyGrpcResultStatus.SUCCESS.getCode()) {
            throw new ServiceException(easyGrpcResponse == null ? 0 : easyGrpcResponse.getCode()
                    , String.format("server return error. code:%d.", easyGrpcResponse == null ? null : easyGrpcResponse.getCode())
                    , easyGrpcResponse == null ? "" : easyGrpcResponse.getMsg());
        }
    }

    class AsynStubBuilder extends AbstractStubBuilder<EasyGrpcServiceGrpc.EasyGrpcServiceStub> {

        public AsynStubBuilder(Invocation invocation, long timeout, Channel manageChannel) {
            super(EasyGrpcServiceGrpc::newStub, invocation, timeout, manageChannel);
        }
    }

    class BlockingStubBuilder extends AbstractStubBuilder<EasyGrpcServiceGrpc.EasyGrpcServiceBlockingStub> {

        public BlockingStubBuilder(Invocation invocation, long timeout, Channel manageChannel) {
            super(EasyGrpcServiceGrpc::newBlockingStub, invocation, timeout, manageChannel);
        }
    }

}
