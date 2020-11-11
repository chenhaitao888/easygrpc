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
import com.cht.easygrpc.helper.EasyRpcParseHelper;
import com.cht.easygrpc.helper.JacksonHelper;
import com.cht.easygrpc.helper.StringHelper;
import com.cht.easygrpc.support.EasyGrpcInvocation;
import com.cht.easygrpc.support.stub.EasyGrpcStub;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;

import static com.cht.easygrpc.constant.LogConstants.TAG_METHOD;
import static com.cht.easygrpc.constant.LogConstants.TAG_SERVICE;

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


}
