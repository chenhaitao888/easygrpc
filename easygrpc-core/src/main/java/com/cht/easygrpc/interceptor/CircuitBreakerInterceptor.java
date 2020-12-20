package com.cht.easygrpc.interceptor;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.constant.ExtRpcConfig;
import com.cht.easygrpc.ec.EventCenter;
import com.cht.easygrpc.ec.EventInfo;
import com.cht.easygrpc.logger.Logger;
import com.cht.easygrpc.logger.LoggerFactory;
import com.cht.easygrpc.registry.AbstractRegistry;
import com.cht.easygrpc.remoting.EasyGrpcCircuitBreakerManager;
import com.cht.easygrpc.remoting.conf.ConfigContext;
import com.cht.easygrpc.support.Invocation;
import com.cht.easygrpc.support.instance.EasyGrpcInjector;
import com.cht.easygrpc.support.stub.AbstractGrpcStub;
import com.cht.easygrpc.support.stub.EasyGrpcStub;
import io.grpc.*;

import javax.annotation.Nullable;

/**
 * @author : chenhaitao934
 */
public class CircuitBreakerInterceptor extends AbstractInterceptor {

    protected final static Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerInterceptor.class.getName());
    protected EventCenter eventCenter = EasyGrpcInjector.getInstance(EventCenter.class);

    private final EasyGrpcCircuitBreakerManager circuitBreakerManager;

    private final ConfigContext configContext;

    public CircuitBreakerInterceptor(EasyGrpcContext context) {
        super(context);
        this.circuitBreakerManager = context.getCircuitBreakerManager();
        this.configContext = context.getConfigContext();

    }

    @Override
    public Object interceptCall(Invocation invocation, AbstractGrpcStub nextStub) throws Exception {

        if(circuitBreakerManager.breakerTrigger(invocation, configContext)){
            LOGGER.warn("breaker trigger open, not allow call server, unique name {}, args {}",
                    invocation.getUniqueName(), invocation.getArguments());
            return circuitBreakerManager.returnMockResult(invocation);
        }
        Object result;
        try {
            result = nextStub.doCall(invocation);
            circuitBreakerManager.sendCallSuccess(invocation, configContext);
        } catch (Exception e) {
            LOGGER.error("{} call failure", invocation.getUniqueName(), e);
            circuitBreakerManager.sendCallFailure(invocation, configContext, e);
            throw e;
        }
        return result;
    }

    private void sendCallFailure(Invocation invocation) {
        eventCenter.publishSync(new EventInfo(ExtRpcConfig.CLIENT_CALL_FAILURE));
    }
}
