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
     * 配置文件的 key
     * 从指定配置文件中获取值，值为逗号分隔的列表，随机取其中一个
     */
    String configKey() default "";

    /**
     * 固定值列表（优先级高于 configKey）
     * 逗号分隔的值列表，随机取其中一个
     */
    String[] values() default {};

    /**
     * 字符串长度范围
     * 格式：min-max，如 "6-10"
     * 当 configKey 和 values 都为空时生效
     */
    String length() default "6-20";

    /**
     * 字符集：alpha（字母）、numeric（数字）、alphanumeric（字母+数字）
     */
    String charset() default "alphanumeric";

    /**
     * 多语言支持
     * 支持：zh-CN（中文）、en-US（英文）、ja-JP（日文）、ko-KR（韩文）
     * AUTO：自动获取当前环境的多语言配置
     */
    String lang() default "AUTO";

    /**
     * 前缀
     */
    String prefix() default "";

    /**
     * 后缀
     */
    String suffix() default "";
}
