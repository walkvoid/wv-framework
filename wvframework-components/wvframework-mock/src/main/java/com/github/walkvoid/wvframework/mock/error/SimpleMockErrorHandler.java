package com.github.walkvoid.wvframework.mock.error;

import com.github.walkvoid.wvframework.mock.operation.MockOperation;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mock 拦截异常处理器的默认实现
 *
 * <p>仿照 Spring Cache 的 {@code SimpleCacheErrorHandler}：
 * 所有异常一律记日志后吞掉，保证 Mock 模块的异常不会阻断业务请求。
 * 业务方可以注入自定义的 {@link MockErrorHandler} 改变行为。</p>
 *
 * @author walkvoid
 */
public class SimpleMockErrorHandler implements MockErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(SimpleMockErrorHandler.class);

    @Override
    public void handleMockDataStoreError(Throwable ex, MockOperation operation, String key) {
        log.warn("[Mock] MockDataStore read failed, key={}, method={}, will fallback to auto-generate. Cause: {}",
                key,
                describeMethod(operation),
                ex.toString());
    }

    @Override
    public void handleMockObjectError(Throwable ex, MockOperation operation) {
        log.warn("[Mock] generate mock object failed, method={}, cause: {}",
                describeMethod(operation), ex.toString());
    }

    @Override
    public void handleInvocationError(Throwable ex, MockOperation operation, MethodInvocation invocation) {
        log.error("[Mock] interceptor unexpected error, method={}", describeMethod(operation), ex);
    }

    private static String describeMethod(MockOperation operation) {
        if (operation == null || operation.getMethod() == null) {
            return "<unknown>";
        }
        return operation.getMethod().getDeclaringClass().getName()
                + "#" + operation.getMethod().getName();
    }
}
