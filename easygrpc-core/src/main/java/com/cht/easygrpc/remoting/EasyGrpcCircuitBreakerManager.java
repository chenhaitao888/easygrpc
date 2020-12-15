package com.cht.easygrpc.remoting;

import com.cht.easygrpc.domain.CircuitBreakerInfo;
import com.cht.easygrpc.helper.CollectionHelper;
import com.cht.easygrpc.helper.JsonHelper;
import com.cht.easygrpc.logger.Logger;
import com.cht.easygrpc.logger.LoggerFactory;
import com.cht.easygrpc.remoting.conf.ConfigContext;
import com.cht.easygrpc.remoting.conf.EasyGrpcCircuitBreakerConfig;
import com.cht.easygrpc.remoting.conf.EasyGrpcClientConfig;
import com.cht.easygrpc.support.Invocation;
import com.cht.easygrpc.support.SystemClock;
import com.netflix.hystrix.util.HystrixRollingNumber;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcCircuitBreakerManager {

    protected final static Logger LOGGER = LoggerFactory.getLogger(EasyGrpcCircuitBreakerManager.class.getName());

    private Map<String, Map<String, CircuitBreakerInfo>> circuitBreakerMap = new ConcurrentHashMap<>();

    private Map<String, EasyGrpcCircuitBreakerConfig> methodConfigMap = new ConcurrentHashMap<>();

    private static final double ZERO_RANGE = 1e-6;

    private Map<String, CircuitBreaker> circuitBreaker = new ConcurrentHashMap<>();

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

    public boolean breakerTrigger(Invocation invocation, ConfigContext configContext) {
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
        CircuitBreaker circuitBreaker = this.circuitBreaker.get(freezeCallKey);
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

    private boolean clearCircuitBreaker(CircuitBreaker circuitBreaker) {
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
        return null;
    }

    class CircuitBreaker {
        private long lastErrorTime;
        private HystrixRollingNumber errorRollingNumber;
        private AtomicBoolean circuitBreakerOpen = new AtomicBoolean(false);

        public long getLastErrorTime() {
            return lastErrorTime;
        }

        public void setLastErrorTime(long lastErrorTime) {
            this.lastErrorTime = lastErrorTime;
        }

        public HystrixRollingNumber getErrorRollingNumber() {
            return errorRollingNumber;
        }

        public void setErrorRollingNumber(HystrixRollingNumber errorRollingNumber) {
            this.errorRollingNumber = errorRollingNumber;
        }

        public AtomicBoolean getCircuitBreakerOpen() {
            return circuitBreakerOpen;
        }

        public void setCircuitBreakerOpen(AtomicBoolean circuitBreakerOpen) {
            this.circuitBreakerOpen = circuitBreakerOpen;
        }
    }
}
