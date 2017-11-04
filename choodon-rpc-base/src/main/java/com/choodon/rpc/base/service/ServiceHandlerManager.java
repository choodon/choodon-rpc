package com.choodon.rpc.base.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceHandlerManager {
    private static Map<String, Map<String, ServiceHandler>> packageServiceHandlerContainer = new HashMap<>();
    private static Map<String, ServiceHandler> globalServiceHandlerContainer = new HashMap<>();
    private static Map<String, Boolean> clazzSericeHandledContianer = new ConcurrentHashMap<>();

    public static void addServiceHandler(String packageName, Map<String, ServiceHandler> serviceHandlerContainer) {
        if (packageServiceHandlerContainer.containsKey(packageName)) {
            packageServiceHandlerContainer.get(packageName).putAll(serviceHandlerContainer);
            globalServiceHandlerContainer.putAll(serviceHandlerContainer);
        } else {
            packageServiceHandlerContainer.put(packageName, serviceHandlerContainer);
            globalServiceHandlerContainer.putAll(serviceHandlerContainer);
        }

    }

    public static boolean isServiceHandled(String className) {
        return clazzSericeHandledContianer.get(className);
    }

    public static void setServiceHandled(String classsName) {
        clazzSericeHandledContianer.put(classsName, true);
    }


    public static void addServiceHandler(String packageName, String handlerId, ServiceHandler serviceHandler) {
        if (packageServiceHandlerContainer.containsKey(packageName)) {
            packageServiceHandlerContainer.get(packageName).put(handlerId, serviceHandler);
            globalServiceHandlerContainer.put(handlerId, serviceHandler);
        } else {
            packageServiceHandlerContainer.put(packageName, new HashMap<String, ServiceHandler>());
            packageServiceHandlerContainer.get(packageName).put(handlerId, serviceHandler);
            globalServiceHandlerContainer.put(handlerId, serviceHandler);
        }

    }

    public static void addServiceHandler(String handlerId, ServiceHandler serviceHandler) {
        globalServiceHandlerContainer.put(handlerId, serviceHandler);
    }

    public static Map<String, ServiceHandler> getGlobalServiceHandlerContainer() {
        return globalServiceHandlerContainer;
    }

    public static boolean contains(String packageName) {
        return packageServiceHandlerContainer.containsKey(packageName);
    }

    public static ServiceHandler get(String handlerId) {
        return globalServiceHandlerContainer.get(handlerId);
    }
}
