package com.cht.easygrpc.support;

import com.cht.easygrpc.annotation.EasyGrpcService;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author : chenhaitao934
 */
public interface Scanner {
    Set<Class<?>> scan(String... var1);

}
