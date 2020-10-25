package com.cht.easygrpc.spring.boot;

import com.cht.easygrpc.AbstractEasyGrpcStarter;
import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.config.EasyGrpcConfig;
import com.cht.easygrpc.constant.ExtRpcConfig;
import com.cht.easygrpc.ec.EventInfo;
import com.cht.easygrpc.remoting.iface.IServiceInitializer;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcSpringbootStartup extends AbstractEasyGrpcStarter<EasyGrpcContext> {


    public EasyGrpcSpringbootStartup(EasyGrpcConfig grpcConfig, IServiceInitializer initializer) {
        this.grpcConfig = grpcConfig;
        this.iServiceInitializer = initializer;
    }

    @Override
    protected EasyGrpcConfig loadConfig() {
        return this.grpcConfig;
    }

    @Override
    protected void publishEvent() {
        eventCenter.publishSync(new EventInfo(ExtRpcConfig.INJECT_EVENT));
    }
}
