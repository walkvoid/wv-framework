package com.github.walkvoid.wvframework.mock.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mock 电话号码数据注解
 * 
 * <p>根据字段语义生成符合业务含义的 Mock 数据</p>
 * <p>支持生成手机号、座机等多种格式</p>
 *
 * @author walkvoid
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MockPhone {

    /**
     * 电话类型：mobile（手机）、telephone（座机）、fax（传真）、any（任意）
     */
    Type type() default Type.MOBILE;

    /**
     * 多语言支持（不同地区电话格式不同）
     * 支持：zh-CN（中文）、en-US（英文）、ja-JP（日文）等
     * AUTO：自动获取当前环境的多语言配置
     */
    String lang() default "AUTO";

    enum Type {
        MOBILE,
        TELEPHONE,
        FAX,
        ANY
    }
}
