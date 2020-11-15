package com.cht.easygrpc.support.instance;

import com.cht.easygrpc.EasyGrpcContext;

/**
 * @author : chenhaitao934
 */
public interface Container {

    <T> T createInstance(Class<T> clazz);

    <T> T createInstance(Class<T> clazz, EasyGrpcContext context);

    <T> void bindContext(Class<T> claz, EasyGrpcContext context);

    <T> T createStreamInstance(Class<T> clazz);

    <T> T createStreamInstance(Class<T> clazz, EasyGrpcContext context);

}
