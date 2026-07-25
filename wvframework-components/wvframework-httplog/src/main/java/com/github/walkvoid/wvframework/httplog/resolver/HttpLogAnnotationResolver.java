package com.github.walkvoid.wvframework.httplog.resolver;

import com.github.walkvoid.wvframework.httplog.annotation.HttpLog;
import com.github.walkvoid.wvframework.httplog.annotation.LogLevel;
import com.github.walkvoid.wvframework.httplog.model.HttpLogProperties;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @HttpLog 注解属性解析器
 *
 * <p>负责将注解属性与全局配置合并，注解属性优先级高于全局配置。
 *
 * @author walkvoid
 */
public class HttpLogAnnotationResolver {

    private final HttpLogProperties properties;

    public HttpLogAnnotationResolver(HttpLogProperties properties) {
        this.properties = properties;
    }

    /**
     * 解析注解属性，合并全局配置
     *
     * @param httpLog 注解实例
     * @return 解析后的配置
     */
    public ResolvedHttpLogConfig resolve(HttpLog httpLog) {
        if (httpLog == null) {
            return null;
        }

        ResolvedHttpLogConfig config = new ResolvedHttpLogConfig();
        config.setDescription(httpLog.value());
        config.setEnabled(httpLog.enabled());
        config.setLogRequest(resolveLogRequest(httpLog));
        config.setLogRequestBody(resolveLogRequestBody(httpLog));
        config.setLogResponseBody(resolveLogResponseBody(httpLog));
        config.setLogLevel(resolveLogLevel(httpLog));
        config.setMaxBodyLength(resolveMaxBodyLength(httpLog));
        config.setSlowThreshold(resolveSlowThreshold(httpLog));
        config.setExcludeHeaders(resolveExcludeHeaders(httpLog));
        config.setMaskFields(resolveMaskFields(httpLog));

        return config;
    }

    private boolean resolveLogRequest(HttpLog httpLog) {
        // 注解默认值 true，取全局配置
        return properties.isLogRequest();
    }

    private boolean resolveLogRequestBody(HttpLog httpLog) {
        return httpLog.logRequestBody() && properties.isLogRequestBody();
    }

    private boolean resolveLogResponseBody(HttpLog httpLog) {
        return httpLog.logResponseBody() && properties.isLogResponseBody();
    }

    private LogLevel resolveLogLevel(HttpLog httpLog) {
        // 如果注解使用默认值 INFO，取全局配置
        if (httpLog.logLevel() == LogLevel.INFO) {
            return properties.getLogLevel();
        }
        return httpLog.logLevel();
    }

    private int resolveMaxBodyLength(HttpLog httpLog) {
        // 如果注解使用默认值 2048，取全局配置
        if (httpLog.maxBodyLength() == 2048) {
            return properties.getMaxBodyLength();
        }
        return httpLog.maxBodyLength();
    }

    private long resolveSlowThreshold(HttpLog httpLog) {
        // 如果注解使用默认值 3000，取全局配置
        if (httpLog.slowThreshold() == 3000) {
            return properties.getSlowThreshold();
        }
        return httpLog.slowThreshold();
    }

    private Set<String> resolveExcludeHeaders(HttpLog httpLog) {
        Set<String> headers = new HashSet<>();
        // 全局排除
        List<String> globalExclude = properties.getExcludeHeaders();
        if (globalExclude != null) {
            headers.addAll(globalExclude);
        }
        // 注解排除
        String[] annotationExclude = httpLog.excludeHeaders();
        if (annotationExclude != null && annotationExclude.length > 0) {
            headers.addAll(Arrays.asList(annotationExclude));
        }
        return headers;
    }

    private Set<String> resolveMaskFields(HttpLog httpLog) {
        Set<String> fields = new HashSet<>();
        // 全局脱敏
        List<String> globalMask = properties.getMaskFields();
        if (globalMask != null) {
            fields.addAll(globalMask);
        }
        // 注解脱敏
        String[] annotationMask = httpLog.maskFields();
        if (annotationMask != null && annotationMask.length > 0) {
            fields.addAll(Arrays.asList(annotationMask));
        }
        return fields;
    }

    /**
     * 解析后的配置
     */
    public static class ResolvedHttpLogConfig {
        private String description;
        private boolean enabled;
        private boolean logRequest;
        private boolean logRequestBody;
        private boolean logResponseBody;
        private LogLevel logLevel;
        private int maxBodyLength;
        private long slowThreshold;
        private Set<String> excludeHeaders;
        private Set<String> maskFields;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

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

        public LogLevel getLogLevel() {
            return logLevel;
        }

        public void setLogLevel(LogLevel logLevel) {
            this.logLevel = logLevel;
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

        public Set<String> getExcludeHeaders() {
            return excludeHeaders != null ? excludeHeaders : Collections.emptySet();
        }

        public void setExcludeHeaders(Set<String> excludeHeaders) {
            this.excludeHeaders = excludeHeaders;
        }

        public Set<String> getMaskFields() {
            return maskFields != null ? maskFields : Collections.emptySet();
        }

        public void setMaskFields(Set<String> maskFields) {
            this.maskFields = maskFields;
        }
    }
}
