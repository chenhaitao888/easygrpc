package com.cht.easygrpc.logger;

import java.io.File;

/**
 * @author : chenhaitao934
 */
public class Slf4jLoggerAdapter implements LoggerAdapter{

    @Override
    public Logger getLogger(Class<?> key) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(key));
    }

    @Override
    public Logger getLogger(String key) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(key));
    }
}
