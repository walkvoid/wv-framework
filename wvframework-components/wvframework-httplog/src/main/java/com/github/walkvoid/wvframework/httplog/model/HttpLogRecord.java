package com.github.walkvoid.wvframework.httplog.model;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 统一 HTTP 日志记录模型
 *
 * @author walkvoid
 */
public class HttpLogRecord {

    /** 日志唯一 ID（UUID） */
    private String logId;

    /** 链路追踪 ID（从 MDC 或 Header 获取） */
    private String traceId;

    /** 日志类型：INBOUND / OUTBOUND */
    private HttpLogType type;

    /** 注解 @HttpLog 的 value 描述 */
    private String description;

    /** HTTP 方法（GET/POST/PUT/DELETE 等） */
    private String httpMethod;

    /** 请求 URL（含查询参数） */
    private String url;

    /** 请求头（已排除敏感 Header） */
    private Map<String, String> requestHeaders;

    /** 请求体（已截断、已脱敏，可能是加密后的密文） */
    private String requestBody;

    /** 解密得到的请求明文（由业务代码通过 HttpLogContext 手动赋值，可为 null） */
    private String requestBodyPlain;

    /** 响应状态码 */
    private int responseStatus;

    /** 响应头 */
    private Map<String, String> responseHeaders;

    /** 响应体（已截断、已脱敏，可能是加密后的密文） */
    private String responseBody;

    /** 解密得到的响应明文（由业务代码通过 HttpLogContext 手动赋值，可为 null） */
    private String responseBodyPlain;

    /** 请求耗时（毫秒） */
    private long duration;

    /** 是否为慢请求（超过阈值） */
    private boolean slow;

    /** 服务名（Feign 场景为 FeignClient name） */
    private String clientName;

    /** 方法签名（类名.方法名） */
    private String methodSignature;

    /** 请求开始时间 */
    private LocalDateTime timestamp;

    /** 异常信息（如有） */
    private String exception;

    public HttpLogRecord() {
    }

    private HttpLogRecord(Builder builder) {
        this.logId = builder.logId;
        this.traceId = builder.traceId;
        this.type = builder.type;
        this.description = builder.description;
        this.httpMethod = builder.httpMethod;
        this.url = builder.url;
        this.requestHeaders = builder.requestHeaders;
        this.requestBody = builder.requestBody;
        this.requestBodyPlain = builder.requestBodyPlain;
        this.responseStatus = builder.responseStatus;
        this.responseHeaders = builder.responseHeaders;
        this.responseBody = builder.responseBody;
        this.responseBodyPlain = builder.responseBodyPlain;
        this.duration = builder.duration;
        this.slow = builder.slow;
        this.clientName = builder.clientName;
        this.methodSignature = builder.methodSignature;
        this.timestamp = builder.timestamp;
        this.exception = builder.exception;
    }

    public static Builder builder() {
        return new Builder();
    }

    // ===== Getters and Setters =====

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public HttpLogType getType() {
        return type;
    }

    public void setType(HttpLogType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getRequestBodyPlain() {
        return requestBodyPlain;
    }

    public void setRequestBodyPlain(String requestBodyPlain) {
        this.requestBodyPlain = requestBodyPlain;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getResponseBodyPlain() {
        return responseBodyPlain;
    }

    public void setResponseBodyPlain(String responseBodyPlain) {
        this.responseBodyPlain = responseBodyPlain;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isSlow() {
        return slow;
    }

    public void setSlow(boolean slow) {
        this.slow = slow;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "HttpLogRecord{" +
                "logId='" + logId + '\'' +
                ", type=" + type +
                ", httpMethod='" + httpMethod + '\'' +
                ", url='" + url + '\'' +
                ", responseStatus=" + responseStatus +
                ", duration=" + duration + "ms" +
                ", slow=" + slow +
                ", methodSignature='" + methodSignature + '\'' +
                '}';
    }

    /**
     * HttpLogRecord Builder
     */
    public static class Builder {
        private String logId;
        private String traceId;
        private HttpLogType type;
        private String description;
        private String httpMethod;
        private String url;
        private Map<String, String> requestHeaders;
        private String requestBody;
        private String requestBodyPlain;
        private int responseStatus;
        private Map<String, String> responseHeaders;
        private String responseBody;
        private String responseBodyPlain;
        private long duration;
        private boolean slow;
        private String clientName;
        private String methodSignature;
        private LocalDateTime timestamp;
        private String exception;

        public Builder logId(String logId) {
            this.logId = logId;
            return this;
        }

        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder type(HttpLogType type) {
            this.type = type;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder httpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder requestHeaders(Map<String, String> requestHeaders) {
            this.requestHeaders = requestHeaders;
            return this;
        }

        public Builder requestBody(String requestBody) {
            this.requestBody = requestBody;
            return this;
        }

        public Builder requestBodyPlain(String requestBodyPlain) {
            this.requestBodyPlain = requestBodyPlain;
            return this;
        }

        public Builder responseStatus(int responseStatus) {
            this.responseStatus = responseStatus;
            return this;
        }

        public Builder responseHeaders(Map<String, String> responseHeaders) {
            this.responseHeaders = responseHeaders;
            return this;
        }

        public Builder responseBody(String responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public Builder responseBodyPlain(String responseBodyPlain) {
            this.responseBodyPlain = responseBodyPlain;
            return this;
        }

        public Builder duration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder slow(boolean slow) {
            this.slow = slow;
            return this;
        }

        public Builder clientName(String clientName) {
            this.clientName = clientName;
            return this;
        }

        public Builder methodSignature(String methodSignature) {
            this.methodSignature = methodSignature;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder exception(String exception) {
            this.exception = exception;
            return this;
        }

        public HttpLogRecord build() {
            return new HttpLogRecord(this);
        }
    }
}
