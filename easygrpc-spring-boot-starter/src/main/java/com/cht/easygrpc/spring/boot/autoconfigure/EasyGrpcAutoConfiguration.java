package com.cht.easygrpc.spring.boot.autoconfigure;

import com.cht.easygrpc.remoting.iface.IServiceInitializer;
import com.cht.easygrpc.spring.boot.EasyGrpcServiceSpringInitializer;
import com.cht.easygrpc.spring.boot.EasyGrpcSpringbootStartup;
import com.cht.easygrpc.spring.boot.config.EasyGrpcConstants;
import com.cht.easygrpc.spring.boot.processor.EasyGrpcProxProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : chenhaitao934
 */
@Configuration
@EnableConfigurationProperties({EasyGrpcProperties.class})
@ConditionalOnProperty(EasyGrpcConstants.EASY_GRPC_BOOTSTRAP_ENABLED)
public class EasyGrpcAutoConfiguration {

    @Bean
    public IServiceInitializer initializer() {
        return new EasyGrpcServiceSpringInitializer();
    }


    @Bean(initMethod = "start")
    @ConditionalOnMissingBean
    public EasyGrpcSpringbootStartup easyGrpcSpringbootStartup(EasyGrpcProperties properties){
        return new EasyGrpcSpringbootStartup(properties, initializer());
    }
}
