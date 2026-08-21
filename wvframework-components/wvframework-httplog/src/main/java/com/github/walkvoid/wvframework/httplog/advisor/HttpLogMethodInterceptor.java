package com.github.walkvoid.wvframework.httplog.advisor;

import com.github.walkvoid.wvframework.httplog.annotation.HttpLog;
import com.github.walkvoid.wvframework.httplog.context.HttpLogContext;
import com.github.walkvoid.wvframework.httplog.model.HttpLogProperties;
import com.github.walkvoid.wvframework.httplog.model.HttpLogRecord;
import com.github.walkvoid.wvframework.httplog.model.HttpLogType;
import com.github.walkvoid.wvframework.httplog.publisher.HttpLogPublisher;
import com.github.walkvoid.wvframework.httplog.resolver.HttpLogAnnotationResolver;
import com.github.walkvoid.wvframework.httplog.resolver.HttpLogAnnotationResolver.ResolvedHttpLogConfig;
import com.github.walkvoid.wvframework.httplog.resolver.SensitiveFieldMasker;
import com.github.walkvoid.wvframework.httplog.util.HttpLogUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * HTTP 日志 MethodInterceptor（核心拦截逻辑）
 *
 * <p>类比 Spring Cache 的 CacheInterceptor：
 * <ul>
 *   <li>实现 org.aopalliance.intercept.MethodInterceptor</li>
 *   <li>在 invoke() 中统一处理 Controller 入站请求、Feign 出站请求和 @HttpExchange 出站请求</li>
 * </ul>
 *
 * @author walkvoid
 */
