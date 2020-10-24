package com.cht.easygrpc.logger;

/**
 * @author : chenhaitao934
 */
public class Slf4jLogger extends AbstractLogger implements Logger{

    private final org.slf4j.Logger logger;

    public Slf4jLogger(org.slf4j.Logger logger) {
        this.logger = logger;
    }
    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void debug(Throwable e) {
        logger.debug(e.getMessage(), e);
    }

    @Override
    public void debug(String message, Throwable e) {
        logger.debug(message, e);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void info(Throwable e) {
        logger.info(e.getMessage(), e);
    }

    @Override
    public void info(String message, Throwable e) {
        logger.info(message, e);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void warn(Throwable e) {
        logger.warn(e.getMessage(), e);
    }

    @Override
    public void warn(String message, Throwable e) {
        logger.warn(message, e);
    }

    @Override
    public void error(String message) {
        logger.error(message);
    }

    @Override
    public void error(Throwable e) {
        logger.error(e.getMessage(), e);
    }

    @Override
    public void error(String message, Throwable e) {
        logger.error(message, e);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }
}
