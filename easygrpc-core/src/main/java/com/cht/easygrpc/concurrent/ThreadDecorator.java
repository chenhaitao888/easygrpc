package com.cht.easygrpc.concurrent;

/**
 * @author : chenhaitao934
 * @date : 8:43 下午 2020/5/24
 */
@FunctionalInterface
public interface ThreadDecorator {
    Runnable decorate(Runnable runnable);
}
