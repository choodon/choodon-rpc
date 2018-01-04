package com.choodon.rpc.base.service;

import com.choodon.rpc.base.common.RPCConstants;
import com.choodon.rpc.base.common.URL;
import com.choodon.rpc.base.common.URLParamType;
import com.choodon.rpc.base.exception.RPCFrameworkException;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.service.annotation.Service;
import com.choodon.rpc.base.service.annotation.ServiceImpl;
import com.choodon.rpc.base.util.MethodUtil;
import com.choodon.rpc.base.util.URLTools;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceManager {
    private final static Map<String, List<URL>> packageServiceURLContainer = new HashMap<>();
    private final static Map<String, URL> globalServiceURLContainer = new HashMap<>();
    private final static Map<Class, Class> class2InterfaceMapperContainer = new ConcurrentHashMap<>();

    public static List<URL> get(String packageName) {
        if (packageServiceURLContainer.containsKey(packageName)) {
            return packageServiceURLContainer.get(packageName);
        } else {
            packageServiceURLContainer.put(packageName, init(packageName));
            return packageServiceURLContainer.get(packageName);
        }

    }


    private static List<URL> init(String packageName) throws RPCFrameworkException {
        List<URL> serviceURLs = new LinkedList<>();
        Map<Class, Class> class2InterfaceMapperContainer = RPCServiceClassManager.getServiceClass2InterfaceMapperContainer(packageName);
        String handlerId;
        StringBuilder serviceDes;
        for (Entry<Class, Class> entry : class2InterfaceMapperContainer.entrySet()) {
            serviceDes = getServiceDes(entry);
            Class<?> clazz = entry.getKey();
            Class interfaceClass = entry.getValue();
            for (Method method : interfaceClass.getMethods()) {
                handlerId = serviceDes.append("-").append(MethodUtil.getMethodDes(method)).toString();
                ServiceHandler serviceHandler = ClassFactory.getServiceHandler(clazz, method);
                ServiceHandlerManager.addServiceHandler(packageName, handlerId,
                        serviceHandler);
            }
            ServiceHandlerManager.setServiceHandled(entry.getKey().getCanonicalName());
            serviceURLs.add(createServiceURL(entry));
        }
        return serviceURLs;

    }

    public static void handle(Class<?> clazz) {

        if (clazz.isAnnotationPresent(ServiceImpl.class)) {
            Object obj;
            Class<?>[] interfaceClasses = clazz.getInterfaces();
            Class interfaceClazz = null;
            boolean isAnnotationServicePresent = false;
            for (Class interfaceClass : interfaceClasses) {
                if (interfaceClass.isAnnotationPresent(Service.class)) {
                    try {
                        interfaceClazz = interfaceClass;
                        obj = clazz.getDeclaredConstructor().newInstance();
                    } catch (IllegalAccessException e) {
                        LoggerUtil.error("Service class`s constructor should be public  ", e);
                        throw new RPCFrameworkException("Service class`s constructor should be public  ");
                    } catch (Exception e) {
                        LoggerUtil.error("Service instantite exception ", e);
                        throw new RPCFrameworkException("Service instantite instance exception .");
                    }
                    RPCServiceClassManager.addServieInstance(clazz, obj);
                    break;
                }
            }
            if (isAnnotationServicePresent) {
                ServiceImpl serviceImpl = clazz.getAnnotation(ServiceImpl.class);
                String serviceId = serviceImpl.serviceId();
                if (serviceId == null) {
                    serviceId = clazz.getCanonicalName();
                }
                String serviceDes = new StringBuilder(serviceId + "-").append(serviceImpl.group() + "-").append(serviceImpl.version()).toString();
                for (Method method : interfaceClazz.getMethods()) {
                    String handlerId = new StringBuilder(serviceDes).append("-").append(MethodUtil.getMethodDes(method)).toString();
                    ServiceHandler serviceHandler = ClassFactory.getServiceHandler(clazz, method);
                    ServiceHandlerManager.addServiceHandler(handlerId, serviceHandler);
                }
                ServiceHandlerManager.setServiceHandled(clazz.getCanonicalName());
            } else {
                throw new RPCFrameworkException(clazz.getCanonicalName() + " `s interfaces do not hava  a  interface with Service.class Annotation.");
            }
        } else {
            throw new RPCFrameworkException("service class is not Annotation (ServiceImpl.class)Present.");
        }
    }


    private static URL createServiceURL(Entry<Class, Class> entry) {
        if (globalServiceURLContainer.containsKey(getServiceDes(entry).toString())) {
            return globalServiceURLContainer.get(getServiceDes(entry).toString());
        }
        Class<?> clazz = entry.getKey();
        Class<?> interfaceClass = entry.getValue();
        ServiceImpl serviceImpl = clazz.getAnnotation(ServiceImpl.class);
        String serviceId = serviceImpl.serviceId();
        if (serviceId == null) {
			serviceId = interfaceClass.getCanonicalName();
		}
        Map<String, String> parameters = new HashMap<>();
        parameters.put(URLParamType.serviceId.getName(), serviceImpl.serviceId());
        parameters.put(URLParamType.group.getName(), serviceImpl.group());
        parameters.put(URLParamType.version.getName(), serviceImpl.version());
        URL serviceURL = URLTools.createServiceURL(RPCConstants.SERVICE_PROTOCOL, serviceId, serviceImpl.group(), serviceImpl.version(), parameters);
        globalServiceURLContainer.put(getServiceDes(entry).toString(), serviceURL);
        return serviceURL;
    }

    private static StringBuilder getServiceDes(Entry<Class, Class> entry) {
        Class<?> clazz = entry.getKey();
        ServiceImpl serviceImpl = clazz.getAnnotation(ServiceImpl.class);
        String serviceId = serviceImpl.serviceId();
        if (serviceId == null) {
            serviceId = clazz.getCanonicalName();
        }
        return new StringBuilder(serviceId + "-").append(serviceImpl.group() + "-").append(serviceImpl.version());

    }

}
