package com.cht.easygrpc.interceptor;

import com.cht.easygrpc.EasyGrpcContext;
import com.cht.easygrpc.constant.ExtRpcConfig;
import com.cht.easygrpc.domain.CircuitBreakerInfo;
import com.cht.easygrpc.ec.EventCenter;
import com.cht.easygrpc.ec.EventSubscriber;
import com.cht.easygrpc.enums.EasyGrpcResultStatus;
import com.cht.easygrpc.helper.CollectionHelper;
import com.cht.easygrpc.helper.GrpcParseHelper;
import com.cht.easygrpc.helper.JsonHelper;
import com.cht.easygrpc.logger.Logger;
import com.cht.easygrpc.logger.LoggerFactory;
import com.cht.easygrpc.remoting.EasyGrpcCircuitBreakerManager;
import com.cht.easygrpc.remoting.conf.ConfigContext;
import com.cht.easygrpc.remoting.conf.EasyGrpcCircuitBreakerConfig;
import com.cht.easygrpc.remoting.conf.EasyGrpcClientConfig;
import com.cht.easygrpc.support.Invocation;
import com.cht.easygrpc.support.SystemClock;
import com.cht.easygrpc.support.instance.EasyGrpcInjector;
import com.cht.easygrpc.support.stub.AbstractGrpcStub;
import com.netflix.hystrix.util.HystrixRollingNumber;
import com.netflix.hystrix.util.HystrixRollingNumberEvent;
import io.grpc.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author : chenhaitao934
 */
public class CircuitBreakerInterceptor extends AbstractInterceptor {

