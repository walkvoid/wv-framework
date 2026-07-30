package com.github.walkvoid.wvframework.mock.operation;

import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;

/**
 * 基于 {@link MockOperationSource} 的 Pointcut
 *
 * <p>仿照 Spring Cache 的 {@code CacheOperationSourcePointcut}：
 * 在 Pointcut 阶段就调用 {@link MockOperationSource#getMockOperation}，
 * 由元数据源决定该方法是否需要 Mock。</p>
 *
 * <p>相比 {@code AnnotationMatchingPointcut} 的优势：
 * <ul>
 *   <li>支持"接口 / 父类上的 {@code @Mock}"（Feign / @HttpExchange 场景）</li>
 *   <li>支持 {@code enabled=false} 等"运行时开关"在 Pointcut 阶段就剪枝，
 *       避免给无效方法创建 AOP 代理</li>
 *   <li>复用 {@link MockOperationSource} 的缓存，Pointcut 调用和拦截器调用共享同一份元数据</li>
 * </ul>
 *
 * @author walkvoid
 */
public class MockOperationSourcePointcut extends StaticMethodMatcherPointcut {

    /**
     * 元数据源
     */
    private final MockOperationSource operationSource;

    public MockOperationSourcePointcut(MockOperationSource operationSource) {
        this.operationSource = operationSource;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        // 跳过 Object / 内部方法，避免不必要的匹配
        if (method.getDeclaringClass().equals(Object.class)) {
            return false;
        }
        if (method.getDeclaringClass().equals(MockOperationSourcePointcut.class)
                || method.getDeclaringClass().equals(StaticMethodMatcherPointcut.class)) {
            return false;
        }
        MockOperation operation = operationSource.getMockOperation(method, targetClass);
        return operation != null;
    }

    /**
     * 暴露内部 operationSource（用于其他 Advisor 复用同一份缓存）。
     */
    public MockOperationSource getOperationSource() {
        return operationSource;
    }
}
