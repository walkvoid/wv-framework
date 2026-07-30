package com.github.walkvoid.wvframework.mock.interceptor;

import com.github.walkvoid.wvframework.mock.annotation.Mock;
import com.github.walkvoid.wvframework.mock.config.MockProperties;
import com.github.walkvoid.wvframework.mock.core.MockObjectFactory;
import com.github.walkvoid.wvframework.mock.store.MockDataStore;
import org.apache.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Dubbo Provider Mock 拦截器
 * 
 * <p>拦截 Dubbo 服务提供方方法，直接返回 Mock 数据</p>
 *
 * @author walkvoid
 */
@ConditionalOnClass(name = "org.apache.dubbo.rpc.Filter")
@ConditionalOnProperty(name = "wv.mock.dubbo.enabled", havingValue = "true", matchIfMissing = true)
public class MockDubboFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(MockDubboFilter.class);

    private final MockObjectFactory mockObjectFactory;
    private final MockDataStore mockDataStore;
    private final MockProperties properties;

    public MockDubboFilter(MockObjectFactory mockObjectFactory, 
                          MockDataStore mockDataStore,
                          MockProperties properties) {
        this.mockObjectFactory = mockObjectFactory;
        this.mockDataStore = mockDataStore;
        this.properties = properties;
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // 检查全局开关
        if (!properties.isEnabled() || !properties.getDubbo().isEnabled()) {
            return invoker.invoke(invocation);
        }

        // 获取方法
        java.lang.reflect.Method method = null;
        try {
            method = invoker.getInterface().getMethod(
                    invocation.getMethodName(), 
                    invocation.getParameterTypes()
            );
        } catch (Exception e) {
            logger.debug("获取方法失败", e);
            return invoker.invoke(invocation);
        }

        // 检查 @Mock 注解
        Mock mock = getMockAnnotation(method);
        if (mock == null || !mock.enabled()) {
            return invoker.invoke(invocation);
        }

        // 生成 Mock 数据
        logger.info("Dubbo Mock 拦截: {}.{}", 
                invoker.getInterface().getSimpleName(), 
                invocation.getMethodName());

        // 处理延迟
        long delay = getDelay(mock);
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // 创建 Mock 响应
        Object mockResult = mockObjectFactory.createMockObject(
                method.getGenericReturnType(), 
                mock
        );

        return new RpcResult(mockResult);
    }

    /**
     * 获取方法上的 @Mock 注解（先检查方法，再检查类）
     */
    private Mock getMockAnnotation(java.lang.reflect.Method method) {
        Mock mock = method.getAnnotation(Mock.class);
        if (mock != null) {
            return mock;
        }
        return method.getDeclaringClass().getAnnotation(Mock.class);
    }

    /**
     * 获取延迟时间
     */
    private long getDelay(Mock mock) {
        if (mock != null && mock.delay() > 0) {
            return mock.delay();
        }
        return 0;
    }
}
