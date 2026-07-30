package com.github.walkvoid.wvframework.mock.operation;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * Mock 操作元数据（不可变对象）
 *
 * <p>仿照 Spring Cache 的 {@code CacheableOperation}：
 * 把方法上的 {@code @Mock} 注解解析为一个运行时不可变对象，
 * 后续拦截器每次调用都复用同一份元数据，避免反射开销。</p>
 *
 * <p>实现基于 {@code Method} + {@code targetClass} 的 {@code equals} / {@code hashCode}，
 * 以便作为 {@code ConcurrentHashMap} 的缓存 Key 使用。</p>
 *
 * @author walkvoid
 */
public class MockOperation {

    /**
     * 合并后的 value（数据库 Mock 数据键），方法级优先
     */
    private final String value;

    /**
     * 合并后的 enabled
     */
    private final boolean enabled;

    /**
     * 合并后的 mockRequest
     */
    private final boolean mockRequest;

    /**
     * 合并后的 mockResponse
     */
    private final boolean mockResponse;

    /**
     * 合并后的 count
     */
    private final int count;

    /**
     * 合并后的 delay
     */
    private final long delay;

    /**
     * 合并后的 description
     */
    private final String description;

    /**
     * 触发该操作的目标方法
     */
    private final Method method;

    /**
     * 触发该操作的目标类（可能为代理类，解析时已还原）
     */
    private final Class<?> targetClass;

    public MockOperation(String value,
                         boolean enabled,
                         boolean mockRequest,
                         boolean mockResponse,
                         int count,
                         long delay,
                         String description,
                         Method method,
                         Class<?> targetClass) {
        this.value = value;
        this.enabled = enabled;
        this.mockRequest = mockRequest;
        this.mockResponse = mockResponse;
        this.count = count;
        this.delay = delay;
        this.description = description;
        this.method = method;
        this.targetClass = targetClass;
    }

    public String getValue() {
        return value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isMockRequest() {
        return mockRequest;
    }

    public boolean isMockResponse() {
        return mockResponse;
    }

    public int getCount() {
        return count;
    }

    public long getDelay() {
        return delay;
    }

    public String getDescription() {
        return description;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MockOperation that)) {
            return false;
        }
        return Objects.equals(this.method, that.method)
                && Objects.equals(this.targetClass, that.targetClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, targetClass);
    }

    @Override
    public String toString() {
        return "MockOperation{" +
                "value='" + value + '\'' +
                ", enabled=" + enabled +
                ", mockRequest=" + mockRequest +
                ", mockResponse=" + mockResponse +
                ", count=" + count +
                ", delay=" + delay +
                ", method=" + (method != null ? method.toGenericString() : "null") +
                ", targetClass=" + (targetClass != null ? targetClass.getName() : "null") +
                '}';
    }

    /**
     * 缓存 Key：包含 Method + 目标类的不可变包装
     */
    public static final class CacheKey {

        private final Method method;

        private final Class<?> targetClass;

        public CacheKey(Method method, Class<?> targetClass) {
            this.method = method;
            this.targetClass = targetClass;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof CacheKey that)) {
                return false;
            }
            return Objects.equals(this.method, that.method)
                    && Objects.equals(this.targetClass, that.targetClass);
        }

        @Override
        public int hashCode() {
            return Objects.hash(method, targetClass);
        }

        @Override
        public String toString() {
            return "CacheKey{" +
                    "method=" + (method != null ? method.getName() : "null") +
                    ", targetClass=" + (targetClass != null ? targetClass.getName() : "null") +
                    '}';
        }
    }
}
