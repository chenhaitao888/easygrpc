package com.cht.easygrpc.support.instance;

/**
 * @author : chenhaitao934
 */
public interface Container {

    <T> T createInstance(Class<T> clazz);

}
