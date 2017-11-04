package com.choodon.rpc.base.util;

public class StringTools {
	private StringTools() {

	}

	/**
	 * 去除字符串中所有空白符
	 * 
	 * @param src
	 * @return
	 */
	public static String trim(String src) {
		return src.replaceAll("\\s", "");
	}

}
