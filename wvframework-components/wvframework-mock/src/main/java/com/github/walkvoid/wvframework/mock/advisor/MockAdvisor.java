package com.github.walkvoid.wvframework.mock.advisor;

import com.github.walkvoid.wvframework.mock.operation.MockOperationSource;
import com.github.walkvoid.wvframework.mock.operation.MockOperationSourcePointcut;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;

/**
 * Mock 功能 Advisor
 *
 * <p>仿照 Spring Cache 的 {@code BeanFactoryCacheOperationSourceAdvisor}：
 * <ul>
 *   <li>Pointcut 使用 {@link MockOperationSourcePointcut}，
 *       在 Pointcut 阶段就调用 {@link MockOperationSource} 判断是否需要 Mock</li>
 *   <li>Advice 为 {@link MockMethodInterceptor}</li>
 * </ul>
 *
 * <p>相比旧实现：
 * <ul>
 *   <li>旧实现使用 {@code AnnotationMatchingPointcut}，只能做纯注解匹配，无法感知接口 / 父类上的 {@code @Mock}</li>
 *   <li>新实现通过 {@link MockOperationSource} 统一解析，能复用缓存、能支持{@code enabled=false} 场景的 Pointcut 阶段剪枝</li>
 * </ul>
 *
 * @author walkvoid
 */
public class MockAdvisor extends AbstractPointcutAdvisor {

    private static final long serialVersionUID = 1L;

    private final Pointcut pointcut;

    private final MethodInterceptor advice;

    public MockAdvisor(MethodInterceptor interceptor, MockOperationSource operationSource) {
        this.pointcut = new MockOperationSourcePointcut(operationSource);
        this.advice = interceptor;
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this.advice;
    }
}
