package com.github.walkvoid.wvframework.mock.advisor;

import com.github.walkvoid.wvframework.mock.config.MockProperties;
import com.github.walkvoid.wvframework.mock.core.MockObjectFactory;
import com.github.walkvoid.wvframework.mock.error.MockErrorHandler;
import com.github.walkvoid.wvframework.mock.operation.MockOperation;
import com.github.walkvoid.wvframework.mock.operation.MockOperationSource;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Mock 方法拦截器（核心拦截逻辑）
 *
 * <p>仿照 Spring Cache 的 {@code CacheInterceptor}，运行期流程：
 * <ol>
 *   <li>通过 {@link MockOperationSource} 取出该方法的缓存元数据 {@link MockOperation}</li>
 *   <li>基于元数据判定"是否启用 Mock"（全局开关 / 方法类型 / 类级开关）</li>
 *   <li>处理延迟、Mock 请求体、Mock 响应体，过程中所有异常都走 {@link MockErrorHandler}</li>
 * </ol>
 *
 * <p>相比旧实现：
 * <ul>
 *   <li>移除 {@code method.getAnnotation()} 反射调用，元数据由 {@code MockOperationSource} 缓存</li>
 *   <li>支持接口 / 父类上的 {@code @Mock}（由元数据源统一处理）</li>
 *   <li>Mock 数据源 / Mock 对象生成异常统一收敛到 {@code MockErrorHandler}</li>
 * </ul>
 *
 * @author walkvoid
 */
public class MockMethodInterceptor implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(MockMethodInterceptor.class);

    private final MockObjectFactory mockObjectFactory;

    private final MockProperties properties;

    private final MockOperationSource operationSource;

    private final MockErrorHandler errorHandler;

    public MockMethodInterceptor(MockObjectFactory mockObjectFactory,
                                 MockProperties properties,
                                 MockOperationSource operationSource,
                                 MockErrorHandler errorHandler) {
        this.mockObjectFactory = mockObjectFactory;
        this.properties = properties;
        this.operationSource = operationSource;
        this.errorHandler = errorHandler;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Class<?> targetClass = invocation.getThis() != null
                ? invocation.getThis().getClass()
                : method.getDeclaringClass();

        // 1. 从元数据源获取该方法的 Mock 配置（带缓存）
        MockOperation operation;
        try {
            operation = operationSource.getMockOperation(method, targetClass);
        } catch (Throwable ex) {
            errorHandler.handleInvocationError(ex, null, invocation);
            return invocation.proceed();
        }
        if (operation == null) {
            return invocation.proceed();
        }

        // 2. 判断是否启用 Mock（全局 + 方法类型）
        if (!isMockEnabled(operation, method)) {
            return invocation.proceed();
        }

        // 3. 处理延迟
        try {
            handleDelay(operation);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw ex;
        }

        // 4. 处理请求体 Mock
        if (operation.isMockRequest()) {
            handleMockRequest(invocation.getArguments(), operation);
        }

        // 5. 生成 Mock 响应
        if (operation.isMockResponse()) {
            Class<?> returnType = method.getReturnType();
            if (returnType != void.class && returnType != Void.class) {
                logger.info("Mock 拦截: {}.{}，mock.value={}",
                        method.getDeclaringClass().getSimpleName(),
                        method.getName(),
                        operation.getValue());
                return createMockObject(invocation, method, operation);
            }
        }

        return invocation.proceed();
    }

    /**
     * 生成 Mock 对象（统一异常收敛到 ErrorHandler）。
     */
    private Object createMockObject(MethodInvocation invocation, Method method, MockOperation operation) {
        // 1. 如果指定了 value（数据库 Mock 数据键），优先从 MockDataStore 查询
        if (operation.getValue() != null && !operation.getValue().isEmpty()) {
            try {
                Object fromStore = mockObjectFactory.getMockDataByKey(operation.getValue());
                if (fromStore != null) {
                    return fromStore;
                }
            } catch (Throwable ex) {
                errorHandler.handleMockDataStoreError(ex, operation, operation.getValue());
                // 降级：继续走自动生成
            }
        }
        // 2. 反射 + 字段注解生成
        try {
            return mockObjectFactory.createMockObject(method.getGenericReturnType(), operation.getCount());
        } catch (Throwable ex) {
            errorHandler.handleMockObjectError(ex, operation);
            // 兜底：返回 null 让业务方决定如何处理
            return null;
        }
    }

    /**
     * 判断是否启用 Mock。
     */
    private boolean isMockEnabled(MockOperation operation, Method method) {
        // 1. 全局开关
        if (!properties.isEnabled()) {
            return false;
        }
        // 2. 注解级 enabled
        if (!operation.isEnabled()) {
            return false;
        }
        // 3. 根据方法类型检查对应子开关
        if (isControllerMethod(method)) {
            return properties.getController().isEnabled();
        } else if (isFeignClient(method)) {
            return properties.getFeign().isEnabled();
        } else if (isHttpExchangeMethod(method)) {
            return properties.getHttpExchange().isEnabled();
        } else if (isDubboProviderMethod(method)) {
            return properties.getDubbo().isEnabled();
        }
        // 默认检查 Controller 开关
        return properties.getController().isEnabled();
    }

    /**
     * 处理延迟（从 operation 取 delay）。
     */
    private void handleDelay(MockOperation operation) throws InterruptedException {
        long delay = operation.getDelay();
        if (delay > 0) {
            Thread.sleep(delay);
        }
    }

    /**
     * 填充请求参数 Mock 数据。
     */
    private void handleMockRequest(Object[] args, MockOperation operation) {
        if (args == null || args.length == 0) {
            return;
        }
        for (Object arg : args) {
            if (arg != null) {
                try {
                    mockObjectFactory.fillMockRequest(arg);
                } catch (Throwable ex) {
                    errorHandler.handleMockObjectError(ex, operation);
                }
            }
        }
    }

    /**
     * 判断是否为 Controller 方法。
     */
    private boolean isControllerMethod(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        return declaringClass.isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class)
                || declaringClass.isAnnotationPresent(org.springframework.stereotype.Controller.class);
    }

    /**
     * 判断是否为 Feign Client 方法。
     */
    private boolean isFeignClient(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        if (declaringClass.isAnnotationPresent(org.springframework.cloud.openfeign.FeignClient.class)) {
            return true;
        }
        // feign.Client 是接口（feign core 提供的 SPI 接口），不是注解；通过名字做兜底检测
        try {
            Class<?> feignClientCls = Class.forName("feign.Client");
            return feignClientCls.isAssignableFrom(declaringClass);
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    /**
     * 判断是否为 @HttpExchange 方法。
     */
    private boolean isHttpExchangeMethod(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        if (declaringClass.isAnnotationPresent(org.springframework.web.service.annotation.HttpExchange.class)) {
            return true;
        }
        return method.isAnnotationPresent(org.springframework.web.service.annotation.HttpExchange.class);
    }

    /**
     * 判断是否为 Dubbo Provider 方法。
     */
    private boolean isDubboProviderMethod(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        // Dubbo 的 @DubboService 不在核心 classpath，避免硬依赖；用 class name 字符串探测
        try {
            Class<?> dubboServiceCls = Class.forName("org.apache.dubbo.config.annotation.DubboService");
            if (declaringClass.isAnnotationPresent((Class<? extends java.lang.annotation.Annotation>) dubboServiceCls)) {
                return true;
            }
        } catch (ClassNotFoundException ex) {
            // Dubbo 不在 classpath 时跳过
        }
        return declaringClass.isAnnotationPresent(org.springframework.stereotype.Service.class);
    }
}
