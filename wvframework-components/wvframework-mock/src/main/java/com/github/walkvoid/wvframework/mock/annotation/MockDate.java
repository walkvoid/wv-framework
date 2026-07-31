package com.github.walkvoid.wvframework.mock.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mock 日期时间数据注解
 *
 * <p>根据字段语义生成符合业务含义的 Mock 数据</p>
 *
 * @author walkvoid
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MockDate {

    /**
     * 规则数组，{@link #lang()} = {@code "RULES"} 时生效。
     */
    String[] rules() default {};

    /**
     * 多语言文件 key，默认映射 {@code i18n/mock/date_{locale}.properties}。
     */
    String i18nKey() default "date";

    /**
     * 多语言支持，语义见 {@link MockName#lang()}。
     */
    String lang() default "AUTO";

    /**
     * 自定义 generator 的 bean 名称，仅 {@code GENER} 模式生效。
     */
    String generator() default "";

    /**
     * 固定值，仅 {@code FIXED} 模式生效。
     */
    String fixedValue() default "";

    /**
     * 日期格式
     * 例如：yyyy-MM-dd、yyyy-MM-dd HH:mm:ss、yyyy-MM-dd'T'HH:mm:ss 等
     */
    String format() default "yyyy-MM-dd";

    /**
     * 日期范围起始
     * 格式：yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss
     * 默认：当前日期向前5年
     */
    String from() default "";

    /**
     * 日期范围结束
     * 格式：yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss
     * 默认：当前日期
     */
    String to() default "";

    /**
     * 是否包含时间部分
     */
    boolean withTime() default false;
}
