package com.github.walkvoid.wvframework.mock.interceptor;

import com.github.walkvoid.wvframework.mock.annotation.Mock;
import com.github.walkvoid.wvframework.mock.config.MockProperties;
import com.github.walkvoid.wvframework.mock.core.MockObjectFactory;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Feign Client Mock 拦截器
 * 
 * <p>拦截 Feign Client 方法，返回 Mock 数据</p>
 *
 * @author walkvoid
 */
@ConditionalOnClass(name = "feign.RequestInterceptor")
@ConditionalOnProperty(name = "wv.mock.feign.enabled", havingValue = "true", matchIfMissing = true)
public class MockFeignInterceptor implements RequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(MockFeignInterceptor.class);

    @Autowired
    private MockObjectFactory mockObjectFactory;

    @Autowired
    private MockProperties properties;

    /**
     * 这里不做实际拦截，只是标记
     * 实际拦截需要在 Feign 客户端调用时处理
     */
    @Override
    public void apply(RequestTemplate template) {
        // Feign 的拦截器主要用于修改请求
        // Mock 功能主要在调用方通过 AOP 拦截实现
    }

    /**
     * 创建 Mock 响应
     * 
     * <p>用于 Feign 调用的降级处理</p>
     */
    public Object createMockResponse(Method method) {
        // 检查是否启用了 Mock
        if (!properties.isEnabled() || !properties.getFeign().isEnabled()) {
            return null;
        }

        // 检查方法上是否有 @Mock 注解
        Mock mock = method.getAnnotation(Mock.class);
        if (mock == null) {
            // 检查类上是否有 @Mock 注解
            mock = method.getDeclaringClass().getAnnotation(Mock.class);
        }

        if (mock == null || !mock.enabled()) {
            return null;
        }

        return mockObjectFactory.createMockObject(method.getGenericReturnType(), mock);
    }
}
