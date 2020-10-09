package com.cht.easygrpc.remoting.iface;

/**
 * @author : chenhaitao934
 * @date : 4:03 下午 2020/10/9
 */
public interface IServiceInitializer {

    default void init() {
    }

    Object getImpl(Class<?> iface);

    default IInvokeHandler getHandler(Class<?> iface) {
        return null;
    }

    default void easyGrpcPostProcessor(){}
}
