package com.github.walkvoid.wvframework.httplog.model;

/**
 * HTTP 日志类型枚举
 *
 * @author walkvoid
 */
public enum HttpLogType {

    /**
     * 入站请求（Controller 接收到的请求）
     */
    INBOUND,

    /**
     * 出站请求（Feign / @HttpExchange 发出的请求）
     */
    OUTBOUND
}
