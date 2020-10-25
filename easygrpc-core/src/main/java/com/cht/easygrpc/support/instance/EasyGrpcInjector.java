package com.cht.easygrpc.support.instance;

import com.cht.easygrpc.ec.DefaultEventCenter;
import com.cht.easygrpc.ec.EventCenter;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcInjector {

    private static volatile Injector s_injector;
    private static final Object lock = new Object();

    private static Injector getInjector() {
        if (s_injector == null) {
            synchronized (lock) {
                if (s_injector == null) {
                    try {
                        s_injector = Guice.createInjector(new EasyGrpcModule());
                    } catch (Throwable ex) {
                        throw new RuntimeException("Unable to initialize CustomInjector!", ex);
                    }
                }
            }
        }

        return s_injector;
    }

    public static <T> T getInstance(Class<T> clazz) {
        try {
            return getInjector().getInstance(clazz);
        } catch (Throwable ex) {
            throw new RuntimeException(
                    String.format("Unable to load instance for %s!", clazz.getName()), ex);
        }
    }

    private static class EasyGrpcModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(Container.class).to(DefaultContainer.class).in(Singleton.class);
            bind(EventCenter.class).to(DefaultEventCenter.class).in(Singleton.class);
        }
    }
}
