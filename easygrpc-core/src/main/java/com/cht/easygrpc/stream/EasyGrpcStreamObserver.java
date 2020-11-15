package com.cht.easygrpc.stream;

import java.lang.reflect.Type;

/**
 * @author : chenhaitao934
 * @date : 1:57 下午 2020/10/9
 */
public interface EasyGrpcStreamObserver<V> {


    void onNext(V value);


    void onError(Throwable t);


    void onCompleted();

    default Type getReturnType() {
        return null;
    }

}
