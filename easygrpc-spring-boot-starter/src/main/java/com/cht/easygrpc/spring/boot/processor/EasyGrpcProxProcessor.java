package com.cht.easygrpc.spring.boot.processor;

import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

/**
 * @author : chenhaitao934
 */
public class EasyGrpcProxProcessor extends AbstractEasyGrpcProcessor implements PriorityOrdered {


    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
