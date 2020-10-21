package com.cht.easygrpc.spi;

import com.cht.easygrpc.helper.IOHelper;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : chenhaitao934
 */
public class EasyGprcClassLoader extends ClassLoader{

    private Map<String, Class<?>> classMap = new HashMap<>();
    private static ClassLoader myClassLoader;

    public synchronized static void setSystemClassLoader(ClassLoader systemClassLoader) {
        EasyGprcClassLoader.setMyClassLoader(systemClassLoader);
    }
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = classMap.get(name);
        if (clazz != null) {
            return clazz;
        }
        synchronized (getClassLoadingLock(name)) {
            clazz = findLoadedClass(name);
            if (clazz == null) {
                clazz = myClassLoader.loadClass(name);
                if(clazz == null){
                    clazz = findClass(name);
                }
            }
            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            FileInputStream in = new FileInputStream(nameToPath(name, false));
            byte[] bytes = IOHelper.readStreamBytesAndClose(in);
            Class<?> defineClass = defineClass(name, bytes, 0, bytes.length);
            if(defineClass != null){
                classMap.put(name, defineClass);
            }
            return defineClass;
        } catch (Exception e) {
            throw new ClassNotFoundException();
        }
    }

    private String nameToPath(String binaryName, boolean withLeadingSlash) {
        StringBuilder path = new StringBuilder(7 + binaryName.length());
        if (withLeadingSlash) {
            path.append('/');
        }
        path.append(binaryName.replace('.', '/'));
        path.append(".class");
        return path.toString();
    }
    public static ClassLoader getMyClassLoader() {
        return myClassLoader;
    }
    public static void setMyClassLoader(ClassLoader myClassLoader) {
        EasyGprcClassLoader.myClassLoader = myClassLoader;
    }
}
