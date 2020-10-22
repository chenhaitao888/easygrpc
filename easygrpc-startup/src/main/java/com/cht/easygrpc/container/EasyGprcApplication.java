package com.cht.easygrpc.container;

import com.cht.easygrpc.support.instance.Container;
import com.cht.easygrpc.support.instance.EasyGrpcInjector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : chenhaitao934
 */
public class EasyGprcApplication {

    private static Container container = EasyGrpcInjector.getInstance(Container.class);

    public static <T> T getBean(Class<T> iface){
        return container.createInstance(iface);
    }
}
