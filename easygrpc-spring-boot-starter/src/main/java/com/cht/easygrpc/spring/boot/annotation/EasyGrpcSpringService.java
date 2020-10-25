package com.cht.easygrpc.spring.boot.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author : chenhaitao934
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface EasyGrpcSpringService {
    Class<?>[] interfaces() default {};
}
