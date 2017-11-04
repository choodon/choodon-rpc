package com.choodon.rpc.base.util;

import java.lang.reflect.Method;

public class MethodUtil {
	public static String getMethodDes(Method method) {
		StringBuilder sb = new StringBuilder();
		sb.append(method.getName());
		sb.append("(");
		Class<?>[] parameterTypes = method.getParameterTypes();
		for (int i = 0; i < method.getParameterCount(); i++) {
			if (i == 0) {
				sb.append(parameterTypes[i].getCanonicalName());
			} else {
				sb.append(", " + parameterTypes[i].getCanonicalName());
			}

		}
		return sb + ")";
	}
}
