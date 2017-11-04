package com.choodon.rpc.base.extension;

import java.lang.annotation.*;

/**
 * 
 * @author cqq 2017.03.07
 *
 * @version V1.0
 * 
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Activation {

	/** seq号越小，在返回的list<Instance>中的位置越靠前，尽量使用 0-100以内的数字 */
	int sequence() default 20;

	/** spi 的key，获取spi列表时，根据key进行匹配，当key中存在待过滤的search-key时，匹配成功 */
	String[] key() default "";

	/** 是否支持重试的时候也调用 */
	boolean retry() default true;
}
