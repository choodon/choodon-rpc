package com.choodon.rpc.base.util;
public class SystemUtil {
	public static int getProcessorCoreSize() {
		return Runtime.getRuntime().availableProcessors();
	}

}
