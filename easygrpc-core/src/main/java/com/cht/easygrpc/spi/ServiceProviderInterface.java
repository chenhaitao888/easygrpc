package com.cht.easygrpc.spi;

import com.cht.easygrpc.helper.IOHelper;
import com.cht.easygrpc.helper.StringHelper;
import com.cht.easygrpc.remoting.conf.EasyGrpcCommonConfig;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author : chenhaitao934
 * @date : 1:20 上午 2020/10/12
 */
public class ServiceProviderInterface {

    private static EasyGprcClassLoader easyGprcClassLoader = new EasyGprcClassLoader();;
    private static final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
    private static final String EASYGRPC_DIRECTORY = "META-INF/easygrpc/";

    public static <T> T load(final Class<T> clazz, EasyGrpcCommonConfig config) throws Exception {
        SPI spi = clazz.getAnnotation(SPI.class);
        String dynamicConfigKey = spi.key();
        String defaultValue = spi.value();
        if(StringHelper.isNotEmpty(config.getParameter(dynamicConfigKey))){
            defaultValue = config.getParameter(dynamicConfigKey);
        }
        Map<String, Object> clazzName = getClazzName(clazz);
        String className = (String)clazzName.get(defaultValue);
        return loadClass(className);
    }
    @SuppressWarnings("unchecked")
    public static <T> T loadClass(String clazzName) throws Exception{
        Class<T> loadClass = (Class<T>) easyGprcClassLoader.loadClass(clazzName);
        Constructor<T> constructor = loadClass.getConstructor();
        return (T) constructor.newInstance();
    }
    public static Map<String, Object> getClazzName(final Class<?> clazz) throws Exception {
        EasyGprcClassLoader.setSystemClassLoader(systemClassLoader);
        Enumeration<URL> configs = easyGprcClassLoader.getResources(EASYGRPC_DIRECTORY + clazz.getName());
        Map<String, Object> clazzNameMap = null;
        while (configs.hasMoreElements()) {
            URL url = configs.nextElement();
            clazzNameMap = IOHelper.readUrlStream(url);
        }
        return clazzNameMap;
    }
    public static void checkParams(final Class<?> clazz){
        if (clazz == null)
            throw new IllegalArgumentException("type == null");
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException(" type(" + clazz + ") is not interface!");
        }
        if (!clazz.isAnnotationPresent(SPI.class)) {
            throw new IllegalArgumentException("type(" + clazz + ") is not extension, because WITHOUT @"
                    + SPI.class.getSimpleName() + " Annotation!");
        }
    }
}
