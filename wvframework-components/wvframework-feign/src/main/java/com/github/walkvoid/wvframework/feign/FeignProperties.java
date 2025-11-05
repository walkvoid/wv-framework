package com.github.walkvoid.wvframework.feign;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * Feign 配置属性
 * @author jiangjunqing
 * @date 2025/11/5
 * @description: Feign 客户端配置属性类
 * @version: 1.0
 */
@ConfigurationProperties(prefix = "wv.feign")
public class FeignProperties implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户端类型配置
     * 可选值：urlconnection（默认）、okhttp
     * 配置前缀：wv.feign.client
     */
    private Client client = new Client();

    /**
     * HTTP 连接配置（通用配置，适用于 HttpURLConnection 和 OkHttp）
     * 配置前缀：wv.feign.http
     */
    private Http http = new Http();

    /**
     * OkHttp 专用配置
     * 配置前缀：wv.feign.okhttp
     */
    private OkHttp okhttp = new OkHttp();

    /**
     * 拦截器配置
     * 配置前缀：wv.feign.interceptor
     */
    private Interceptor interceptor = new Interceptor();

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Http getHttp() {
        return http;
    }

    public void setHttp(Http http) {
        this.http = http;
    }

    public OkHttp getOkhttp() {
        return okhttp;
    }

    public void setOkhttp(OkHttp okhttp) {
        this.okhttp = okhttp;
    }

    public Interceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    /**
     * 客户端类型配置组
     * 配置示例：wv.feign.client.type=urlconnection
     */
    public static class Client implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 客户端类型
         * 可选值：urlconnection（默认）、okhttp
         * 默认值：urlconnection
         */
        private String type = "urlconnection";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    /**
     * HTTP 连接配置组（通用配置）
     * 配置示例：wv.feign.http.connect-timeout=10000
     */
    public static class Http implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 连接超时时间（毫秒）
         * 默认值：10000（10秒）
         */
        private long connectTimeout = 10000;

        /**
         * 读取超时时间（毫秒）
         * 默认值：10000（10秒）
         */
        private long readTimeout = 10000;

        /**
         * 写入超时时间（毫秒）
         * 默认值：10000（10秒）
         */
        private long writeTimeout = 10000;

        public long getConnectTimeout() {
            return connectTimeout;
        }

        public void setConnectTimeout(long connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public long getReadTimeout() {
            return readTimeout;
        }

        public void setReadTimeout(long readTimeout) {
            this.readTimeout = readTimeout;
        }

        public long getWriteTimeout() {
            return writeTimeout;
        }

        public void setWriteTimeout(long writeTimeout) {
            this.writeTimeout = writeTimeout;
        }
    }

    /**
     * OkHttp 专用配置组
     * 配置示例：wv.feign.okhttp.retry-on-connection-failure=true
     */
    public static class OkHttp implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 是否启用 OkHttp
         * 默认值：false（使用 HttpURLConnection）
         */
        private boolean enabled = false;

        /**
         * 是否在连接失败时重试
         * 默认值：true
         */
        private boolean retryOnConnectionFailure = true;

        /**
         * 连接池配置
         */
        private Pool pool = new Pool();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isRetryOnConnectionFailure() {
            return retryOnConnectionFailure;
        }

        public void setRetryOnConnectionFailure(boolean retryOnConnectionFailure) {
            this.retryOnConnectionFailure = retryOnConnectionFailure;
        }

        public Pool getPool() {
            return pool;
        }

        public void setPool(Pool pool) {
            this.pool = pool;
        }

        /**
         * OkHttp 连接池配置组
         * 配置示例：wv.feign.okhttp.pool.max-idle-connections=5
         */
        public static class Pool implements Serializable {

            private static final long serialVersionUID = 1L;

            /**
             * 连接池最大空闲连接数
             * 默认值：5
             */
            private int maxIdleConnections = 5;

            /**
             * 连接保持存活时间（分钟）
             * 默认值：5
             */
            private long keepAliveDuration = 5;

            public int getMaxIdleConnections() {
                return maxIdleConnections;
            }

            public void setMaxIdleConnections(int maxIdleConnections) {
                this.maxIdleConnections = maxIdleConnections;
            }

            public long getKeepAliveDuration() {
                return keepAliveDuration;
            }

            public void setKeepAliveDuration(long keepAliveDuration) {
                this.keepAliveDuration = keepAliveDuration;
            }
        }
    }

    /**
     * 拦截器配置组
     * 配置示例：wv.feign.interceptor.enable-logging=true
     */
    public static class Interceptor implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 是否启用日志拦截器
         * 默认值：false
         */
        private boolean enableLogging = false;

        /**
         * 最大重试次数
         * 默认值：0（不重试）
         */
        private int maxRetries = 0;

        public boolean isEnableLogging() {
            return enableLogging;
        }

        public void setEnableLogging(boolean enableLogging) {
            this.enableLogging = enableLogging;
        }

        public int getMaxRetries() {
            return maxRetries;
        }

        public void setMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
        }
    }
}
