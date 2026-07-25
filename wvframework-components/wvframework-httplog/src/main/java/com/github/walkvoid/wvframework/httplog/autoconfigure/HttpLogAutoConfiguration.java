package com.github.walkvoid.wvframework.httplog.autoconfigure;

import com.github.walkvoid.wvframework.httplog.advisor.HttpLogAdvisor;
import com.github.walkvoid.wvframework.httplog.advisor.HttpLogMethodInterceptor;
import com.github.walkvoid.wvframework.httplog.mapper.HttpLogMapper;
import com.github.walkvoid.wvframework.httplog.model.HttpLogProperties;
import com.github.walkvoid.wvframework.httplog.publisher.DatabaseHttpLogPublisher;
import com.github.walkvoid.wvframework.httplog.publisher.HttpLogPublisher;
import com.github.walkvoid.wvframework.httplog.publisher.Slf4jHttpLogPublisher;
import com.github.walkvoid.wvframework.httplog.resolver.HttpLogAnnotationResolver;
import com.github.walkvoid.wvframework.httplog.resolver.SensitiveFieldMasker;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HTTP 日志自动配置
 *
 * <p>核心逻辑：
 * <ol>
 *   <li>读取 wv.httplog.* 配置</li>
 *   <li>注册 HttpLogAdvisor（统一拦截 Controller + Feign + @HttpExchange）</li>
 *   <li>注册默认 DatabaseHttpLogPublisher（若业务方未自定义）</li>
 *   <li>注册 SensitiveFieldMasker</li>
 * </ol>
 *
 * @author walkvoid
 */
@Configuration
@ConditionalOnProperty(name = "wv.httplog.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(HttpLogProperties.class)
public class HttpLogAutoConfiguration {

    /**
     * 注册 HTTP 日志 Advisor
     */
    @Bean
    @ConditionalOnProperty(name = "wv.httplog.controller.enabled", havingValue = "true", matchIfMissing = true)
    public HttpLogAdvisor httpLogAdvisor(
            HttpLogPublisher publisher,
            HttpLogProperties properties,
            HttpLogAnnotationResolver resolver,
            SensitiveFieldMasker masker) {
        return new HttpLogAdvisor(
                new HttpLogMethodInterceptor(publisher, properties, resolver, masker));
    }

    /**
     * 注册默认数据库日志发布器
     */
    @Bean
    @ConditionalOnMissingBean(HttpLogPublisher.class)
    @ConditionalOnProperty(name = "wv.httplog.enabled", havingValue = "true", matchIfMissing = true)
    public HttpLogPublisher databaseHttpLogPublisher(ObjectProvider<HttpLogMapper> httpLogMapper) {
        HttpLogMapper mapper = httpLogMapper.getIfAvailable();
        if (mapper != null) {
            return new DatabaseHttpLogPublisher(mapper);
        }
        // 如果没有 Mapper，使用 SLF4J 发布器输出日志
        return new Slf4jHttpLogPublisher();
    }

    /**
     * 注册注解解析器
     */
    @Bean
    @ConditionalOnMissingBean(HttpLogAnnotationResolver.class)
    public HttpLogAnnotationResolver httpLogAnnotationResolver(HttpLogProperties properties) {
        return new HttpLogAnnotationResolver(properties);
    }

    /**
     * 注册敏感字段脱敏器
     */
    @Bean
    @ConditionalOnMissingBean(SensitiveFieldMasker.class)
    public SensitiveFieldMasker sensitiveFieldMasker(HttpLogProperties properties) {
        return new SensitiveFieldMasker(properties.getMaskFields());
    }
}
