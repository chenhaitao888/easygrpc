package com.cht.easygrpc.spring.boot.annotation;

import java.lang.annotation.*;

/**
 * @author : chenhaitao934
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EasyGrpcAutowired {

    String value();
}
