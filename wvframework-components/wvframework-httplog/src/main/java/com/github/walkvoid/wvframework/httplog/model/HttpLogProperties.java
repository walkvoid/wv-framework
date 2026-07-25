package com.github.walkvoid.wvframework.httplog.model;

import com.github.walkvoid.wvframework.httplog.annotation.LogLevel;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * HTTP 日志配置属性
 *
 * @author walkvoid
 */
@ConfigurationProperties(prefix = "wv.httplog")
public class HttpLogProperties {

    /** 全局开关 */
    private boolean enabled = true;

    /** 默认是否记录请求信息 */
    private boolean logRequest = true;

    /** 默认是否记录请求体 */
    private boolean logRequestBody = true;

    /** 默认是否记录响应体（生产建议关闭） */
    private boolean logResponseBody = false;

    /** 默认最大 Body 记录长度 */
    private int maxBodyLength = 2048;

    /** 默认慢请求阈值（ms） */
    private long slowThreshold = 3000;

    /** 默认日志级别 */
    private LogLevel logLevel = LogLevel.INFO;

    /** 全局排除的 Header */
    private List<String> excludeHeaders = new ArrayList<>();

    /** 全局脱敏字段 */
    private List<String> maskFields = new ArrayList<>();

    /** Feign 拦截配置 */
    private FeignConfig feign = new FeignConfig();

    /** @HttpExchange 拦截配置 */
    private HttpExchangeConfig httpExchange = new HttpExchangeConfig();

    /** Controller 拦截配置 */
    private ControllerConfig controller = new ControllerConfig();

    // ===== Getters and Setters =====

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isLogRequest() {
        return logRequest;
    }

    public void setLogRequest(boolean logRequest) {
        this.logRequest = logRequest;
    }

    public boolean isLogRequestBody() {
        return logRequestBody;
    }

    public void setLogRequestBody(boolean logRequestBody) {
        this.logRequestBody = logRequestBody;
    }

    public boolean isLogResponseBody() {
        return logResponseBody;
    }

    public void setLogResponseBody(boolean logResponseBody) {
        this.logResponseBody = logResponseBody;
    }

    public int getMaxBodyLength() {
        return maxBodyLength;
    }

    public void setMaxBodyLength(int maxBodyLength) {
        this.maxBodyLength = maxBodyLength;
    }

    public long getSlowThreshold() {
        return slowThreshold;
    }

    public void setSlowThreshold(long slowThreshold) {
        this.slowThreshold = slowThreshold;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public List<String> getExcludeHeaders() {
        return excludeHeaders;
    }

    public void setExcludeHeaders(List<String> excludeHeaders) {
        this.excludeHeaders = excludeHeaders;
    }

    public List<String> getMaskFields() {
        return maskFields;
    }

    public void setMaskFields(List<String> maskFields) {
        this.maskFields = maskFields;
    }

    public FeignConfig getFeign() {
        return feign;
    }

    public void setFeign(FeignConfig feign) {
        this.feign = feign;
    }

    public HttpExchangeConfig getHttpExchange() {
        return httpExchange;
    }

    public void setHttpExchange(HttpExchangeConfig httpExchange) {
        this.httpExchange = httpExchange;
    }

    public ControllerConfig getController() {
        return controller;
    }

    public void setController(ControllerConfig controller) {
        this.controller = controller;
    }

    /**
     * Feign 拦截配置
     */
    public static class FeignConfig {
        /** Feign 拦截开关 */
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    /**
     * @HttpExchange 拦截配置
     */
    public static class HttpExchangeConfig {
        /** @HttpExchange 拦截开关 */
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    /**
     * Controller 拦截配置
     */
    public static class ControllerConfig {
        /** Controller 拦截开关 */
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
