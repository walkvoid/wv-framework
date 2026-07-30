package com.github.walkvoid.wvframework.mock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Mock 模块配置属性
 *
 * @author walkvoid
 */
@ConfigurationProperties(prefix = "wv.mock")
public class MockProperties {

    /**
     * 全局开关：是否启用 Mock 功能
     */
    private boolean enabled = true;

    /**
     * Controller 拦截开关
     */
    private Controller controller = new Controller();

    /**
     * Feign 拦截开关
     */
    private Feign feign = new Feign();

    /**
     * @HttpExchange 拦截开关
     */
    private HttpExchange httpExchange = new HttpExchange();

    /**
     * Dubbo 拦截开关
     */
    private Dubbo dubbo = new Dubbo();

    /**
     * 数据存储配置
     */
    private Store store = new Store();

    /**
     * 多语言配置
     */
    private I18n i18n = new I18n();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Feign getFeign() {
        return feign;
    }

    public void setFeign(Feign feign) {
        this.feign = feign;
    }

    public HttpExchange getHttpExchange() {
        return httpExchange;
    }

    public void setHttpExchange(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }

    public Dubbo getDubbo() {
        return dubbo;
    }

    public void setDubbo(Dubbo dubbo) {
        this.dubbo = dubbo;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public I18n getI18n() {
        return i18n;
    }

    public void setI18n(I18n i18n) {
        this.i18n = i18n;
    }

    /**
     * Controller 拦截配置
     */
    public static class Controller {
        /**
         * 是否启用 Controller 方法拦截
         */
        private boolean enabled = true;

        /**
         * 默认延迟返回时间（毫秒）
         */
        private long defaultDelay = 0;

        /**
         * 默认 Mock 数据条数
         */
        private int defaultCount = 3;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public long getDefaultDelay() {
            return defaultDelay;
        }

        public void setDefaultDelay(long defaultDelay) {
            this.defaultDelay = defaultDelay;
        }

        public int getDefaultCount() {
            return defaultCount;
        }

        public void setDefaultCount(int defaultCount) {
            this.defaultCount = defaultCount;
        }
    }

    /**
     * Feign 拦截配置
     */
    public static class Feign {
        /**
         * 是否启用 Feign Client 拦截
         */
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
    public static class HttpExchange {
        /**
         * 是否启用 @HttpExchange 拦截
         */
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    /**
     * Dubbo 拦截配置
     */
    public static class Dubbo {
        /**
         * 是否启用 Dubbo 拦截
         */
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    /**
     * 数据存储配置
     */
    public static class Store {
        /**
         * 是否启用数据库数据源
         */
        private boolean enabled = false;

        /**
         * Mock 数据表名
         */
        private String table = "wv_mock_data";

        /**
         * 缓存过期时间（秒）
         */
        private long cacheExpireSeconds = 300;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getTable() {
            return table;
        }

        public void setTable(String table) {
            this.table = table;
        }

        public long getCacheExpireSeconds() {
            return cacheExpireSeconds;
        }

        public void setCacheExpireSeconds(long cacheExpireSeconds) {
            this.cacheExpireSeconds = cacheExpireSeconds;
        }
    }

    /**
     * 多语言配置
     */
    public static class I18n {
        /**
         * 默认语言
         */
        private String defaultLang = "zh-CN";

        /**
         * 是否从 HTTP 请求头 Accept-Language 动态获取
         */
        private boolean acceptLanguageEnabled = true;

        public String getDefaultLang() {
            return defaultLang;
        }

        public void setDefaultLang(String defaultLang) {
            this.defaultLang = defaultLang;
        }

        public boolean isAcceptLanguageEnabled() {
            return acceptLanguageEnabled;
        }

        public void setAcceptLanguageEnabled(boolean acceptLanguageEnabled) {
            this.acceptLanguageEnabled = acceptLanguageEnabled;
        }
    }
}
