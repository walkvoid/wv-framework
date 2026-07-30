package com.github.walkvoid.wvframework.mock.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.walkvoid.wvframework.mock.advisor.MockAdvisor;
import com.github.walkvoid.wvframework.mock.advisor.MockMethodInterceptor;
import com.github.walkvoid.wvframework.mock.config.MockProperties;
import com.github.walkvoid.wvframework.mock.core.generator.MockDataGeneratorRegistry;
import com.github.walkvoid.wvframework.mock.core.MockObjectFactory;
import com.github.walkvoid.wvframework.mock.core.generator.*;
import com.github.walkvoid.wvframework.mock.error.MockErrorHandler;
import com.github.walkvoid.wvframework.mock.error.SimpleMockErrorHandler;
import com.github.walkvoid.wvframework.mock.interceptor.MockFeignInterceptor;
import com.github.walkvoid.wvframework.mock.operation.AnnotationMockOperationSource;
import com.github.walkvoid.wvframework.mock.operation.MockOperationSource;
import com.github.walkvoid.wvframework.mock.store.DatabaseMockDataStore;
import com.github.walkvoid.wvframework.mock.store.MockDataStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Mock 模块自动配置
 *
 * <p>使用 Spring AOP 的 Advisor + MethodInterceptor 实现拦截：
 * <ul>
 *   <li>{@link MockAdvisor} —— 基于 {@code MockOperationSource} 的 Pointcut + Advice</li>
 *   <li>{@link MockMethodInterceptor} —— 统一的拦截处理逻辑</li>
 *   <li>{@link MockOperationSource} —— 注解元数据源（带 ConcurrentHashMap 缓存）</li>
 *   <li>{@link MockErrorHandler} —— 异常处理 SPI（默认 SimpleMockErrorHandler 静默吞掉）</li>
 * </ul>
 *
 * @author walkvoid
 */
@AutoConfiguration
@EnableConfigurationProperties(MockProperties.class)
public class MockAutoConfiguration {

    /**
     * Mock 配置属性
     */
    @Bean
    @ConditionalOnMissingBean
    public MockProperties mockProperties() {
        return new MockProperties();
    }

    /**
     * ObjectMapper
     */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper mockObjectMapper() {
        return new ObjectMapper();
    }

    /**
     * Mock 数据生成器注册表
     */
    @Bean
    @ConditionalOnMissingBean
    public MockDataGeneratorRegistry mockDataGeneratorRegistry() {
        MockDataGeneratorRegistry registry = new MockDataGeneratorRegistry();
        registerBuiltinGenerators(registry);
        return registry;
    }

    /**
     * Mock 对象工厂
     */
    @Bean
    @ConditionalOnMissingBean
    public MockObjectFactory mockObjectFactory() {
        return new MockObjectFactory();
    }

    /**
     * Mock 操作元数据源（带 ConcurrentHashMap 缓存）
     *
     * <p>仿照 Spring Cache 的 {@code AnnotationCacheOperationSource}，
     * 把 {@code @Mock} 注解的解析结果按 {@code Method + targetClass} 缓存，
     * 避免每次拦截都做反射。</p>
     */
    @Bean
    @ConditionalOnMissingBean
    public MockOperationSource mockOperationSource(MockProperties properties) {
        return new AnnotationMockOperationSource(properties);
    }

    /**
     * Mock 拦截异常处理器（默认静默吞掉 + log）
     *
     * <p>业务方可实现自定义的 {@link MockErrorHandler} 并注册为 Bean 覆盖默认行为。</p>
     */
    @Bean
    @ConditionalOnMissingBean
    public MockErrorHandler mockErrorHandler() {
        return new SimpleMockErrorHandler();
    }

    /**
     * Mock 方法拦截器（核心拦截逻辑）
     */
    @Bean
    @ConditionalOnProperty(name = "wv.mock.enabled", havingValue = "true", matchIfMissing = true)
    public MockMethodInterceptor mockMethodInterceptor(MockObjectFactory mockObjectFactory,
                                                        MockProperties properties,
                                                        MockOperationSource operationSource,
                                                        MockErrorHandler errorHandler) {
        return new MockMethodInterceptor(mockObjectFactory, properties, operationSource, errorHandler);
    }

    /**
     * Mock Advisor（使用基于 OperationSource 的 Pointcut）
     *
     * <p>Pointcut 在匹配阶段就调用 {@link MockOperationSource#getMockOperation}，
     * 支持接口 / 父类上的 {@code @Mock} 注解，能在 Pointcut 阶段就剪枝。</p>
     */
    @Bean
    @ConditionalOnProperty(name = "wv.mock.enabled", havingValue = "true", matchIfMissing = true)
    public MockAdvisor mockAdvisor(MockMethodInterceptor mockMethodInterceptor,
                                   MockOperationSource operationSource) {
        return new MockAdvisor(mockMethodInterceptor, operationSource);
    }

    /**
     * Feign 拦截器（保留原有实现）
     */
    @Bean
    @ConditionalOnClass(name = "feign.RequestInterceptor")
    @ConditionalOnProperty(name = "wv.mock.feign.enabled", havingValue = "true", matchIfMissing = true)
    public MockFeignInterceptor mockFeignInterceptor() {
        return new MockFeignInterceptor();
    }

    /**
     * 数据库 Mock 数据源（可选）
     */
    @Bean
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnProperty(name = "wv.mock.store.enabled", havingValue = "true", matchIfMissing = false)
    @ConditionalOnMissingBean
    public MockDataStore mockDataStore(DataSource dataSource, MockProperties properties) {
        return new DatabaseMockDataStore(dataSource, properties);
    }

    /**
     * 注册内置的生成器
     */
    private void registerBuiltinGenerators(MockDataGeneratorRegistry registry) {
        registry.register(new NameMockDataGenerator());
        registry.register(new IdCardNoMockDataGenerator());
        registry.register(new AddressMockDataGenerator());
        registry.register(new PhoneMockDataGenerator());
        registry.register(new EmailMockDataGenerator());
        registry.register(new DateMockDataGenerator());
        registry.register(new NumberMockDataGenerator());
        registry.register(new StringMockDataGenerator());
    }
}
