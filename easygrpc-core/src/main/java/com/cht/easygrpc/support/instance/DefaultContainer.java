package com.cht.easygrpc.support.instance;

import com.cht.easygrpc.EasyGrpcContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : chenhaitao934
 */
public class DefaultContainer extends AbstractContainer{

    private Map<Class<?>, Object> instance = new ConcurrentHashMap<>();

    @Override
    public <T> T createInstance(Class<T> clazz) {
        return createInstance(clazz, null);

    }

    @Override
    public <T> T createInstance(Class<T> clazz, EasyGrpcContext context) {
        if(context != null){
            stubType.put(clazz, context);
        }
        synchronized (this.instance) {
            Object o = this.instance.get(clazz);
            if(o == null){
                instance.put(clazz, genIntance(clazz));
            }
        }
        return (T) instance.get(clazz);
    }
}
