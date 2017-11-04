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
@Target({ ElementType.TYPE })
public @interface SpiMeta {
	String name() default "";
}
