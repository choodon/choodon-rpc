package com.choodon.rpc.base.service;

import com.choodon.rpc.base.exception.RPCFrameworkException;
import com.choodon.rpc.base.log.LoggerUtil;
import com.choodon.rpc.base.service.annotation.Service;
import com.choodon.rpc.base.service.annotation.ServiceImpl;
import com.choodon.rpc.base.util.MethodUtil;

import java.lang.reflect.Method;

public class ClassFactory {


    public static ServiceHandler getServiceHandler(Class<?> clazz, Method method) {

        try {
            return (ServiceHandler) getHanderClass(clazz, method).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            LoggerUtil.error("Service proxy initialize instance excepiton", e);
            throw new RPCFrameworkException("Service proxy initialize instance excepiton");
        }
    }

    @SuppressWarnings("rawtypes")
    public static Class getHanderClass(Class clazz, Method method) {
        return CompilerManager.compiler(createHandlerCodes(clazz, method));
    }

    @SuppressWarnings("rawtypes")
    private static String createHandlerCodes(Class clazz, Method method) {
        StringBuilder sb = new StringBuilder();
        String packageName = clazz.getName();
        sb.append("public class " + packageName.replaceAll("\\.", "_") + "_" + getMethodKey(method)
                + "_Handler implements " + ServiceHandler.class.getName() + " {\n");
        sb.append("    private " + clazz.getName() + " service = (" + clazz.getName() + ")" + RPCServiceClassManager.class.getName() + ".getServiceInstance(" + clazz.getName() + ".class);\n\n");
        sb.append("    @Override\n");
        sb.append("    public Object doHandler(Object[] parameters) {\n");
        StringBuilder parametersStr = new StringBuilder();
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Class<?>[] ParameterTypes = method.getParameterTypes();
            if (i == 0) {
                parametersStr.append("(" + ParameterTypes[i].getName() + ")parameters[" + i + "]");
            } else {
                parametersStr.append(", (" + ParameterTypes[i].getName() + ")parameters[" + i + "]");
            }

        }
        sb.append("        return service." + method.getName() + "(" + parametersStr + ");\n");
        sb.append("    }\n\n");
        sb.append("}");
        LoggerUtil.info(clazz.getName() + " `s proxy codes :\n" + sb);
        return sb.toString();
    }

    @SuppressWarnings({"rawtypes"})
    public static String getParametersCodes(Class clazz, Method method) {
        StringBuilder sb = new StringBuilder();
        Class<?>[] types = method.getParameterTypes();
        for (int i = 0; i < types.length; i++) {
            if (i != 0)
                sb.append(", ");
            sb.append("parameters[" + i + "]");
        }
        return sb.toString();
    }

    public static String getMethodKey(Method method) {
        return MethodUtil.getMethodDes(method).trim().replaceAll("\\[", "").replaceAll("]", "").replaceAll("\\(", "_")
                .replaceAll("\\)", "_").replaceAll("\\.", "_");

    }
}
