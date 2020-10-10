package com.cht.easygrpc.spi;

import java.lang.annotation.*;

/**
 * @author : chenhaitao934
 * @date : 8:08 下午 2020/10/9
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SPI {

    String key() default "";
    String value() default "";
}
