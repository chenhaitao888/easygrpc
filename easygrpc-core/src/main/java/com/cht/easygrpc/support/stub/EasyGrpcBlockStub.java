package com.cht.easygrpc.support.stub;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.EasyGrpcRequest;
import com.cht.easygrpc.EasyGrpcResponse;
import com.cht.easygrpc.EasyGrpcServiceGrpc;
import com.cht.easygrpc.helper.GrpcParseHelper;
import com.cht.easygrpc.support.Invocation;
import io.grpc.ManagedChannel;
import io.grpc.stub.AbstractStub;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcBlockStub<T> extends AbstractGrpcStub<T> {


    public EasyGrpcBlockStub(Class<T> type, EasyGrpcContext context) {
        super(type, context);
    }

    public static <T> EasyGrpcBlockStub<T> buid(Class<T> type, EasyGrpcContext context){
        return new EasyGrpcBlockStub<>(type, context);
    }

    @Override
    protected T doCall(Invocation invocation) throws Exception {
        long timeout = getTimeout(getServiceName(invocation.getIfaceName()), invocation.getMethodName());
        EasyGrpcServiceGrpc.EasyGrpcServiceBlockingStub blockingStub =
                (EasyGrpcServiceGrpc.EasyGrpcServiceBlockingStub) createEasyGrpcServiceStub(invocation, timeout);
        EasyGrpcRequest request = buildRequest(invocation);

        Future<EasyGrpcResponse> responseFuture = submit(blockingStub, request);
        EasyGrpcResponse easyGrpcResponse = responseFuture.get(timeout, TimeUnit.MILLISECONDS);
        checkResponseCode(easyGrpcResponse);

        T result = GrpcParseHelper.parseResult(easyGrpcResponse.getResultJson(), getServiceName(invocation.getIfaceName()),
                invocation.getMethod());
        logForResponse(invocation.getMethod(), result);
        return result;
    }

    @Override
    protected AbstractStub createEasyGrpcServiceStub(ManagedChannel manageChannel, Invocation invocation, long timeout) {
        /*EasyGrpcServiceGrpc.EasyGrpcServiceBlockingStub blockingStub = EasyGrpcServiceGrpc.newBlockingStub(manageChannel)
                .withDeadlineAfter(timeout, TimeUnit.MILLISECONDS)
                .withOption(IFACE_METHOD_KEY, getIfaceMethodKey(invocation.getIfaceName(), invocation.getMethodName()))
                .withOption(CALL_PARAMS_KEY, invocation.getArguments() == null ? new Object[]{} :
                        invocation.getArguments());
        return blockingStub;*/
        EasyGrpcServiceGrpc.EasyGrpcServiceBlockingStub blockingStub = new BlockingStubBuilder(invocation, timeout, manageChannel)
                                                                           .buildStub();
        return blockingStub;
    }
}
