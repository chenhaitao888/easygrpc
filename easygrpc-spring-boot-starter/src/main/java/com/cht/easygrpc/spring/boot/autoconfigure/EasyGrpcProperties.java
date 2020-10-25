package com.cht.easygrpc.spring.boot.autoconfigure;

import com.cht.easygrpc.config.EasyGrpcConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : chenhaitao934
 */
@ConfigurationProperties(prefix = EasyGrpcProperties.EASY_GRPC_PREFIX)
@ConditionalOnMissingBean({EasyGrpcConfig.class})
public class EasyGrpcProperties extends EasyGrpcConfig {

    public static final String EASY_GRPC_PREFIX = "easygrpc";


}
