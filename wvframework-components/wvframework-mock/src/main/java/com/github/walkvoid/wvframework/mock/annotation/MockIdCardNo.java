package com.github.walkvoid.wvframework.mock.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mock 身份证号数据注解
 *
 * <p>根据字段语义生成符合业务含义的 Mock 数据</p>
 * <p>生成18位身份证号码</p>
 *
 * @author walkvoid
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MockIdCardNo {

    /**
     * 规则数组，{@link #lang()} = {@code "RULES"} 时生效。
     */
    String[] rules() default {};

    /**
     * 多语言文件 key，默认映射 {@code i18n/mock/idcardno_{locale}.properties}。
     */
    String i18nKey() default "idcardno";

    /**
     * 多语言支持（不同地区证件格式不同）
     * <p>语义见 {@link MockName#lang()}。</p>
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
     * 证件类型：idCard（身份证）、passport（护照）、driverLicense（驾驶证）
     */
    Type type() default Type.ID_CARD;

    enum Type {
        ID_CARD,
        PASSPORT,
        DRIVER_LICENSE
    }
}
