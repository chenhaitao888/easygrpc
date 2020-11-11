package com.cht.easygrpc.stream;


import com.cht.easygrpc.EasyGrpcResponse;
import com.cht.easygrpc.helper.EasyRpcParseHelper;
import com.cht.easygrpc.support.Invocation;
import io.grpc.stub.StreamObserver;

import java.lang.reflect.Type;

/**
 * @author : chenhaitao934
 */
public class ClientStreamWrapObserver implements StreamObserver<EasyGrpcResponse> {

    private Invocation invocation;

    private EasyGrpcStreamObserver observerDecorate;

    public ClientStreamWrapObserver(Invocation invocation, EasyGrpcStreamObserver streamObserver) {
        this.invocation = invocation;
        this.observerDecorate = streamObserver;
    }

    @Override
    public void onNext(EasyGrpcResponse value) {
        String uniqueName = invocation.getUniqueName();
        Type type = observerDecorate.getReturnType();
        observerDecorate.onNext(EasyRpcParseHelper.parseResult(value.getResultJson(), uniqueName, type));
    }

    @Override
    public void onError(Throwable t) {
        observerDecorate.onError(t);
    }

    @Override
    public void onCompleted() {
        observerDecorate.onCompleted();
    }
}
