package com.github.walkvoid.wvframework.httplog.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.github.walkvoid.wvframework.httplog.annotation.HttpLog;
import com.github.walkvoid.wvframework.httplog.model.HttpLogRecord;

/**
 * HTTP 日志上下文（基于 TransmittableThreadLocal，支持线程池跨线程传递）
 *
 * <p>核心设计：
 * <ol>
 *   <li>使用 Alibaba TTL（TransmittableThreadLocal）替代 InheritableThreadLocal，
 *       在线程池场景下也能正确传递上下文，避免数据丢失或脏数据</li>
 *   <li>提供 setRequestBodyPlain() / setResponseBodyPlain() 静态方法，
 *       业务代码在解密后手动调用，将明文写入当前上下文</li>
 *   <li>拦截器在发布日志前，从上下文中读取明文并填充到 HttpLogRecord</li>
 * </ol>
 *
 * <p><b>注意：</b>若业务使用了自定义线程池，需使用 {@code TtlExecutors.getTtlExecutorService(executor)}
 * 包装线程池，以确保 TTL 上下文正确传递。
 *
 * @author walkvoid
 */
public class HttpLogContext {

    /**
     * 使用 TransmittableThreadLocal 支持线程池场景下的跨线程上下文传递。
     * <p>相比 InheritableThreadLocal，TTL 在任务提交时捕获上下文、
     * 任务执行时恢复、任务完成后清理，彻底解决线程池复用导致的数据丢失/脏数据问题。
     */
    private static final TransmittableThreadLocal<HttpLogContext> HOLDER = new TransmittableThreadLocal<>();

    /** 请求开始时间 */
    private long startTime;

    /** 当前 @HttpLog 注解 */
    private HttpLog annotation;

    /** 日志记录 Builder */
    private HttpLogRecord.Builder recordBuilder;

    /** 解密得到的请求明文 */
    private String requestBodyPlain;

    /** 解密得到的响应明文 */
    private String responseBodyPlain;

    public static void set(HttpLogContext context) {
        HOLDER.set(context);
    }

    public static HttpLogContext get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }

    /**
     * 业务代码调用：设置解密后的请求明文
     * 可在任意线程中调用（包括异步子线程）
     */
    public static void setRequestBodyPlain(String plain) {
        HttpLogContext ctx = HOLDER.get();
        if (ctx != null) {
            ctx.requestBodyPlain = plain;
        }
    }

    /**
     * 业务代码调用：设置解密后的响应明文
     * 可在任意线程中调用（包括异步子线程）
     */
    public static void setResponseBodyPlain(String plain) {
        HttpLogContext ctx = HOLDER.get();
        if (ctx != null) {
            ctx.responseBodyPlain = plain;
        }
    }

    // ===== Getters and Setters =====

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public HttpLog getAnnotation() {
        return annotation;
    }

    public void setAnnotation(HttpLog annotation) {
        this.annotation = annotation;
    }

    public HttpLogRecord.Builder getRecordBuilder() {
        return recordBuilder;
    }

    public void setRecordBuilder(HttpLogRecord.Builder recordBuilder) {
        this.recordBuilder = recordBuilder;
    }

    public String getRequestBodyPlain() {
        return requestBodyPlain;
    }

    public String getResponseBodyPlain() {
        return responseBodyPlain;
    }
}
