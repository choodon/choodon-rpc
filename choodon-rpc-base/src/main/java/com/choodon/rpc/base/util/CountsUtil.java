package com.choodon.rpc.base.util;

import com.google.common.util.concurrent.AtomicLongMap;

public class CountsUtil {


	private static AtomicLongMap<String> counts = AtomicLongMap.create();

	/**
	 * 对指定的key记录活跃连接数加1
	 * 
	 * @param key
	 *            唯一标识符
	 * @return 当前活跃并发数量
	 */
	public static long beginActiveCounts(String key) {
		// 加1操作
		return counts.incrementAndGet(key);
	}

	/**
	 * 对指定的key记录活跃连接数减1
	 * 
	 * @param key
	 *            唯一标识符
	 * @return 当前活跃并发数量
	 */
	public static long endActiveCounts(String key) {
		// 减1操作
		if (counts.get(key) > 0) {
			return counts.decrementAndGet(key);
		} else {
			return 0;
		}
	}

	/**
	 * 
	 * @param key
	 *            客户端传入的key
	 * @return 活跃连接数
	 */
	public static long getActive(String key) {
		return counts.get(key);
	}
	
	/**
	 * 
	 * @param key
	 *            客户端传入的key
	 * @return 活跃连接数
	 */
	public static long remove(String key) {
		return counts.remove(key);
	}

}
