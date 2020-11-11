package com.cht.easygrpc.spring.boot;

import com.cht.easygrpc.remoting.iface.IServiceInitializer;
import com.cht.easygrpc.spring.boot.annotation.EasyGrpcSpringService;
import com.cht.easygrpc.support.stub.EasyGrpcStub;
import com.cht.easygrpc.support.proxy.ProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcServiceSpringInitializer implements IServiceInitializer, ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(EasyGrpcServiceSpringInitializer.class.getName());

    public ApplicationContext applicationContext;

    private ProxyFactory proxyFactory;

    @Override
    public Set<Class<?>> getService() {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(EasyGrpcSpringService.class);
        Set<Class<?>> service = new HashSet<>();
        for(Map.Entry<String, Object> entry : beansWithAnnotation.entrySet()){
            EasyGrpcSpringService annotation = entry.getValue().getClass().getAnnotation(EasyGrpcSpringService.class);
            Class<?>[] interfaces = annotation.interfaces();
            service.addAll(Arrays.asList(interfaces));
        }
        return service;
    }

    @Override
    public void wrap(String[] servicePackages, String[] serviceImplPackages, ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    @Override
    public Object getBean(Class<?> type) {
        return applicationContext.getBean(type);
    }

    @Override
    public EasyGrpcStub serviceStub(Class iface, Class<? extends Annotation> annotaion) {
        try {
            Object bean = getBean(iface);
            EasyGrpcStub stub = proxyFactory.getStub(bean, iface);
            return stub;
        } catch (Exception e) {
            logger.error(iface.getName() + "<generate stub failure>", e);
            return null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
