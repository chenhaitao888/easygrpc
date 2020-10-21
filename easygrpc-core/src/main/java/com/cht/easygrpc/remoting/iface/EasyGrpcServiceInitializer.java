package com.cht.easygrpc.remoting.iface;

import com.cht.easygrpc.exception.EasyGrpcException;
import com.cht.easygrpc.helper.CollectionHelper;
import com.cht.easygrpc.support.ClassScanner;
import com.cht.easygrpc.support.EasyGrpcStub;
import com.cht.easygrpc.support.proxy.ProxyFactory;


import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcServiceInitializer implements IServiceInitializer{

    private String[] servicePackages;

    private String[] serviceImplPackages;

    private ProxyFactory proxyFactory;



    public Set<Class<?>> getService() {
        Set<Class<?>> scan = new ClassScanner().scan(servicePackages);
        return scan;
    }

    @Override
    public void wrap(String[] servicePackages, String[] serviceImplPackages, ProxyFactory proxyFactory) {
        this.servicePackages = servicePackages;
        this.proxyFactory = proxyFactory;
        this.serviceImplPackages = serviceImplPackages;
    }

    @Override
    public Object getBean(Class<?> type) {
        try {
            Constructor<?> constructor = type.getConstructor();
            Object bean = constructor.newInstance();
            return bean;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public EasyGrpcStub serviceStub(Class iface, Class<? extends Annotation> annotaion) {
        Set<Class<?>> impl = new ClassScanner().scan(serviceImplPackages, annotaion);
        Class<?> proxy = CollectionHelper.getFirstOptional(impl, iface::isAssignableFrom)
                .orElse(null);
        Object bean = getBean(proxy);
        if(bean == null){
            return null;
        }
        EasyGrpcStub stub = proxyFactory.getStub(bean, iface);
        return stub;
    }


}
