package com.github.walkvoid.wvframework.mock.annotation;

import java.lang.annotation.*;

/**
 * Mock 字符串数据注解
 *
 * <p>基础 Mock 注解，用于生成随机字符串数据</p>
 * <p>支持从多语言配置文件中随机获取值，数据来源可以是配置文件（KEY-VALUE 格式，VALUE 为逗号分隔的列表）</p>
 *
 * @author walkvoid
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(MockStrings.class)
public @interface MockString {

    /**
     * 规则数组，{@link #lang()} = {@code "RULES"} 时生效。
     */
    String[] rules() default {};

    /**
     * 多语言文件 key，默认映射 {@code i18n/mock/string_{locale}.properties}。
     */
    String i18nKey() default "string";

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
     * 固定值列表，逗号分隔的值列表，随机取其中一个。
     */
    String[] values() default {};

    /**
     * 字符串长度范围
     * 格式：min-max，如 "6-10"
     * 当 values 为空时生效
     */
    String length() default "6-20";

    /**
     * 字符集：alpha（字母）、numeric（数字）、alphanumeric（字母+数字）
     */
    String charset() default "alphanumeric";

    /**
     * 前缀
     */
    String prefix() default "";

    /**
     * 后缀
     */
    String suffix() default "";
}