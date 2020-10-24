package com.cht.easygrpc.logger;

import com.cht.easygrpc.remoting.conf.EasyGrpcCommonConfig;
import com.cht.easygrpc.spi.ServiceProviderInterface;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : chenhaitao934
 */
public class LoggerFactory {

    private static LoggerAdapter loggerAdapter;
    private static final ConcurrentHashMap<String, Logger> map = new ConcurrentHashMap<>();

    public static Logger getLogger(String key){
        Logger logger = map.get(key);
        if(logger == null){
            map.putIfAbsent(key, loggerAdapter.getLogger(key));
            logger = map.get(key);
        }
        return logger;
    }

    public static void setLoggerAdapter(EasyGrpcCommonConfig config) {
        try {
            setLoggerAdapter(ServiceProviderInterface.load(LoggerAdapter.class, config));
        } catch (Exception e) {
            throw new IllegalStateException("service loader fail", e);
        }
    }

    public static void setLoggerAdapter(LoggerAdapter loggerAdapter) {
        if(loggerAdapter != null){
            LoggerFactory.loggerAdapter = loggerAdapter;
            Logger logger = loggerAdapter.getLogger(LoggerFactory.class.getName());
            logger.info("logger: {}", loggerAdapter.getClass().getName());
        }
    }
}
