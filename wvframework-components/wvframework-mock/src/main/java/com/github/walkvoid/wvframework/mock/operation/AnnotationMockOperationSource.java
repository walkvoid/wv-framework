package com.github.walkvoid.wvframework.mock.operation;

import com.github.walkvoid.wvframework.mock.annotation.Mock;
import com.github.walkvoid.wvframework.mock.config.MockProperties;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 基于注解的 Mock 操作元数据源（默认实现）
 *
 * <p>仿照 Spring Cache 的 {@code AnnotationCacheOperationSource}：
 * <ul>
 *   <li>使用 {@link ConcurrentHashMap} 缓存解析结果，避免每次拦截都做反射</li>
 *   <li>合并方法级 + 类级 {@code @Mock} 配置（方法级优先）</li>
 *   <li>支持接口 / 父类上的 {@code @Mock}（Feign / @HttpExchange / 抽象 Service 场景）</li>
 *   <li>桥接方法自动还原真实实现方法</li>
 * </ul>
 *
 * @author walkvoid
 */
public class AnnotationMockOperationSource implements MockOperationSource {

    /**
     * 是否允许继承父类 / 接口上的 {@code @Mock} 注解
     */
    private final boolean allowInherited;

    /**
     * 全局 Mock 配置（用于取默认值）
     */
    private final MockProperties properties;

    /**
     * 元数据缓存：CacheKey(method, targetClass) → MockOperation
     */
    private final ConcurrentMap<MockOperation.CacheKey, MockOperation> operationCache =
            new ConcurrentHashMap<>(256);

    public AnnotationMockOperationSource(MockProperties properties) {
        this(true, properties);
    }

    public AnnotationMockOperationSource(boolean allowInherited, MockProperties properties) {
        this.allowInherited = allowInherited;
        this.properties = properties;
    }

    @Override
    public MockOperation getMockOperation(Method method, Class<?> targetClass) {
        // 1. 类级 + 方法级 都没有 @Mock，直接返回 null（Pointcut 也会因此不命中）
        Mock classMock = findClassAnnotation(targetClass);
        Mock methodMock = findMethodAnnotation(method, targetClass);
        if (classMock == null && methodMock == null) {
            return null;
        }

        // 2. 缓存 Key
        Method specificMethod = AopUtilsBridge.getMostSpecificMethod(method, targetClass);
        MockOperation.CacheKey key = new MockOperation.CacheKey(specificMethod, targetClass);

        // 3. computeIfAbsent 保证只解析一次
        return operationCache.computeIfAbsent(key, k -> parseMockOperation(
                specificMethod, targetClass, methodMock, classMock));
    }

    /**
     * 合并并构造 MockOperation。
     */
    private MockOperation parseMockOperation(Method method,
                                             Class<?> targetClass,
                                             Mock methodMock,
                                             Mock classMock) {
        // 方法级优先：值未设置时回落到类级；都没有则使用全局默认值
        String value = pick(methodMock, Mock::value, classMock, Mock::value, "");
        boolean enabled = pick(methodMock, Mock::enabled, classMock, Mock::enabled, true);
        boolean mockRequest = pick(methodMock, Mock::mockRequest, classMock, Mock::mockRequest, true);
        boolean mockResponse = pick(methodMock, Mock::mockResponse, classMock, Mock::mockResponse, true);
        int defaultCount = properties != null ? properties.getController().getDefaultCount() : 3;
        int count = pick(methodMock, Mock::count, classMock, Mock::count, defaultCount);
        long defaultDelay = properties != null ? properties.getController().getDefaultDelay() : 0L;
        long delay = pick(methodMock, Mock::delay, classMock, Mock::delay, defaultDelay);
        String description = pick(methodMock, Mock::description, classMock, Mock::description, "");

        return new MockOperation(value, enabled, mockRequest, mockResponse,
                count, delay, description, method, targetClass);
    }

