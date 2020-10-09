package com.cht.easygrpc.concurrent;

import java.util.concurrent.ThreadFactory;

/**
 * @author : chenhaitao934
 * @date : 8:30 下午 2020/5/24
 */
public class CustomizeThreadFactory extends CustomizeThreadCreator implements ThreadFactory {

    public CustomizeThreadFactory(String threadNamePrefix) {
        super(threadNamePrefix);
    }

    public CustomizeThreadFactory() {
        super();
    }

    @Override
    public Thread newThread(Runnable runnable) {
        return createThread(runnable);
    }
}
