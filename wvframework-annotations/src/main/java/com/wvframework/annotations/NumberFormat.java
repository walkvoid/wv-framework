package com.wvframework.annotations;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author walkvoid
 * @version v0.0.1
 * @desc 该注解在标识在一个数值类型的field上时，支持将一个数序列化成指定的格式
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
public @interface NumberFormat {

    /**
     * 需要乘上的数，等价于扩大的倍数
     * @return
     */
    int multiply() default 1;

    /**
     * 需要缩小的数，等价于缩小的倍数
     * @return
     */
    int divide() default 1;

    /**
     * 保留小数点后的位数
     * @return
     */
    int scale() default 2;

    /**
     * 是否忽略反序列化
     * @return
     */
    boolean ignoreDeser() default false;

    /**
     * 需要拼接上去的前缀
     * @return
     */
    String prefix() default "";

    /**
     * 需要拼接上去的后缀
     * @return
     */
    String suffix() default "";


}
