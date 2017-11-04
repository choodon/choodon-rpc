package com.choodon.rpc.base.service;

import com.choodon.rpc.base.exception.RPCFrameworkException;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.service.annotation.Service;
import com.choodon.rpc.base.service.annotation.ServiceImpl;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class RPCServiceClassManager {
    private final static Map<Class, Object> servieInstanceContainer = new ConcurrentHashMap<>();

    public static Object getServiceInstance(Class clazz) {
        return servieInstanceContainer.get(clazz);
    }

    public static void addServieInstance(Class clazz, Object object) {
        servieInstanceContainer.put(clazz, object);
    }


    public static Map<Class, Class> getServiceClass2InterfaceMapperContainer(String packageName) {
        Set<Class<?>> classSet = new HashSet<Class<?>>();

        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader()
                    .getResources(packageName.replace(".", "/"));
            URL url = null;
            String protocol = null;
            JarURLConnection jarURLConnection = null;
            JarFile jarFile = null;
            while (urls.hasMoreElements()) {
                url = urls.nextElement();
                if (null != url) {
                    protocol = url.getProtocol();
                    if (protocol.equals("file")) {
                        String packagePath = url.getPath().replaceAll("%20", " ");
                        addClass(classSet, packagePath, packageName);
                    } else if (protocol.equals("jar")) {
                        jarURLConnection = (JarURLConnection) url.openConnection();
                        if (null != jarURLConnection) {
                            jarFile = jarURLConnection.getJarFile();
                            if (null != jarFile) {
                                Enumeration<JarEntry> jarEntries = jarFile.entries();
                                while (jarEntries.hasMoreElements()) {
                                    JarEntry jarEntry = jarEntries.nextElement();
                                    String jarEntryName = jarEntry.getName();
                                    if (jarEntryName.equals(".class")) {
                                        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf("."))
                                                .replace("/", ".");
                                        try {
                                            classSet.add(Class.forName(className, false,
                                                    Thread.currentThread().getContextClassLoader()));
                                        } catch (ClassNotFoundException e) {
                                            LoggerUtil.error(className + " Class Not Found ", e);
                                            throw new RPCFrameworkException(className + " Class Not Found ");
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            LoggerUtil.error("load class files IOException", e);
            throw new RPCFrameworkException("load class files IOException");
        }
        Object obj = null;
        Map<Class, Class> class2interfaceMapperContaner = new ConcurrentHashMap<>();
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(ServiceImpl.class)) {
                Class interfaceClazz = null;
                Class<?>[] interfaceClasses = clazz.getInterfaces();
                boolean isAnnotationServicePresent = false;
                for (Class interfaceClass : interfaceClasses) {
                    if (interfaceClass.isAnnotationPresent(Service.class)) {
                        isAnnotationServicePresent = true;
                        interfaceClazz = interfaceClass;
                        try {
                            obj = clazz.getDeclaredConstructor().newInstance();
                        } catch (IllegalAccessException e) {
                            LoggerUtil.error("Service class`s constructor should be public  ", e);
                            throw new RPCFrameworkException("Service class`s constructor should be public  ");
                        } catch (Exception e) {
                            LoggerUtil.error("Service Instantite exception ", e);
                            throw new RPCFrameworkException("Service instantite instance exception .");
                        }
                        break;
                    }
                }
                if (isAnnotationServicePresent) {
                    servieInstanceContainer.put(clazz, obj);
                    class2interfaceMapperContaner.put(clazz, interfaceClazz);
                } else {
                    throw new RPCFrameworkException(clazz.getCanonicalName() + " `s interfaces do not hava  a  interface with Service.clsss Annotation.");
                }
            }
        }
        return class2interfaceMapperContaner;

    }

    private static void addClass(Set<Class<?>> classSet, String packagePath, String packageName) {
        File[] files = new File(packagePath).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (dir.isFile() && name.endsWith(".class")) || dir.isDirectory();
            }
        });
        String fileName = null;
        String className = null;
        for (File file : files) {
            fileName = file.getName();
            if (file.isFile()) {
                className = fileName.substring(0, fileName.lastIndexOf("."));
                if (StringUtils.isNotEmpty(packageName)) {
                    className = packageName + "." + className;
                }
                try {
                    classSet.add(Class.forName(className, false, Thread.currentThread().getContextClassLoader()));
                } catch (ClassNotFoundException e) {
                    LoggerUtil.error(className + " Class Not Found ", e);
                    throw new RPCFrameworkException(className + " Class Not Found ");
                }
            } else {
                String subPackagePath = fileName;
                if (StringUtils.isNotEmpty(packagePath)) {
                    subPackagePath = packagePath + "/" + subPackagePath;
                }
                String subPackageName = fileName;
                if (StringUtils.isNotEmpty(packageName)) {
                    subPackageName = packageName + "." + subPackageName;
                }
                addClass(classSet, subPackagePath, subPackageName);
            }

        }
    }
}
