package com.github.walkvoid.wvframework.mock.error;

import com.github.walkvoid.wvframework.mock.operation.MockOperation;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Mock 拦截异常处理器（SPI 接口）
 *
 * <p>仿照 Spring Cache 的 {@code CacheErrorHandler}：
 * 把 Mock 拦截过程中可能出现的异常集中处理，避免一个 Mock 数据源故障
 * 把整个请求拖垮。</p>
 *
 * <p>典型异常来源：
 * <ul>
 *   <li>{@link #handleMockDataStoreError} —— MockDataStore 读取数据库 / Redis 失败</li>
 *   <li>{@link #handleMockObjectError} —— MockObjectFactory 生成对象时反射失败</li>
 *   <li>{@link #handleInvocationError} —— 兜底异常，处理 invocation 自身的错误</li>
 * </ul>
 *
 * @author walkvoid
 */
public interface MockErrorHandler {

    /**
     * 处理 MockDataStore（数据库 / Redis）读取异常。
     *
     * <p>默认实现应当降级为自动生成 Mock 数据，不应阻断业务请求。</p>
     *
     * @param ex        异常
     * @param operation 当前 Mock 操作
     * @param key       Mock 数据键（可能为 null）
     */
    void handleMockDataStoreError(Throwable ex, MockOperation operation, String key);

    /**
     * 处理 Mock 对象生成异常。
     *
     * @param ex        异常
     * @param operation 当前 Mock 操作
     */
    void handleMockObjectError(Throwable ex, MockOperation operation);

    /**
     * 兜底异常处理（其他未分类的拦截异常）。
     *
     * @param ex         异常
     * @param operation  当前 Mock 操作
     * @param invocation 原始调用
     */
    void handleInvocationError(Throwable ex, MockOperation operation, MethodInvocation invocation);
}
