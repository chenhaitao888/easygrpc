package com.cht.easygrpc.support;

import com.cht.easygrpc.annotation.EasyGrpcService;
import com.cht.easygrpc.enums.FileType;
import com.cht.easygrpc.exception.ReflectException;
import com.cht.easygrpc.helper.FileHelper;
import com.cht.easygrpc.helper.StringHelper;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @author : chenhaitao934
 */
public class ClassScanner implements Scanner {
    public ClassScanner() {
    }

    public Set<Class<?>> scan(String... packages) {
        return scan(packages, null);

    }

    public Set<Class<?>> scan(String[] packages, Class<? extends Annotation> annotation) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return Arrays.stream(packages).map((pkg) -> this.scan(cl, pkg, annotation)).flatMap(Collection::stream).collect(Collectors.toSet());
    }

    private Set<Class<?>> scan(ClassLoader cl, String pkg, Class<? extends Annotation> annotation) {
        String jarPath = pkg.replace('.', File.separatorChar);
        HashSet clzSet = new HashSet();

        try {
            Enumeration resources = cl.getResources(jarPath);

            while(resources.hasMoreElements()) {
                URL resource = (URL)resources.nextElement();
                if (resource.getProtocol().equals("jar")) {
                    JarURLConnection jarConn = (JarURLConnection)resource.openConnection();
                    JarFile jarFile = jarConn.getJarFile();
                    Throwable var9 = null;

                    try {
                        clzSet.addAll(this.findAllClassInJar(cl, jarFile, jarPath, annotation));
                    } catch (Throwable var19) {
                        var9 = var19;
                        throw var19;
                    } finally {
                        if (jarFile != null) {
                            if (var9 != null) {
                                try {
                                    jarFile.close();
                                } catch (Throwable var18) {
                                    var9.addSuppressed(var18);
                                }
                            } else {
                                jarFile.close();
                            }
                        }

                    }
                } else if (resource.getProtocol().equals("file")) {
                    clzSet.addAll(this.findAllClassInFolder(cl, new File(resource.toURI()), pkg, annotation));
                }
            }

            return clzSet;
        } catch (URISyntaxException | IOException var21) {
            throw new ReflectException(var21);
        }
    }

    private Set<Class<?>> findAllClassInFolder(ClassLoader cl, File folder, String pkg, Class<? extends Annotation> annotation) {
        Set<Class<?>> clzSet = new HashSet();
        if (!folder.isDirectory()) {
            return clzSet;
        } else {
            File[] var5 = Optional.ofNullable(folder.listFiles()).orElse(new File[0]);
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                File file = var5[var7];
                if (this.isClassFile(file)) {
                    String className = this.getClassName(file.getName());
                    if (!StringHelper.isEmpty(className)) {
                        try {
                            Class<?> aClass = cl.loadClass(String.join(".", pkg, className));
                            if(annotation == null){
                                clzSet.add(aClass);
                            }else {
                                if(null != aClass.getAnnotation(annotation)){
                                    clzSet.add(aClass);
                                }
                            }

                            //clzSet.add(cl.loadClass(String.join(".", pkg, className)));
                        } catch (ClassNotFoundException var11) {
                            //todo log
                        }
                    }
                } else if (file.isDirectory()) {
                    clzSet.addAll(this.findAllClassInFolder(cl, file, String.join(".", pkg, file.getName()), annotation));
                }
            }

            return clzSet;
        }
    }

    private boolean isClassFile(File file) {
        if (file != null && file.exists() && file.isFile()) {
            return FileHelper.getFileType(file) == FileType.CLASS;
        } else {
            return false;
        }
    }

    private String getClassName(String fileName) {
        return !StringHelper.isEmpty(fileName) && fileName.endsWith(".class") ? fileName.substring(0, fileName.length() - 6) :
                "";
    }

    private Set<Class<?>> findAllClassInJar(ClassLoader cl, JarFile jarFile, String path, Class<? extends Annotation> annotation) {
        Set<Class<?>> clzSet = new HashSet();
        Enumeration entries = jarFile.entries();

        while(entries.hasMoreElements()) {
            JarEntry entry = (JarEntry)entries.nextElement();
            if (entry.getName().startsWith(path) && entry.getName().endsWith(".class")) {
                try {
                    Class<?> aClass = cl.loadClass(entry.getName().replace(".class", "").replace(File.separatorChar, '.'));
                    if(annotation == null){
                        clzSet.add(aClass);
                    }else {
                        if(null != aClass.getAnnotation(annotation)){
                            clzSet.add(aClass);
                        }
                    }


                } catch (ClassNotFoundException var8) {
                    var8.printStackTrace();
                }
            }
        }

        return clzSet;
    }
}
