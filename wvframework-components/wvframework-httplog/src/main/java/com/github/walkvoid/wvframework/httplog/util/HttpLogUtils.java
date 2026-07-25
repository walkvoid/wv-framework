package com.github.walkvoid.wvframework.httplog.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * HTTP 日志工具类
 *
 * @author walkvoid
 */
public final class HttpLogUtils {

    private static final Logger log = LoggerFactory.getLogger(HttpLogUtils.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private HttpLogUtils() {
        // 工具类禁止实例化
    }

    /**
     * 解析链路追踪 ID（按优先级）：
     * <ol>
     *   <li>从 MDC 中获取（如 SkyWalking / Zipkin / Sleuth 已注入）</li>
     *   <li>自动生成 UUID</li>
     * </ol>
     */
    public static String resolveTraceId() {
        // 尝试从 MDC 获取
        String traceId = MDC.get("traceId");
        if (traceId == null || traceId.isEmpty()) {
            traceId = MDC.get("X-B3-TraceId");
        }
        if (traceId == null || traceId.isEmpty()) {
            traceId = MDC.get("trace-id");
        }
        // 自动生成
        if (traceId == null || traceId.isEmpty()) {
            traceId = java.util.UUID.randomUUID().toString().replace("-", "");
        }
        return traceId;
    }

    /**
     * 从接口方法上查找注解（用于 Feign 和 @HttpExchange 场景）
     */
    public static <A extends Annotation> A findAnnotationOnInterface(Method method, Class<A> annotationType) {
        // 先检查方法本身
        A annotation = method.getAnnotation(annotationType);
        if (annotation != null) {
            return annotation;
        }

        // 检查接口方法
        Class<?> declaringClass = method.getDeclaringClass();
        Class<?>[] interfaces = declaringClass.getInterfaces();
        for (Class<?> iface : interfaces) {
            try {
                Method interfaceMethod = iface.getMethod(method.getName(), method.getParameterTypes());
                annotation = interfaceMethod.getAnnotation(annotationType);
                if (annotation != null) {
                    return annotation;
                }
            } catch (NoSuchMethodException e) {
                // 忽略
            }
        }
        return null;
    }

    /**
     * 从接口方法上查找 @HttpLog 注解
     */
    public static com.github.walkvoid.wvframework.httplog.annotation.HttpLog findAnnotationOnInterface(Method method) {
        return findAnnotationOnInterface(method, com.github.walkvoid.wvframework.httplog.annotation.HttpLog.class);
    }

    /**
     * 解析出站请求的 URL
     *
     * <p>注意：Spring Web / @HttpExchange 为可选依赖，不在 classpath 时降级返回类名.方法名
     */
    public static String resolveOutboundUrl(Method method, Object[] args) {
        try {
            StringBuilder url = new StringBuilder();

            // 尝试从 @RequestMapping / @GetMapping 等注解解析路径
            try {
                org.springframework.web.bind.annotation.RequestMapping requestMapping =
                        method.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);
                if (requestMapping != null && requestMapping.value().length > 0) {
                    url.append(requestMapping.value()[0]);
                }
            } catch (NoClassDefFoundError e) {
                // Spring Web MVC 不在 classpath，忽略
            }

            // 尝试从 @GetExchange / @PostExchange 等注解解析路径
            try {
                org.springframework.web.service.annotation.GetExchange getExchange =
                        method.getAnnotation(org.springframework.web.service.annotation.GetExchange.class);
                if (getExchange != null && !getExchange.value().isEmpty()) {
                    url.append(getExchange.value());
                }

                org.springframework.web.service.annotation.PostExchange postExchange =
                        method.getAnnotation(org.springframework.web.service.annotation.PostExchange.class);
                if (postExchange != null && !postExchange.value().isEmpty()) {
                    url.append(postExchange.value());
                }

                org.springframework.web.service.annotation.PutExchange putExchange =
                        method.getAnnotation(org.springframework.web.service.annotation.PutExchange.class);
                if (putExchange != null && !putExchange.value().isEmpty()) {
                    url.append(putExchange.value());
                }

                org.springframework.web.service.annotation.DeleteExchange deleteExchange =
                        method.getAnnotation(org.springframework.web.service.annotation.DeleteExchange.class);
                if (deleteExchange != null && !deleteExchange.value().isEmpty()) {
                    url.append(deleteExchange.value());
                }

                // 尝试从类级别的 @HttpExchange 获取 baseUrl
                org.springframework.web.service.annotation.HttpExchange httpExchange =
                        method.getDeclaringClass().getAnnotation(org.springframework.web.service.annotation.HttpExchange.class);
                if (httpExchange != null && !httpExchange.url().isEmpty()) {
                    url.insert(0, httpExchange.url());
                }
            } catch (NoClassDefFoundError e) {
                // @HttpExchange 不在 classpath（Spring Framework < 6.0），忽略
            }

            return url.length() > 0 ? url.toString() : method.getDeclaringClass().getSimpleName() + "." + method.getName();
        } catch (Exception e) {
            return method.getDeclaringClass().getSimpleName() + "." + method.getName();
        }
    }

