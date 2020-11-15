package com.cht.easygrpc.runner;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.EasyGrpcRequest;
import com.cht.easygrpc.EasyGrpcResponse;
import com.cht.easygrpc.domain.MethodInfo;
import com.cht.easygrpc.domain.ServiceInfo;
import com.cht.easygrpc.enums.EasyGrpcResultStatus;
import com.cht.easygrpc.exception.ServiceException;
import com.cht.easygrpc.helper.GrpcParseHelper;
import com.cht.easygrpc.helper.StringHelper;
import com.cht.easygrpc.support.EasyGrpcInvocation;
import com.cht.easygrpc.support.stub.EasyGrpcStub;
import io.grpc.stub.StreamObserver;

import java.util.Map;

/**
 * @author : chenhaitao934
 * @date : 3:13 下午 2020/10/9
 */
public class EasyGrpcRunnable extends AbstractEasyGrpcRun implements Runnable{

    private EasyGrpcRequest request;
    private StreamObserver<EasyGrpcResponse> responseObserver;


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
            Map<String, Object> args = GrpcParseHelper.parseArgs(request.getRequestJson(), methodInfo);

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


}
