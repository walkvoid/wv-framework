package com.github.walkvoid.wvframework.mock.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mock 邮箱数据注解
 * 
 * <p>根据字段语义生成符合业务含义的 Mock 数据</p>
 *
 * @author walkvoid
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MockEmail {

    /**
     * 邮箱域名
     * 为空时随机选择常用域名
     */
    String[] rules() default {"@alnum[5-10]@at@letters[4-8]@idot{com,cn}"};

    /**
     * 姓名类型：firstName（名）、lastName（姓）、fullName（全名）
     */
    String i18nKey() default "mocke.email";

    /**
     * 多语言支持
     * 支持：zh-CN（中文）、en-US（英文）、ja-JP（日文）、ko-KR（韩文）
     * AUTO：自动获取当前环境的多语言配置
     * NONE：不使用多语言配置
     */
    String lang() default "NONE";
}
