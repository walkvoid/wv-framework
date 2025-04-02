package com.github.walkvoid.wvframework.core.annotations;

import com.github.walkvoid.wvframework.core.configuration.FrameworkComponentSelector;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * @author jiangjunqing
 * @date 2025/2/20
 * @description: 启用框架功能注解
 * @version:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(FrameworkComponentSelector.class)
@Documented
public @interface EnableFramework {

    /**
     * include names of component, use FrameworkComponents enums
     * @return
     */
    String[] includes() default {};

    /**
     * exclude names of component, use FrameworkComponents enums
     * @return
     */
    String[] excludes() default {};
}
