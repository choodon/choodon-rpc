package com.choodon.rpc.base.util;

import java.math.BigDecimal;

/**
 * 数字工具类
 *
 * @author michael
 * @since 2018/6/14
 */
public class NumberUtils {
    private NumberUtils() {
    }

    /**
     * 数字相等比较
     * 如果任意一个为null 返回false
     * 适用于 Number 所有的子类 Byte Short Integer Long Double AtomicInteger 等
     * 忽略精度 2.00 2.0 认为相等
     *
     * @param n1
     * @param n2
     * @return
     */
    public static final boolean equals(Number n1, Number n2) {
        if (n1 == null || n2 == null) {
            return false;
        } else {
            BigDecimal num1 = new BigDecimal(n1.toString());
            BigDecimal num2 = new BigDecimal(n2.toString());
            return num1.compareTo(num2) == 0;
        }
    }

    public static final boolean notEquals(Number n1, Number n2) {
        return !equals(n1, n2);
    }
}
