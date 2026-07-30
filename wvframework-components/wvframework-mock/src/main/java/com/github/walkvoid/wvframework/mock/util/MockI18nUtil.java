package com.github.walkvoid.wvframework.mock.util;

import com.github.walkvoid.wvframework.mock.config.MockProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Locale;

/**
 * 多语言工具类
 * 
 * <p>支持自动获取当前环境的多语言配置</p>
 * <p>支持从 HTTP 请求头 Accept-Language 动态获取</p>
 *
 * @author walkvoid
 */
@Component
public class MockI18nUtil {

    private static final Logger logger = LoggerFactory.getLogger(MockI18nUtil.class);

    private static MockProperties mockProperties;

    @Autowired
    public void setMockProperties(MockProperties properties) {
        MockI18nUtil.mockProperties = properties;
    }

    /**
     * 获取当前语言环境
     * 
     * <p>优先级：
     * <ol>
     *   <li>HTTP 请求头 Accept-Language</li>
     *   <li>Spring LocaleContextHolder</li>
     *   <li>配置默认值</li>
     * </ol>
     * 
     * @return 当前语言环境代码，如 zh-CN、en-US
     */
    public static String getCurrentLang() {
        // 1. 尝试从 HTTP 请求头获取
        if (mockProperties != null && mockProperties.getI18n().isAcceptLanguageEnabled()) {
            String acceptLanguage = getAcceptLanguage();
            if (acceptLanguage != null && !acceptLanguage.isEmpty()) {
                return normalizeLang(acceptLanguage);
            }
        }

        // 2. 从 Spring 上下文获取
        Locale locale = LocaleContextHolder.getLocale();
        if (locale != null) {
            String lang = locale.toString();
            if (lang != null && !lang.isEmpty()) {
                return normalizeLang(lang);
            }
        }

        // 3. 使用默认值
        if (mockProperties != null) {
            return mockProperties.getI18n().getDefaultLang();
        }

        return "zh-CN";
    }

    /**
     * 获取 HTTP 请求头的 Accept-Language
     */
    private static String getAcceptLanguage() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getHeader("Accept-Language");
            }
        } catch (Exception e) {
            logger.debug("获取 Accept-Language 失败", e);
        }
        return null;
    }

    /**
     * 标准化语言代码
     * <p>如 zh-CN、zh_CN -> zh-CN</p>
     */
    public static String normalizeLang(String lang) {
        if (lang == null || lang.isEmpty()) {
            return "zh-CN";
        }
        // 替换下划线为横线
        return lang.replace("_", "-").split(";")[0].split(",")[0].trim();
    }

    /**
     * 判断是否为自动模式
     */
    public static boolean isAutoLang(String lang) {
        return "AUTO".equalsIgnoreCase(lang);
    }

    /**
     * 解析语言代码
     * <p>如果是 AUTO 模式，则返回当前语言环境</p>
     * 
     * @param lang 语言代码
     * @return 解析后的语言代码
     */
    public static String resolveLang(String lang) {
        if (isAutoLang(lang)) {
            return getCurrentLang();
        }
        return normalizeLang(lang);
    }
}
