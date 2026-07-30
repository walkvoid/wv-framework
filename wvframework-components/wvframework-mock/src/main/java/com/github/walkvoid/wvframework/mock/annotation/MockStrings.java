package com.github.walkvoid.wvframework.mock.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mock 字符串数据注解
 * 
 * <p>基础 Mock 注解，用于生成随机字符串数据</p>
 * <p>支持从多语言配置文件中随机获取值，数据来源可以是配置文件（KEY-VALUE 格式，VALUE 为逗号分隔的列表）</p>
 *
 * @author walkvoid
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MockStrings {

    MockString[] value();
}
