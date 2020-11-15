package com.cht.easygrpc.stream;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.EasyGrpcRequest;
import com.cht.easygrpc.EasyGrpcResponse;
import com.cht.easygrpc.domain.MethodInfo;
import com.cht.easygrpc.domain.ServiceInfo;
import com.cht.easygrpc.helper.GrpcParseHelper;
import com.cht.easygrpc.helper.StringHelper;
import com.cht.easygrpc.runner.AbstractEasyGrpcRun;
import com.cht.easygrpc.support.EasyGrpcInvocation;
import com.cht.easygrpc.support.stub.EasyGrpcStub;
import io.grpc.stub.StreamObserver;

import java.util.Map;

/**
 * @author : chenhaitao934
 */
public class ServerStreamWrapObserver extends AbstractEasyGrpcRun implements StreamObserver<EasyGrpcRequest> {


    private StreamObserver<EasyGrpcResponse> responseObserver;

    private EasyGrpcStreamObserver observerDecorate;

    public ServerStreamWrapObserver(StreamObserver<EasyGrpcResponse> responseObserver,
                                    EasyGrpcContext context, ServiceInfo serviceInfo, Map<String, EasyGrpcStub> serviceStubMap) {
        this.responseObserver = responseObserver;
        this.context = context;
        this.serviceInfo = serviceInfo;
        this.serviceStubMap = serviceStubMap;
    }

    @Override
    public void onNext(EasyGrpcRequest value) {
        if(observerDecorate == null){
            observerDecorate = getEasyGrpcStreamObserver(value, responseObserver);
        }
        if (StringHelper.isEmpty(value.getRequestJson())) {
            return;
        }
        observerDecorate.onNext(GrpcParseHelper.parseArgs(value.getRequestJson(),
                getMethodInfo(value.getIface(), value.getMethod())).get("arg0"));
    }

    @Override
    public void onError(Throwable t) {
        observerDecorate.onError(t);
    }

    @Override
    public void onCompleted() {
        observerDecorate.onCompleted();
    }


    protected EasyGrpcStreamObserver getEasyGrpcStreamObserver(EasyGrpcRequest request, StreamObserver<EasyGrpcResponse> responseObserver){
        MethodInfo methodInfo = getMethodInfo(request.getIface(), request.getMethod());
        EasyGrpcServerStreamObserver serverStreamObserver = new EasyGrpcServerStreamObserver(responseObserver);
        EasyGrpcInvocation invocation = new EasyGrpcInvocation(methodInfo.getMethod(), new Object[]{serverStreamObserver});

        return (EasyGrpcStreamObserver) serviceStubMap.get(methodInfo.getIface()).call(invocation);
    }
}
