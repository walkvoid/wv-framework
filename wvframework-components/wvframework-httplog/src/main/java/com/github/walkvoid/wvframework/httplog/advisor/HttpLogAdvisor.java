package com.github.walkvoid.wvframework.httplog.advisor;

import com.github.walkvoid.wvframework.httplog.annotation.HttpLog;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

/**
 * HTTP 日志 Advisor
 *
 * <p>类比 Spring Cache 的 BeanFactoryCacheOperationSourceAdvisor：
 * <ul>
 *   <li>Pointcut 匹配所有标注了 @HttpLog 的方法</li>
 *   <li>Advice 为 HttpLogMethodInterceptor</li>
 * </ul>
 *
 * @author walkvoid
 */
public class HttpLogAdvisor extends AbstractPointcutAdvisor {

    private static final long serialVersionUID = 1L;

    private final Pointcut pointcut;

    private final HttpLogMethodInterceptor advice;

    public HttpLogAdvisor(HttpLogMethodInterceptor interceptor) {
        // checkInherited = true：同时检查接口方法上的注解（Feign 和 @HttpExchange 场景）
        this.pointcut = new AnnotationMatchingPointcut(null, HttpLog.class, true);
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
