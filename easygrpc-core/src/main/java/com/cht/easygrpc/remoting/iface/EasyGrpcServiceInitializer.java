package com.cht.easygrpc.remoting.iface;

import com.cht.easygrpc.helper.CollectionHelper;
import com.cht.easygrpc.support.ClassScanner;


import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcServiceInitializer implements IServiceInitializer{

    private String[] servicePackages;
    private Set<Class<?>> implClazz = null;

    public EasyGrpcServiceInitializer(String[] servicePackages) {
        this.servicePackages = servicePackages;
    }

    public Object getImpl(Class<?> iface) {
        /*if (implClazz == null) {
            implClazz = new ClassScanner().scan(servicePackages)
                    .stream()
                    .filter(clazz -> !clazz.isInterface() && !clazz.isAnnotation() && !clazz.isAnonymousClass()
                            && !clazz.isEnum())
                    .collect(Collectors.toSet());
        }

        return getProxy(CollectionHelper.getFirstOptional(implClazz, iface::isAssignableFrom)
                .orElse(null), iface);*/

        try {
            return EasyGrpcTestImpl.class.newInstance(); //todo
        } catch (Exception e) {
            return null;
        }

    }

    private Object getProxy(Class<?> implCls, Class<?> iface) {
        if (implCls == null) {
            return null;
        } else {
            try {
                return implCls.newInstance(); //todo  proxy
            } catch (Exception var6) {
                return null;
            }
        }
    }
}
