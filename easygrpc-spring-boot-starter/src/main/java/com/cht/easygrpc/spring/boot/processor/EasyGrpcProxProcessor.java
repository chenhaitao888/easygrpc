package com.cht.easygrpc.spring.boot.processor;

import com.cht.easygrpc.ec.EventCenter;
import com.cht.easygrpc.ec.EventSubscriber;
import com.cht.easygrpc.spring.boot.annotation.EasyGrpcAutowired;
import com.cht.easygrpc.support.instance.EasyGrpcInjector;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

import static com.cht.easygrpc.constant.ExtRpcConfig.INJECT_EVENT;

/**
 * @author : chenhaitao934
 */
@Component
public class EasyGrpcProxProcessor extends AbstractEasyGrpcProcessor implements PriorityOrdered {

    private EventCenter eventCenter = EasyGrpcInjector.getInstance(EventCenter.class);

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] targetField = targetClass.getDeclaredFields();
        for (Field field : targetField) {
            if (field.isAnnotationPresent(EasyGrpcAutowired.class)) {
                if (!field.getType().isInterface()) {
                    throw new BeanCreationException("Inject field must be declared as an interface:" + field.getName()
                            + " @Class " + targetClass.getName());
                }

                eventCenter.subscribe(new EventSubscriber(bean.getClass().getName() + "inject" + field.getName(),
                        (eventInfo) -> {
                            try {
                                this.handleEasyGprcInjected(field, bean, field.getType());
                            } catch (IllegalAccessException e) {
                                logger.error("{} inject {} proxy failure", targetClass.getName(), field.getName(), e);
                            }
                        }), INJECT_EVENT);

            }
        }
        return bean;
    }

    private void handleEasyGprcInjected(Field field, Object bean, Class<?> type) throws IllegalAccessException {
        try {
            Object instance = container.createInstance(field.getType());
            if(instance == null){
                logger.debug("{} can not create instance", field.getType());
                return;
            }
            field.setAccessible(true);
            field.set(bean, instance);
        } finally {
            field.setAccessible(false);
        }
    }

}
