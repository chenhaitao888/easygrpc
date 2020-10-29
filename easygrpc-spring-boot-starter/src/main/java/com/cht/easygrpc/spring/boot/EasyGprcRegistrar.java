package com.cht.easygrpc.spring.boot;

import com.cht.easygrpc.spring.boot.processor.EasyGrpcProxProcessor;
import com.cht.easygrpc.spring.boot.util.EasyGprcRegistrationUtil;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author : chenhaitao934
 */
public class EasyGprcRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        EasyGprcRegistrationUtil.registerBeanDefinitionIfNotExists(registry, EasyGrpcProxProcessor.class.getName(),
                EasyGrpcProxProcessor.class);

    }
}
