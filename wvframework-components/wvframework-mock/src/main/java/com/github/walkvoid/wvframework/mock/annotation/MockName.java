package com.github.walkvoid.wvframework.mock.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mock 姓名数据注解
 *
 * <p>复合注解，底层基于 @MockString 实现</p>
 * <p>根据字段语义生成符合业务含义的 Mock 数据</p>
 *
 * @author walkvoid
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MockName {

    /**
     * 姓名生成规则数组。
     * <p>仅当 {@link #lang()} 等于 {@code "RULES"} 时生效，随机抽取其中一条
     * 直接传入 {@code RandomUtils.fromRule(...)}。</p>
     * <p>例：{@code rules = {"{王,李,张}[1]{伟,芳,娜}[1-2]"}}</p>
     */
    String[] rules() default {};

    /**
     * 多语言文件 key（不含 locale 后缀）。
     * <p>对应 classpath 资源 {@code i18n/mock/{i18nKey}_{locale}.properties}。</p>
     * <p>前缀（如 {@code "mock."} 或 {@code "mocke."}）会被自动剥离，默认映射到 {@code name_zh_CN.properties}。</p>
     */
    String i18nKey() default "name";

    /**
     * 多语言支持
     * <ul>
     *   <li>{@code zh-CN} / {@code en-US} 等具体 locale —— 读取 {@code i18nKey 对应文件}</li>
     *   <li>{@code AUTO} —— 自动获取当前环境的多语言配置（HTTP Accept-Language / Spring Locale / 配置默认值）</li>
     *   <li>{@code RULES} —— 取 {@link #rules()} 的规则生成（不走多语言文件）</li>
     *   <li>{@code FIXED} —— 固定值，返回 {@link #fixedValue()}</li>
     *   <li>{@code GENER} —— 由业务方自定义的 {@link #generator()} 生成</li>
     *   <li>{@code NONE} 或其它 —— 不使用规则生成，走注解方提供的兜底逻辑</li>
     * </ul>
     */
    String lang() default "AUTO";

    /**
     * 自定义 generator 的 bean 名称，仅当 {@link #lang()} = {@code "GENER"} 时生效。
     */
    String generator() default "";

    /**
     * 固定值，仅当 {@link #lang()} = {@code "FIXED"} 时生效。
     */
    String fixedValue() default "";

    /**
     * 姓名类型：firstName（名）、lastName（姓）、fullName（全名）
     */
    Type type() default Type.FULL_NAME;

    enum Type {
        FIRST_NAME,
        LAST_NAME,
        FULL_NAME
    }
}
