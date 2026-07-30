package com.github.walkvoid.wvframework.mock.core.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock 数据生成器注册表
 * 
 * <p>管理所有 Mock 数据生成器</p>
 *
 * @author walkvoid
 */
@Component
public class MockDataGeneratorRegistry {

    private static final Logger logger = LoggerFactory.getLogger(MockDataGeneratorRegistry.class);

    /**
     * 注解类型 -> 生成器映射
     */
    private final Map<Class<?>, MockDataGenerator<?>> generators = new ConcurrentHashMap<>();

    /**
     * 注册生成器
     */
    public void register(MockDataGenerator<?> generator) {
        Class<?> annotationType = generator.getSupportedAnnotationType();
        generators.put(annotationType, generator);
        logger.info("注册 Mock 数据生成器: {} -> {}", annotationType.getSimpleName(), generator.getClass().getSimpleName());
    }

    /**
     * 批量注册生成器
     */
    public void register(List<MockDataGenerator<?>> generatorList) {
        generatorList.forEach(this::register);
    }

    /**
     * 获取注解对应的生成器
     */
    @SuppressWarnings("unchecked")
    public <T> MockDataGenerator<T> getGenerator(Class<?> annotationType) {
        return (MockDataGenerator<T>) generators.get(annotationType);
    }

    /**
     * 检查是否支持指定注解类型
     */
    public boolean isSupported(Class<?> annotationType) {
        return generators.containsKey(annotationType);
    }

    /**
     * 获取所有支持的注解类型
     */
    public Map<Class<?>, MockDataGenerator<?>> getAllGenerators() {
        return new HashMap<>(generators);
    }
}
