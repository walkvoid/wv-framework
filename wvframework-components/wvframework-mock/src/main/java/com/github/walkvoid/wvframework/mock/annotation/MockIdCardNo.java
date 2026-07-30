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
     * 证件类型：idCard（身份证）、passport（护照）、driverLicense（驾驶证）
     */
    Type type() default Type.ID_CARD;

    /**
     * 多语言支持（不同地区证件格式不同）
     * 支持：zh-CN（中文）、en-US（英文）、ja-JP（日文）等
     * AUTO：自动获取当前环境的多语言配置
     */
    String lang() default "AUTO";

    enum Type {
        ID_CARD,
        PASSPORT,
        DRIVER_LICENSE
    }
}
