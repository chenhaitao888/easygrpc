package com.cht.easygrpc.support.builder;

/**
 * @author : chenhaitao934
 */
@FunctionalInterface
public interface ConsumerOne<T, P1> {
    void accept(T t, P1 p1);
}
