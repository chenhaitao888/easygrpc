package com.cht.easygrpc.remoting;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.exception.EasyGrpcException;
import com.cht.easygrpc.exception.RemotingException;
import com.cht.easygrpc.helper.CollectionHelper;
import com.cht.easygrpc.helper.StringHelper;
import com.cht.easygrpc.remoting.iface.IServiceInitializer;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : chenhaitao934
 * @date : 2:21 下午 2020/10/9
 */
public class EasyGrpcServer extends AbstractRemotingServer{

    private static final int FLOW_CONTROLL_WINDOW = 10 * 1024 * 1024; // 10MB

    private static final int MAX_MESSAGE_SIZE = 20 * 1024 * 1024; // 接收消息最大20MB

    public EasyGrpcServer(EasyGrpcContext context, IServiceInitializer initializer) {
        super(context, initializer);
    }

    @Override
    protected void serverStart() throws RemotingException {
        if(context == null || context.getServerConfig() == null){
            return;
        }
        if(StringHelper.isEmpty(context.getServerConfig().getServiceName())){
            throw new EasyGrpcException("serviceName is empty.");
        }
        try {
            nettyServerStart(context);
        } catch (Exception e) {
            throw new RemotingException("start netty server failure", e);
        }
    }

    private void nettyServerStart(EasyGrpcContext context) throws IOException, InterruptedException {

        EasyGrpcProcessor easyGrpcProcessor = new EasyGrpcProcessor();
        Server server = NettyServerBuilder
                .forPort(context.getServerConfig().getPort())
                .addService(easyGrpcProcessor)
                .executor(threadPoolExecutor)
                .flowControlWindow(FLOW_CONTROLL_WINDOW)
                .maxInboundMessageSize(MAX_MESSAGE_SIZE)
                .build()
                .start();
        awaitTermination(server);
    }

    @Override
    protected void serverShutdown(Server server) throws RemotingException {
        Runtime.getRuntime().addShutdownHook(new Thread(){

            @Override
            public void run() {
                if (server != null) {
                    try {
                        server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        // 添加日志 todo
                    }
                }
            }
        });
    }
}