    /**
     * 优先取 methodMock 的属性，若为默认值再回落到 classMock，最终回落到全局 defaultValue。
     */
    private static <T> T pick(Mock methodMock,
                              java.util.function.Function<Mock, T> getter,
                              Mock classMock,
                              java.util.function.Function<Mock, T> getter2,
                              T defaultValue) {
        if (methodMock != null) {
            T v = getter.apply(methodMock);
            if (!isDefault(methodMock, getter, v)) {
                return v;
            }
        }
        if (classMock != null) {
            T v = getter2.apply(classMock);
            if (!isDefault(classMock, getter2, v)) {
                return v;
            }
        }
        return defaultValue;
    }

    /**
     * 通过与默认值比较判断"用户是否显式设置"，
     * 避免业务方的配置被类级默认值覆盖。
     */
    private static <T> boolean isDefault(Mock mock,
                                         java.util.function.Function<Mock, T> getter,
                                         T currentValue) {
        // 通过 AnnotationUtils 获取注解上的默认值，与当前值比对
        Map<String, Object> attrs = AnnotationUtils.getAnnotationAttributes(mock);
        if (attrs == null) {
            return false;
        }
        // 这里用反射拿方法名（Function 难以直接拿到属性名），简单粗暴走属性名字符串
        String name = attributeName(getter);
        if (name == null) {
            return false;
        }
        Object defaultVal = attrs.get(name);
        return java.util.Objects.equals(currentValue, defaultVal);
    }

    private static String attributeName(java.util.function.Function<Mock, ?> getter) {
        // 用 toString 解析出方法名: "Mock::value" -> "value"
        String s = getter.toString();
        int idx = s.lastIndexOf("::");
        if (idx < 0) {
            return null;
        }
        return s.substring(idx + 2);
    }

    /**
     * 查找类上的 {@code @Mock} 注解（支持父类）。
     */
    private Mock findClassAnnotation(Class<?> targetClass) {
        if (targetClass == null) {
            return null;
        }
        Class<?> userClass = ClassUtils.getUserClass(targetClass);
        return AnnotationUtils.findAnnotation(userClass, Mock.class);
    }

    /**
     * 查找方法上的 {@code @Mock} 注解（支持接口 / 父类）。
     */
    private Mock findMethodAnnotation(Method method, Class<?> targetClass) {
        if (method == null) {
            return null;
        }
        // 1. 方法自身
        Mock mock = AnnotationUtils.findAnnotation(method, Mock.class);
        if (mock != null) {
            return mock;
        }
        // 2. 桥接方法处理
        Method resolved = BridgeMethodResolver.findBridgedMethod(method);
        if (resolved != method) {
            mock = AnnotationUtils.findAnnotation(resolved, Mock.class);
            if (mock != null) {
                return mock;
            }
        }
        // 3. 父类 / 接口方法
        if (allowInherited && targetClass != null) {
            try {
                Class<?> userClass = ClassUtils.getUserClass(targetClass);
                Method specific = AopUtilsBridge.getMostSpecificMethod(method, userClass);
                if (specific != method) {
                    mock = AnnotationUtils.findAnnotation(specific, Mock.class);
                    if (mock != null) {
                        return mock;
                    }
                }
            } catch (Exception ignored) {
                // 找不到父类方法时忽略
            }
        }
        return null;
    }

    /**
     * 暴露缓存视图（仅用于调试 / 测试）。
     */
    public Map<MockOperation.CacheKey, MockOperation> getOperationCacheView() {
        return Collections.unmodifiableMap(operationCache);
    }

    /**
     * 清空缓存（仅用于测试）。
     */
    public void clear() {
        operationCache.clear();
    }

    /**
     * 内部工具：解析"最具体的方法"，避免直接依赖 spring-aop。
     * 这样 mock 模块在 spring-core 即可工作，spring-aop 由 advisor 间接使用。
     */
    static final class AopUtilsBridge {

        static Method getMostSpecificMethod(Method method, Class<?> targetClass) {
            if (targetClass == null) {
                return method;
            }
            Class<?> userClass = ClassUtils.getUserClass(targetClass);
            // 跳过合成方法
            if (method != null && !Modifier.isAbstract(method.getModifiers())
                    && method.getDeclaringClass().isAssignableFrom(userClass)) {
                return method;
            }
            // 在 userClass 上找同名同参方法
            try {
                return userClass.getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException ex) {
                return method;
            }
        }
    }
}
