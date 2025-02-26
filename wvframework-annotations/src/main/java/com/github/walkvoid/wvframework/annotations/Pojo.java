package com.github.walkvoid.wvframework.annotations;

import java.lang.annotation.*;

/**
 * @author walkvoid
 * @version 1.0
 * @date 2025/2/21
 * @desc
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Pojo {

    boolean builder() default false;
}