public class HttpLogMethodInterceptor implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(HttpLogMethodInterceptor.class);

    private final HttpLogPublisher publisher;

    private final HttpLogProperties properties;

    private final HttpLogAnnotationResolver resolver;

    private final SensitiveFieldMasker masker;

    public HttpLogMethodInterceptor(HttpLogPublisher publisher,
                                    HttpLogProperties properties,
                                    HttpLogAnnotationResolver resolver,
                                    SensitiveFieldMasker masker) {
        this.publisher = publisher;
        this.properties = properties;
        this.resolver = resolver;
        this.masker = masker;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        HttpLog httpLog = method.getAnnotation(HttpLog.class);

        // 如果方法上没有 @HttpLog 注解，尝试从接口方法上获取
        if (httpLog == null) {
            httpLog = HttpLogUtils.findAnnotationOnInterface(method);
        }

        if (httpLog == null || !httpLog.enabled()) {
            return invocation.proceed();
        }

        if (!isLoggingEnabled(method)) {
            return invocation.proceed();
        }

        // 解析注解属性，合并全局配置
        ResolvedHttpLogConfig config = resolver.resolve(httpLog);
        if (config == null || !config.isEnabled()) {
            return invocation.proceed();
        }

        // 初始化日志上下文
        HttpLogContext context = new HttpLogContext();
        context.setStartTime(System.currentTimeMillis());
        context.setAnnotation(httpLog);
        HttpLogContext.set(context);

        long startTime = System.currentTimeMillis();
        HttpLogRecord.Builder builder = HttpLogRecord.builder()
                .logId(UUID.randomUUID().toString())
                .methodSignature(buildMethodSignature(invocation))
                .description(config.getDescription())
                .timestamp(LocalDateTime.now())
                .traceId(HttpLogUtils.resolveTraceId());

        try {
            // 判断请求类型
            if (isControllerRequest()) {
                // ===== Controller 入站请求 =====
                handleInboundRequest(invocation, config, builder);
            } else {
                // ===== Feign / @HttpExchange 出站请求 =====
                handleOutboundRequest(invocation, config, builder);
            }

            // 执行目标方法
            Object result = invocation.proceed();

            // 记录响应信息
            long duration = System.currentTimeMillis() - startTime;
            builder.duration(duration);
            builder.slow(duration > config.getSlowThreshold());

            // 记录响应信息
            if (isControllerRequest()) {
                handleInboundResponse(config, builder);
            } else {
                handleOutboundResponse(result, config, builder);
            }

            // 从 HttpLogContext 读取业务代码手动赋值的明文
            HttpLogContext ctx = HttpLogContext.get();
            if (ctx != null) {
                builder.requestBodyPlain(ctx.getRequestBodyPlain());
                builder.responseBodyPlain(ctx.getResponseBodyPlain());
            }

            // 发布日志
            publishLog(builder.build(), config);

            return result;
        } catch (Throwable t) {
            // 异常场景也记录日志
            builder.exception(t.getClass().getName() + ": " + t.getMessage());
            long duration = System.currentTimeMillis() - startTime;
            builder.duration(duration);
            builder.slow(duration > config.getSlowThreshold());

            // 异常时也读取明文
            HttpLogContext ctx = HttpLogContext.get();
            if (ctx != null) {
                builder.requestBodyPlain(ctx.getRequestBodyPlain());
                builder.responseBodyPlain(ctx.getResponseBodyPlain());
            }

            // 发布日志
            publishLog(builder.build(), config);
            throw t;
        } finally {
            // 清理上下文，防止内存泄漏
            HttpLogContext.clear();
        }
    }

    /**
     * 判断当前场景是否允许记录日志
     */
    private boolean isLoggingEnabled(Method method) {
        if (!properties.isEnabled()) {
            return false;
        }
        if (isFeignClient(method)) {
            return properties.getFeign().isEnabled();
        }
        if (isHttpExchangeMethod(method)) {
            return properties.getHttpExchange().isEnabled();
        }
        if (isControllerMethod(method)) {
            return properties.getController().isEnabled();
        }
        if (isControllerRequest()) {
            return properties.getController().isEnabled();
        }
        return properties.getFeign().isEnabled() || properties.getHttpExchange().isEnabled();
    }

    /**
     * 判断是否为 Controller 入站请求
     */
    private boolean isControllerRequest() {
        return RequestContextHolder.getRequestAttributes() != null;
    }

    private boolean isControllerMethod(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        return declaringClass.isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class)
                || declaringClass.isAnnotationPresent(org.springframework.stereotype.Controller.class);
    }

    private boolean isFeignClient(Method method) {
        try {
            Class<? extends Annotation> feignClientAnnotation =
                    loadAnnotationClass("org.springframework.cloud.openfeign.FeignClient");
            Class<?> declaringClass = method.getDeclaringClass();
            if (declaringClass.isAnnotationPresent(feignClientAnnotation)) {
                return true;
            }
            for (Class<?> iface : declaringClass.getInterfaces()) {
                if (iface.isAnnotationPresent(feignClientAnnotation)) {
                    return true;
                }
            }
        } catch (ClassNotFoundException ignored) {
            // OpenFeign 不在 classpath
        }
        return false;
    }

    private boolean isHttpExchangeMethod(Method method) {
        try {
            Class<? extends Annotation> httpExchangeAnnotation =
                    loadAnnotationClass("org.springframework.web.service.annotation.HttpExchange");
            Class<?> declaringClass = method.getDeclaringClass();
            if (declaringClass.isAnnotationPresent(httpExchangeAnnotation)) {
                return true;
            }
            for (Class<?> iface : declaringClass.getInterfaces()) {
                if (iface.isAnnotationPresent(httpExchangeAnnotation)) {
                    return true;
                }
            }
            return method.isAnnotationPresent(
                            loadAnnotationClass("org.springframework.web.service.annotation.GetExchange"))
                    || method.isAnnotationPresent(
                            loadAnnotationClass("org.springframework.web.service.annotation.PostExchange"))
                    || method.isAnnotationPresent(
                            loadAnnotationClass("org.springframework.web.service.annotation.PutExchange"))
                    || method.isAnnotationPresent(
                            loadAnnotationClass("org.springframework.web.service.annotation.DeleteExchange"))
                    || method.isAnnotationPresent(
                            loadAnnotationClass("org.springframework.web.service.annotation.PatchExchange"));
        } catch (ClassNotFoundException ignored) {
            // @HttpExchange 不在 classpath
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Annotation> loadAnnotationClass(String className)
            throws ClassNotFoundException {
        return (Class<? extends Annotation>) Class.forName(className);
    }

    /**
     * 构建方法签名
     */
    private String buildMethodSignature(MethodInvocation invocation) {
        Object target = invocation.getThis();
        String className = target != null ? target.getClass().getSimpleName() : "Unknown";
        // 去除代理类后缀
        if (className.contains("$")) {
            className = className.substring(0, className.indexOf("$"));
        }
        return className + "." + invocation.getMethod().getName();
    }

    /**
     * 处理 Controller 入站请求
     */
    private void handleInboundRequest(MethodInvocation invocation,
                                      ResolvedHttpLogConfig config,
                                      HttpLogRecord.Builder builder) {
        builder.type(HttpLogType.INBOUND);

        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return;
        }

        HttpServletRequest request = attrs.getRequest();
        HttpServletResponse response = attrs.getResponse();

        // 包装 Request（缓存 Body）
        CachedBodyRequestWrapper wrappedRequest;
        try {
            wrappedRequest = new CachedBodyRequestWrapper(request);
        } catch (Exception e) {
            log.warn("Failed to wrap request for body caching", e);
            return;
        }

        // 记录请求信息
        if (config.isLogRequest()) {
            builder.httpMethod(wrappedRequest.getMethod());
            builder.url(buildUrl(wrappedRequest));
            builder.requestHeaders(extractHeaders(wrappedRequest, config.getExcludeHeaders()));

            // 记录请求体
            if (config.isLogRequestBody()) {
                String body = wrappedRequest.getCachedBodyAsString();
                builder.requestBody(masker.mask(body, config.getMaxBodyLength(), config.getMaskFields()));
            }
        }

        // 包装 Response（缓存 Body）
        ContentCachingResponseWrapper wrappedResponse = null;
        if (response != null) {
            wrappedResponse = new ContentCachingResponseWrapper(response);
        }

        // 替换 RequestContextHolder 中的 attributes
        ServletRequestAttributes newAttrs = new ServletRequestAttributes(wrappedRequest, wrappedResponse != null ? wrappedResponse : response);
        RequestContextHolder.setRequestAttributes(newAttrs, true);
    }

    /**
     * 处理 Controller 入站响应
     *
     * <p>注意：无论是否记录响应体，都必须调用 copyBodyToResponse()
     * 将缓存的响应数据写回原始响应，否则客户端将收到空响应。
     */
    private void handleInboundResponse(ResolvedHttpLogConfig config, HttpLogRecord.Builder builder) {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return;
        }

        HttpServletResponse response = attrs.getResponse();
        if (response == null) {
            return;
        }

        builder.responseStatus(response.getStatus());

        // 如果包装了 ContentCachingResponseWrapper，需要处理
        if (response instanceof ContentCachingResponseWrapper wrappedResponse) {
            try {
                // 读取响应体（仅在启用时）
                if (config.isLogResponseBody()) {
                    byte[] responseBody = wrappedResponse.getContentAsByteArray();
                    if (responseBody.length > 0) {
                        String body = HttpLogUtils.bytesToString(
                                responseBody,
                                wrappedResponse.getCharacterEncoding(),
                                wrappedResponse.getContentType());
                        builder.responseBody(masker.mask(body, config.getMaxBodyLength(), config.getMaskFields()));
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to read response body", e);
            } finally {
                // 必须将响应体写回原始响应，否则客户端收不到响应
                try {
                    wrappedResponse.copyBodyToResponse();
                } catch (Exception e) {
                    log.warn("Failed to copy body to response", e);
                }
            }
        }
    }

    /**
     * 处理出站请求（Feign / @HttpExchange）
     */
    private void handleOutboundRequest(MethodInvocation invocation,
                                       ResolvedHttpLogConfig config,
                                       HttpLogRecord.Builder builder) {
        builder.type(HttpLogType.OUTBOUND);

        Method method = invocation.getMethod();

        // 从方法参数和注解解析目标 URL
        String url = HttpLogUtils.resolveOutboundUrl(method, invocation.getArguments());
        builder.url(url);
        builder.httpMethod(HttpLogUtils.resolveHttpMethod(method));

        // 记录请求体（从方法参数中获取）
        if (config.isLogRequestBody()) {
            String body = HttpLogUtils.resolveRequestBody(invocation.getArguments());
            if (body != null) {
                builder.requestBody(masker.mask(body, config.getMaxBodyLength(), config.getMaskFields()));
            }
        }

        // 尝试获取 Feign Client Name
        String clientName = HttpLogUtils.resolveFeignClientName(invocation.getThis());
        if (clientName != null) {
            builder.clientName(clientName);
        }
    }

    /**
     * 处理出站响应
     */
    private void handleOutboundResponse(Object result,
                                        ResolvedHttpLogConfig config,
                                        HttpLogRecord.Builder builder) {
        // 默认状态码 200（成功）
        builder.responseStatus(200);

        // 记录响应体
        if (config.isLogResponseBody() && result != null) {
            try {
                String body = HttpLogUtils.objectToJson(result);
                builder.responseBody(masker.mask(body, config.getMaxBodyLength(), config.getMaskFields()));
            } catch (Exception e) {
                log.trace("Failed to serialize response body", e);
            }
        }
    }

    /**
     * 构建请求 URL
     */
    private String buildUrl(HttpServletRequest request) {
        String url = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            url = url + "?" + queryString;
        }
        return url;
    }

    /**
     * 提取请求头（排除敏感 Header）
     */
    private Map<String, String> extractHeaders(HttpServletRequest request, Set<String> excludeHeaders) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (excludeHeaders == null || !excludeHeaders.contains(headerName)) {
                headers.put(headerName, request.getHeader(headerName));
            }
        }
        return headers;
    }

    /**
     * 发布日志
     */
    private void publishLog(HttpLogRecord record, ResolvedHttpLogConfig config) {
        try {
            publisher.publish(record);
        } catch (Exception e) {
            log.error("Failed to publish HTTP log: {}", record.getLogId(), e);
        }
    }
}
