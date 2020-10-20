package com.cht.easygrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * @author : chenhaitao934
 */
public class Test {

    public static void main(String[] args) {
        String target = "172.17.210.89:8888";
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext()
                .build();
        EasyGrpcServiceGrpc.EasyGrpcServiceBlockingStub blockingStub = EasyGrpcServiceGrpc.newBlockingStub(channel);
        EasyGrpcRequest request = EasyGrpcRequest.newBuilder().setReqId("111")
                .setRpcId("2222")
                .setIface("com.cht.easygrpc.remoting.iface.EasyGrpcTest")
                .setMethod("hello")
                .setRequestJson("adad")
                .build();
        EasyGrpcResponse call = blockingStub.call(request);
        System.out.println(call);
    }
}