    /**
     * 解析 HTTP 方法
     *
     * <p>注意：Spring Web / @HttpExchange 为可选依赖，不在 classpath 时返回 UNKNOWN
     */
    public static String resolveHttpMethod(Method method) {
        try {
            // Spring MVC 注解
            try {
                if (method.isAnnotationPresent(org.springframework.web.bind.annotation.GetMapping.class)) return "GET";
                if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PostMapping.class)) return "POST";
                if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PutMapping.class)) return "PUT";
                if (method.isAnnotationPresent(org.springframework.web.bind.annotation.DeleteMapping.class)) return "DELETE";
                if (method.isAnnotationPresent(org.springframework.web.bind.annotation.PatchMapping.class)) return "PATCH";
                if (method.isAnnotationPresent(org.springframework.web.bind.annotation.RequestMapping.class)) {
                    org.springframework.web.bind.annotation.RequestMapping rm =
                            method.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);
                    if (rm.method().length > 0) {
                        return rm.method()[0].name();
                    }
                }
            } catch (NoClassDefFoundError e) {
                // Spring Web MVC 不在 classpath，忽略
            }

            // @HttpExchange 注解（Spring Framework 6.0+）
            try {
                if (method.isAnnotationPresent(org.springframework.web.service.annotation.GetExchange.class)) return "GET";
                if (method.isAnnotationPresent(org.springframework.web.service.annotation.PostExchange.class)) return "POST";
                if (method.isAnnotationPresent(org.springframework.web.service.annotation.PutExchange.class)) return "PUT";
                if (method.isAnnotationPresent(org.springframework.web.service.annotation.DeleteExchange.class)) return "DELETE";
            } catch (NoClassDefFoundError e) {
                // @HttpExchange 不在 classpath，忽略
            }
        } catch (Exception e) {
            // 忽略
        }
        return "UNKNOWN";
    }

    /**
     * 解析请求体（从方法参数中获取 @RequestBody 标注的参数）
     */
    public static String resolveRequestBody(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        // 简单处理：将第一个非基本类型参数作为请求体
        for (Object arg : args) {
            if (arg != null && !isSimpleType(arg.getClass())) {
                return objectToJson(arg);
            }
        }
        return null;
    }

    /**
     * 尝试获取 Feign Client Name
     *
     * <p>注意：OpenFeign 为可选依赖，当不在 classpath 时返回 null
     */
    public static String resolveFeignClientName(Object target) {
        if (target == null) {
            return null;
        }
        try {
            Class<?> targetClass = target.getClass();
            // 检查 @FeignClient 注解
            org.springframework.cloud.openfeign.FeignClient feignClient =
                    targetClass.getAnnotation(org.springframework.cloud.openfeign.FeignClient.class);
            if (feignClient != null) {
                return feignClient.name();
            }
            // 检查接口上的 @FeignClient
            for (Class<?> iface : targetClass.getInterfaces()) {
                feignClient = iface.getAnnotation(org.springframework.cloud.openfeign.FeignClient.class);
                if (feignClient != null) {
                    return feignClient.name();
                }
            }
        } catch (NoClassDefFoundError e) {
            // OpenFeign 不在 classpath，忽略
        }
        return null;
    }

    /**
     * 对象转 JSON 字符串
     */
    public static String objectToJson(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.trace("Failed to serialize object to JSON", e);
            return obj.toString();
        }
    }

    /**
     * 判断是否为简单类型
     */
    private static boolean isSimpleType(Class<?> type) {
        return type.isPrimitive() ||
               type == String.class ||
               type == Boolean.class ||
               type == Integer.class ||
               type == Long.class ||
               type == Double.class ||
               type == Float.class ||
               type == Short.class ||
               type == Byte.class ||
               type == Character.class ||
               Number.class.isAssignableFrom(type);
    }
}
