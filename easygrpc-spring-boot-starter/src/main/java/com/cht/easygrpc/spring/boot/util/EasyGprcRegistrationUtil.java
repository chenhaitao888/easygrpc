package com.cht.easygrpc.spring.boot.util;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.Objects;

/**
 * @author : chenhaitao934
 */
public class EasyGprcRegistrationUtil {

    public static boolean registerBeanDefinitionIfNotExists(BeanDefinitionRegistry registry, String beanName,
                                                            Class<?> beanClass){
        if (registry.containsBeanDefinition(beanName)) {
            return false;
        }
        String[] candidates = registry.getBeanDefinitionNames();
        for (String candidate : candidates) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(candidate);
            if (Objects.equals(beanDefinition.getBeanClassName(), beanClass.getName())) {
                return false;
            }
        }
        BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(beanClass).getBeanDefinition();
        registry.registerBeanDefinition(beanName, beanDefinition);
        return true;
    }
}