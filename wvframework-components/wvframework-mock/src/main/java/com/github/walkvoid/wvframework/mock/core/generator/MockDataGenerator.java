package com.github.walkvoid.wvframework.mock.core.generator;

import java.lang.reflect.Field;

/**
 * Mock 数据生成器接口
 * 
 * <p>用于根据字段上的注解生成 Mock 数据</p>
 *
 * @author walkvoid
 */
public interface MockDataGenerator<T> {

    /**
     * 生成 Mock 数据
     *
     * @param field     字段对象
     * @param annotation 注解对象
     * @param lang      语言环境
     * @return Mock 数据
     */
    T generate(Field field, Object annotation, String lang);

    /**
     * 获取支持的注解类型
     * 
     * @return 注解类型 Class
     */
    Class<?> getSupportedAnnotationType();
}
