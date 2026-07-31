package com.github.walkvoid.wvframework.mock.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mock 地址数据注解
 *
 * <p>复合注解，底层基于 @MockString 实现</p>
 * <p>根据字段语义生成符合业务含义的 Mock 数据</p>
 *
 * @author walkvoid
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MockAddress {

    /**
     * 规则数组，{@link #lang()} = {@code "RULES"} 时生效。
     */
    String[] rules() default {};

    /**
     * 多语言文件 key，默认映射 {@code i18n/mock/address_{locale}.properties}。
     */
    String i18nKey() default "address";

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
     * 地址级别：province（省）、city（市）、district（区）、detail（详细）、full（完整）
     */
    Level level() default Level.FULL;

    enum Level {
        PROVINCE,
        CITY,
        DISTRICT,
        DETAIL,
        FULL
    }
}