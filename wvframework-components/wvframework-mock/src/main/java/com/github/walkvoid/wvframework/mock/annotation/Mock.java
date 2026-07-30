package com.github.walkvoid.wvframework.mock.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mock 运行时开关注解
 * 
 * <p>支持类级和方法级两种用法：
 * <ol>
 *   <li><b>类级</b>：标注在 Controller 类上，表示该类所有请求方法都需要 Mock，作为运行时开关</li>
 *   <li><b>方法级</b>：标注在具体请求方法上，更细粒度地控制该方法是否 Mock、Mock 请求体还是响应体等</li>
 * </ol>
 * 
 * <p>支持四种拦截场景：
 * <ul>
 *   <li>Controller 方法 - 外部请求该方法时，直接返回 Mock 数据</li>
 *   <li>Feign Client Facade 方法 - 远程调用时，直接返回 Mock 数据</li>
 *   <li>@HttpExchange 接口方法 - 声明式 HTTP 调用时，直接返回 Mock 数据</li>
 *   <li>Dubbo Provider 接口方法 - 服务提供方拦截，直接返回 Mock 数据</li>
 * </ul>
 *
 * @author walkvoid
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mock {

    /**
     * Mock 数据键
     * <p>当指定值时，从 MockDataStore（数据库/Redis/配置）获取预配置的 Mock 数据</p>
     * <p>例如：@Mock(value = "bank.callback.success")</p>
     */
    String value() default "";

    /**
     * 是否启用 Mock
     * 可覆盖全局开关
     */
    boolean enabled() default true;

    /**
     * 是否 Mock 请求体
     * 当为 true 时，会自动填充请求参数的 Mock 数据
     */
    boolean mockRequest() default true;

    /**
     * 是否 Mock 响应体
     * 当为 true 时，会自动生成响应参数的 Mock 数据
     */
    boolean mockResponse() default true;

    /**
     * 集合类型返回时的 Mock 数据条数
     * 仅对 List、Page 等集合类型生效
     */
    int count() default 3;

    /**
     * 延迟返回（毫秒）
     * 模拟网络延迟，用于测试超时场景
     */
    long delay() default 0;

    /**
     * Mock 描述/备注
     */
    String description() default "";
}
