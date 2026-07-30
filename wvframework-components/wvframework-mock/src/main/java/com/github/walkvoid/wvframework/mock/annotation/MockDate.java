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

    /**
     * 多语言支持
     * 支持：zh-CN（中文）、en-US（英文）、ja-JP（日文）、ko-KR（韩文）
     * AUTO：自动获取当前环境的多语言配置
     */
    String lang() default "AUTO";
}
