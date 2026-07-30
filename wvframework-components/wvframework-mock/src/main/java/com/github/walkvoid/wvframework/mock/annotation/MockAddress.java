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
@MockString(configKey = "mock.address")
public @interface MockAddress {

    /**
     * 地址级别：province（省）、city（市）、district（区）、detail（详细）、full（完整）
     */
    Level level() default Level.FULL;

    /**
     * 多语言支持
     * 支持：zh-CN（中文）、en-US（英文）、ja-JP（日文）、ko-KR（韩文）
     * AUTO：自动获取当前环境的多语言配置
     */
    String lang() default "AUTO";

    enum Level {
        PROVINCE,
        CITY,
        DISTRICT,
        DETAIL,
        FULL
    }
}
