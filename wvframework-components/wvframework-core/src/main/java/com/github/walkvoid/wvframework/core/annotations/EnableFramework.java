package com.github.walkvoid.wvframework.core.annotations;

import com.github.walkvoid.wvframework.core.configuration.FrameworkAutoConfiguration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * @author jiangjunqing
 * @date 2025/2/20
 * @description:
 * @version:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(FrameworkAutoConfiguration.class)
@Documented
public @interface EnableFramework {

    String[] includes() default {};

    String[] excludes() default {};
}
