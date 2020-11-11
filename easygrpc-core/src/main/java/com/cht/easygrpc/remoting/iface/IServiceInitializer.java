package com.cht.easygrpc.remoting.iface;

import com.cht.easygrpc.support.stub.EasyGrpcStub;
import com.cht.easygrpc.support.proxy.ProxyFactory;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author : chenhaitao934
 * @date : 4:03 下午 2020/10/9
 */
public interface IServiceInitializer {

    default void init(String[] servicePackages, String[] serviceImplPackages, ProxyFactory proxyFactory) {
        wrap(servicePackages, serviceImplPackages, proxyFactory);
    }

    Set<Class<?>> getService();

    default EasyGrpcStub getStub(Class<?> iface) {
        return null;
    }

    default void easyGrpcPostProcessor(){}

    void wrap(String[] servicePackages, String[] serviceImplPackages, ProxyFactory proxyFactory);

    Object getBean(Class<?> type);

    EasyGrpcStub serviceStub(Class iface, Class<? extends Annotation> annotaion);

}