    protected final static Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerInterceptor.class.getName());
    protected EventCenter eventCenter = EasyGrpcInjector.getInstance(EventCenter.class);

    private final EasyGrpcCircuitBreakerManager circuitBreakerManager;

    private final ConfigContext configContext;

    private Map<String, Map<String, CircuitBreakerInfo>> circuitBreakerMap = new ConcurrentHashMap<>();

    private Map<String, EasyGrpcCircuitBreakerConfig> methodConfigMap = new ConcurrentHashMap<>();


    private Map<String, CircuitBreakerInterceptor.CircuitBreaker> circuitBreaker = new ConcurrentHashMap<>();

    public CircuitBreakerInterceptor(EasyGrpcContext context) {
        super(context);
        this.circuitBreakerManager = context.getCircuitBreakerManager();
        this.configContext = context.getConfigContext();
        circuitBreakerListener(eventCenter);
    }

    @Override
    public Object interceptCall(Invocation invocation, AbstractGrpcStub nextStub) throws Exception {

        if(breakerTrigger(invocation)){
            LOGGER.warn("breaker trigger open, not allow call server, unique name {}, args {}",
                    invocation.getUniqueName(), invocation.getArguments());
            return returnMockResult(invocation);
        }
        Object result;
        try {
            result = nextStub.doCall(invocation);
            sendCallSuccess(invocation);
        } catch (Exception e) {
            LOGGER.error("{} call failure", invocation.getUniqueName(), e);
            sendCallFailure(invocation, e);
            throw e;
        }
        return result;
    }

    private void circuitBreakerListener(EventCenter eventCenter) {
        String appId = context.getCommonConfig().getAppId();
        eventCenter.subscribe(new EventSubscriber(appId, (eventInfo) -> {
            try {
                putCircuitBreaker(eventInfo.getTopic(),
                        (List<CircuitBreakerInfo>) eventInfo.getParam(eventInfo.getTopic()));
            } catch (Exception e) {
                LOGGER.error("{} put circuit breaker failure", eventInfo.getTopic(), e);
            }
        }), ExtRpcConfig.CIRCUIT_BREAKER_EVENT);
    }


    public void putCircuitBreaker(String clientName, List<CircuitBreakerInfo> circuitBreakerList){
        try {
            Map<String, CircuitBreakerInfo> circuitBreakerInfoMap = wrapCircuitBreaker(circuitBreakerList);
            if(null == circuitBreakerInfoMap){
                circuitBreakerMap.remove(clientName);
                return;
            }
            circuitBreakerMap.put(clientName, circuitBreakerInfoMap);
        } catch (Exception e) {
            LOGGER.error("putCircuitBreaker failure, clientName {} , circuitBreakerList {}", clientName,
                    JsonHelper.toJson(circuitBreakerList));
        }
    }

    private Map<String, CircuitBreakerInfo> wrapCircuitBreaker(List<CircuitBreakerInfo> circuitBreakerList) {
        if(CollectionHelper.isEmpty(circuitBreakerList)){
            return null;
        }
        Map<String, CircuitBreakerInfo> map = new ConcurrentHashMap<>();
        circuitBreakerList.forEach(e -> map.put(e.connectKey(), e));
        return map;
    }

    public boolean breakerTrigger(Invocation invocation) {
        String serviceName = invocation.getServiceName();
        Map<String, CircuitBreakerInfo> circuitBreakerInfoMap = circuitBreakerMap.get(serviceName);
        String ifaceMethodKey = invocation.getIfaceMethodKey();
        if(null != circuitBreakerInfoMap && null != circuitBreakerInfoMap.get(ifaceMethodKey)
                && circuitBreakerInfoMap.get(ifaceMethodKey).isOpenCircuitBreaker()){
            return true;
        }
        EasyGrpcClientConfig clientConfig = configContext.getClientConfig(serviceName);
        EasyGrpcCircuitBreakerConfig circuitBreakerConfig = clientConfig.getCircuitBreakerConfig();
        if(freezeCall(serviceName, circuitBreakerConfig)){
            return true;
        }
        return freezeCall(invocation.getUniqueName(), getMethodCircuitBreakerConfig(invocation, clientConfig));
    }

    private EasyGrpcCircuitBreakerConfig getMethodCircuitBreakerConfig(Invocation invocation, EasyGrpcClientConfig clientConfig) {
        EasyGrpcCircuitBreakerConfig circuitBreakerConfig = methodConfigMap.computeIfAbsent(invocation.getUniqueName(), key -> clientConfig.getCircuitBreakerConfig());
        return circuitBreakerConfig;
    }

    private boolean freezeCall(String freezeCallKey, EasyGrpcCircuitBreakerConfig circuitBreakerConfig) {
        if(!openBreaker(circuitBreakerConfig)){
            return false;
        }
        CircuitBreakerInterceptor.CircuitBreaker circuitBreaker = this.circuitBreaker.get(freezeCallKey);
        if(null == circuitBreaker){
            return false;
        }
        boolean open = circuitBreaker.circuitBreakerOpen.get();
        if(!open){
            return false;
        }
        if ((circuitBreaker.getLastErrorTime() + circuitBreakerConfig.getBreakerTimeWindow()) < SystemClock.now()) {
            return !clearCircuitBreaker(circuitBreaker);
        }
        return true;
    }

    private boolean clearCircuitBreaker(CircuitBreakerInterceptor.CircuitBreaker circuitBreaker) {
        return circuitBreaker.getCircuitBreakerOpen().compareAndSet(true, false);
    }

    private boolean openBreaker(EasyGrpcCircuitBreakerConfig circuitBreakerConfig) {
        if(null == circuitBreakerConfig){
            return false;
        }
        final boolean breakerTimeoutRate = circuitBreakerConfig.getBreakerTimeoutRate() > 0;
        final boolean breakerTimeWindow = circuitBreakerConfig.getBreakerTimeWindow() > 0;
        final boolean breakerThreshold = circuitBreakerConfig.getBreakerThreshold() > 0;
        final boolean hasCircuitBreakerErrorRate = circuitBreakerConfig.getBreakerFailRate() != null
                && Math.abs(circuitBreakerConfig.getBreakerFailRate()) >= ZERO_RANGE;
        final boolean hasCircuitBreakerTimeoutRate = circuitBreakerConfig.getBreakerTimeoutRate() != null
                && Math.abs(circuitBreakerConfig.getBreakerTimeoutRate()) >= ZERO_RANGE;

        return breakerTimeoutRate && breakerTimeWindow && (
                breakerThreshold || hasCircuitBreakerErrorRate || hasCircuitBreakerTimeoutRate);
    }

    public Object returnMockResult(Invocation invocation) {
        try {
            Map<String, CircuitBreakerInfo> circuitBreakerInfoMap = circuitBreakerMap.get(invocation.getServiceName());
            if(null == circuitBreakerInfoMap){
                return null;
            }
            CircuitBreakerInfo circuitBreakerInfo = circuitBreakerInfoMap.get(invocation.getIfaceMethodKey());
            if (null == circuitBreakerInfo) {
                return null;
            }
            return GrpcParseHelper.parseResult(circuitBreakerInfo.getMockResult(), invocation);
        } catch (Exception e) {
            LOGGER.error("return mock result failure ", e);
            return null;
        }
    }

    public void sendCallFailure(Invocation invocation, Exception e) {
        String serviceName = invocation.getServiceName();
        EasyGrpcClientConfig clientConfig = configContext.getClientConfig(serviceName);
        countCallFailTimes(serviceName, clientConfig.getCircuitBreakerConfig(), e);
        String ifaceMethodKey = invocation.getIfaceMethodKey();
        countCallFailTimes(ifaceMethodKey, getMethodCircuitBreakerConfig(invocation, clientConfig), e);
    }

    private void countCallFailTimes(String freezeCallKey, EasyGrpcCircuitBreakerConfig circuitBreakerConfig,
                                    Exception e) {
        if(!openBreaker(circuitBreakerConfig)){
            return;
        }
        final CircuitBreakerInterceptor.CircuitBreaker circuitBreaker = getCircuitBreaker(freezeCallKey, circuitBreakerConfig);
        circuitBreaker.getRollingNumber().increment(HystrixRollingNumberEvent.FAILURE);
        if(EasyGrpcResultStatus.isTimeoutException(e)){
            circuitBreaker.getRollingNumber().increment(HystrixRollingNumberEvent.TIMEOUT);
        }

        if(OverThreshold(circuitBreaker, circuitBreakerConfig)){
            circuitBreaker.setLastErrorTime(SystemClock.now());
            circuitBreaker.getCircuitBreakerOpen().compareAndSet(false, true);
            circuitBreaker.getRollingNumber().reset();
        }

    }

    private boolean OverThreshold(CircuitBreakerInterceptor.CircuitBreaker circuitBreaker, EasyGrpcCircuitBreakerConfig circuitBreakerConfig) {
        long failNum = circuitBreaker.getRollingNumber().getRollingSum(HystrixRollingNumberEvent.FAILURE);
        long timeoutNum = circuitBreaker.getRollingNumber().getRollingSum(HystrixRollingNumberEvent.TIMEOUT);
        long totalNum = circuitBreaker.getRollingNumber().getRollingSum(HystrixRollingNumberEvent.SUCCESS) + failNum;

        if (circuitBreakerConfig.getBreakerThreshold() > 0 && failNum >= circuitBreakerConfig.getBreakerThreshold()) {
            return true;
        }
        if (failNum < 5) {
            return false;
        }

        final Double circuitBreakerFailRate = circuitBreakerConfig.getBreakerFailRate();
        if (circuitBreakerFailRate != null && Math.abs(circuitBreakerFailRate) >= ZERO_RANGE) {
            if ((double) failNum / totalNum > circuitBreakerFailRate) {
                return true;
            }
        }

        final Double circuitBreakerTimeoutRate = circuitBreakerConfig.getBreakerTimeoutRate();
        if (circuitBreakerTimeoutRate != null && Math.abs(circuitBreakerTimeoutRate) >= ZERO_RANGE) {
            return (double) timeoutNum / totalNum > circuitBreakerTimeoutRate;
        }

        return false;
    }

    private CircuitBreakerInterceptor.CircuitBreaker getCircuitBreaker(String freezeCallKey, EasyGrpcCircuitBreakerConfig circuitBreakerConfig) {
        return circuitBreaker.computeIfAbsent(freezeCallKey, key -> {
            CircuitBreakerInterceptor.CircuitBreaker circuitBreaker = new CircuitBreakerInterceptor.CircuitBreaker();
            circuitBreaker.setRollingNumber(new HystrixRollingNumber(circuitBreakerConfig.getBreakerStatisticsTimeWindow(), 10));
            return circuitBreaker;
        });
    }

    public void sendCallSuccess(Invocation invocation) {
        String serviceName = invocation.getServiceName();
        EasyGrpcClientConfig clientConfig = configContext.getClientConfig(serviceName);
        countCallSuccessTimes(serviceName, clientConfig.getCircuitBreakerConfig());
        String ifaceMethodKey = invocation.getIfaceMethodKey();
        countCallSuccessTimes(ifaceMethodKey, getMethodCircuitBreakerConfig(invocation, clientConfig));
    }

    private void countCallSuccessTimes(String key, EasyGrpcCircuitBreakerConfig circuitBreakerConfig) {
        if(!openBreaker(circuitBreakerConfig)){
            return;
        }
        final CircuitBreakerInterceptor.CircuitBreaker circuitBreaker = getCircuitBreaker(key, circuitBreakerConfig);
        circuitBreaker.getRollingNumber().increment(HystrixRollingNumberEvent.SUCCESS);
    }

    class CircuitBreaker {
        private long lastErrorTime;
        private HystrixRollingNumber rollingNumber;
        private AtomicBoolean circuitBreakerOpen = new AtomicBoolean(false);

        public long getLastErrorTime() {
            return lastErrorTime;
        }

        public void setLastErrorTime(long lastErrorTime) {
            this.lastErrorTime = lastErrorTime;
        }

        public HystrixRollingNumber getRollingNumber() {
            return rollingNumber;
        }

        public void setRollingNumber(HystrixRollingNumber rollingNumber) {
            this.rollingNumber = rollingNumber;
        }

        public AtomicBoolean getCircuitBreakerOpen() {
            return circuitBreakerOpen;
        }

        public void setCircuitBreakerOpen(AtomicBoolean circuitBreakerOpen) {
            this.circuitBreakerOpen = circuitBreakerOpen;
        }
    }
}
