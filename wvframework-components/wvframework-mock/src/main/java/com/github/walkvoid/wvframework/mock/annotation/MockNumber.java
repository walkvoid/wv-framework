package com.github.walkvoid.wvframework.mock.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mock 数值数据注解
 * 
 * <p>根据字段语义生成符合业务含义的 Mock 数据</p>
 * <p>支持整数、浮点数、范围随机等</p>
 *
 * @author walkvoid
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MockNumber {

    /**
     * 最小值
     */
    long min() default 0;

    /**
     * 最大值
     */
    long max() default 100;

    /**
     * 是否为浮点数
     */
    boolean decimal() default false;

    /**
     * 浮点数小数位数
     */
    int decimals() default 2;

    /**
     * 数值类型：integer（整数）、long（长整数）、float（浮点）、double（双精度）、bigDecimal（BigDecimal）
     */
    Type type() default Type.INTEGER;

    enum Type {
        INTEGER,
        LONG,
        FLOAT,
        DOUBLE,
        BIG_DECIMAL
    }
}
