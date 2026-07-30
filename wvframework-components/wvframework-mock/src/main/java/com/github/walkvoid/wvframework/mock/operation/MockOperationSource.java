package com.github.walkvoid.wvframework.mock.operation;

import java.lang.reflect.Method;

/**
 * Mock 操作元数据源（SPI 接口）
 *
 * <p>仿照 Spring Cache 的 {@code CacheOperationSource}：
 * 把"从 Method 上查找 Mock 元数据"的能力抽象出来，
 * 拦截器只依赖此接口，不直接调用反射 API。</p>
 *
 * <p>实现应当负责：
 * <ul>
 *   <li>解析 {@code @Mock} 注解（方法级 + 类级合并）</li>
 *   <li>缓存解析结果，避免每次调用都做反射</li>
 *   <li>支持接口 / 父类上的注解查找</li>
 * </ul>
 *
 * @author walkvoid
 */
public interface MockOperationSource {

    /**
     * 解析方法上的 Mock 元数据。
     *
     * <p>返回 {@code null} 表示该方法上没有任何 {@code @Mock} 配置，
     * 拦截器应当直接放行原方法。</p>
     *
     * @param method      目标方法
     * @param targetClass 目标类（可能为 CGLIB 代理类，调用方已自行还原）
     * @return Mock 操作元数据；无配置时返回 {@code null}
     */
    MockOperation getMockOperation(Method method, Class<?> targetClass);
}
