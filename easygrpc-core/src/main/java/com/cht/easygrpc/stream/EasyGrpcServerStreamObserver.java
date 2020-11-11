package com.cht.easygrpc.stream;

import com.cht.easygrpc.EasyGrpcRequest;
import com.cht.easygrpc.EasyGrpcResponse;
import com.cht.easygrpc.enums.EasyGrpcResultStatus;
import com.cht.easygrpc.runner.AbstractEasyGrpcRun;
import io.grpc.stub.StreamObserver;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcServerStreamObserver<V> extends AbstractEasyGrpcRun implements EasyGrpcStreamObserver<V>{
    
    private StreamObserver<EasyGrpcResponse> observerDecorate;

    public EasyGrpcServerStreamObserver(StreamObserver<EasyGrpcResponse> streamObserver) {
        this.observerDecorate = streamObserver;
    }

    @Override
    public void onNext(V value) {
        String resultString = serializeResult(value);
        observerDecorate.onNext(EasyGrpcResponse.newBuilder()
                .setCode(EasyGrpcResultStatus.SUCCESS.getCode())
                .setResultJson(resultString).build());
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
