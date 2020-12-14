package com.cht.easygrpc.remoting;

import com.cht.easygrpc.domain.CircuitBreakerInfo;
import com.cht.easygrpc.helper.CollectionHelper;
import com.cht.easygrpc.helper.JacksonHelper;
import com.cht.easygrpc.helper.JsonHelper;
import com.cht.easygrpc.logger.Logger;
import com.cht.easygrpc.logger.LoggerFactory;
import com.cht.easygrpc.registry.AbstractRegistry;
import com.cht.easygrpc.remoting.conf.EasyGrpcMethodConfig;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcCircuitBreakerManager {

    protected final static Logger LOGGER = LoggerFactory.getLogger(EasyGrpcCircuitBreakerManager.class.getName());

    private Map<String, Map<String, CircuitBreakerInfo>> circuitBreakerMap = new ConcurrentHashMap<>();

    private Map<String, EasyGrpcMethodConfig> methodConfigMap = new ConcurrentHashMap<>();

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

}
