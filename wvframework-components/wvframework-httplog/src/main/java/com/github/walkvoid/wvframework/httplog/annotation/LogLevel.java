package com.github.walkvoid.wvframework.httplog.annotation;

/**
 * HTTP 日志级别枚举
 *
 * @author walkvoid
 */
public enum LogLevel {

    /**
     * 最细粒度，记录完整请求/响应细节
     */
    TRACE,

    /**
     * 调试级别，记录请求摘要信息
     */
    DEBUG,

    /**
     * 默认级别，记录请求基本信息和状态码
     */
    INFO,

    /**
     * 告警级别，用于慢请求记录
     */
    WARN,

    /**
     * 错误级别，用于异常请求记录
     */
    ERROR
}
