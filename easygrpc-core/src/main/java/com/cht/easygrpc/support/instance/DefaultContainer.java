package com.cht.easygrpc.support.instance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : chenhaitao934
 */
public class DefaultContainer extends AbstractContainer{

    private Map<Class<?>, Object> instance = new ConcurrentHashMap<>();

    @Override
    public <T> T createInstance(Class<T> clazz) {
        synchronized (this.instance) {
            Object o = this.instance.get(clazz);
            if(o == null){
                instance.put(clazz, genIntance(clazz));
            }
        }
        return (T) instance.get(clazz);
    }
}
