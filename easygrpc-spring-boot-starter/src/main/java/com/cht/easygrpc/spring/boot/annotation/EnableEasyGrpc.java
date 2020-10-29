package com.cht.easygrpc.spring.boot.annotation;

import com.cht.easygrpc.spring.boot.EasyGprcRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author : chenhaitao934
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({EasyGprcRegistrar.class})
public @interface EnableEasyGrpc {
}
