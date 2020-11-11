package com.cht.easygrpc.support.stub;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.EasyGrpcRequest;
import com.cht.easygrpc.EasyGrpcServiceGrpc;
import com.cht.easygrpc.stream.ClientStreamWrapObserver;
import com.cht.easygrpc.stream.EasyGrpcClientStreamObserever;
import com.cht.easygrpc.stream.EasyGrpcStreamObserver;
import com.cht.easygrpc.support.Invocation;
import io.grpc.ManagedChannel;
import io.grpc.stub.AbstractStub;
import io.grpc.stub.StreamObserver;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcStreamStub<T> extends AbstractGrpcStub<T> {

    public EasyGrpcStreamStub(Class<T> type, EasyGrpcContext context) {
        super(type, context);
    }

    @Override
    protected Object doCall(Invocation invocation) throws Exception {
        long timeout = getTimeout(getServiceName(invocation.getIfaceName()), invocation.getMethodName());
        EasyGrpcServiceGrpc.EasyGrpcServiceStub easyGrpcServiceStub =
                (EasyGrpcServiceGrpc.EasyGrpcServiceStub) createEasyGrpcServiceStub(invocation, timeout);

        ClientStreamWrapObserver streamWrapObserver =
                new ClientStreamWrapObserver(invocation, (EasyGrpcStreamObserver)invocation.getArguments()[0]);

        StreamObserver<EasyGrpcRequest> easyGrpcRequestStreamObserver = easyGrpcServiceStub.callStream(streamWrapObserver);
        EasyGrpcClientStreamObserever clientStreamObserever = new EasyGrpcClientStreamObserever(invocation,
                easyGrpcRequestStreamObserver);
        return clientStreamObserever;
    }

    @Override
    protected AbstractStub createEasyGrpcServiceStub(ManagedChannel manageChannel, Invocation invocation, long timeout) {
        EasyGrpcServiceGrpc.EasyGrpcServiceStub easyGrpcServiceStub = new AsynStubBuilder(invocation, timeout, manageChannel)
                                                                            .buildStub();
        return easyGrpcServiceStub;
    }

}
