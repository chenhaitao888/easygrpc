package com.cht.easygrpc.spring.boot.processor;

import com.cht.easygrpc.spring.boot.EasyGrpcServiceSpringInitializer;
import com.cht.easygrpc.spring.boot.annotation.EasyGrpcAutowired;
import com.cht.easygrpc.support.instance.Container;
import com.cht.easygrpc.support.instance.EasyGrpcInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;

/**
 * @author : chenhaitao934
 */
public class AbstractEasyGrpcProcessor implements BeanPostProcessor, ApplicationContextAware {

    protected final Logger logger = LoggerFactory.getLogger(AbstractEasyGrpcProcessor.class.getName());

    protected ApplicationContext applicationContext;

    protected Container container = EasyGrpcInjector.getInstance(Container.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
