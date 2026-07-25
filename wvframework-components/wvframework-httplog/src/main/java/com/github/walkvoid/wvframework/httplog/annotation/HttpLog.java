package com.github.walkvoid.wvframework.httplog.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * HTTP 请求日志注解
 *
 * <p>可标注在：
 * <ol>
 *   <li>Controller 方法 — 记录入站请求</li>
 *   <li>Feign Client 方法 — 记录出站请求</li>
 *   <li>@HttpExchange 接口方法 — 记录声明式 HTTP 出站请求</li>
 * </ol>
 *
 * @author walkvoid
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpLog {

    /**
     * 日志描述/业务标识
     */
    String value() default "";

    /**
     * 是否启用日志记录（可覆盖全局开关）
     */
    boolean enabled() default true;

    /**
     * 是否记录请求信息（URL、Header、参数）
     */
    boolean logRequest() default true;

    /**
     * 是否记录请求体内容
     */
    boolean logRequestBody() default true;

    /**
     * 是否记录响应体内容
     */
    boolean logResponseBody() default true;

    /**
     * 日志级别
     */
    LogLevel logLevel() default LogLevel.INFO;

    /**
     * 请求体/响应体最大记录长度（字符），超出截断
     */
    int maxBodyLength() default 2048;

    /**
     * 不记录到日志的 Header 名称（如 Authorization）
     */
    String[] excludeHeaders() default {};

    /**
     * 需要脱敏的 JSON 字段名（如 "password", "token"）
     */
    String[] maskFields() default {};

    /**
     * 慢请求阈值（毫秒），超过此阈值以 WARN 级别记录
     */
    long slowThreshold() default 3000;
}
