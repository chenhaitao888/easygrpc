package com.cht.easygrpc.stream;

import com.cht.easygrpc.EasyGrpcRequest;
import com.cht.easygrpc.helper.GrpcParseHelper;
import com.cht.easygrpc.support.Invocation;
import io.grpc.stub.StreamObserver;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcClientStreamObserever<V> implements EasyGrpcStreamObserver<V>{

    private Invocation invocation;

    private StreamObserver<EasyGrpcRequest> observerDecorate;


    public EasyGrpcClientStreamObserever(Invocation invocation, StreamObserver<EasyGrpcRequest> streamObserver) {
        this.invocation = invocation;
        this.observerDecorate = streamObserver;
    }

    @Override
    public void onNext(V value) {
        String req = GrpcParseHelper.genArgJsons(new Object[]{value});
        EasyGrpcRequest request = EasyGrpcRequest.newBuilder().setReqId(invocation.getReqId())
                .setRpcId(invocation.getRpcId())
                .setIface(invocation.getIfaceName())
                .setMethod(invocation.getMethodName())
                .setRequestJson(req)
                .build();
        observerDecorate.onNext(request);
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
