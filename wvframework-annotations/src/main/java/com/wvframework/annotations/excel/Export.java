package com.wvframework.annotations.excel;

import java.lang.annotation.*;

/**
 * @author jiangjunqing
 * @date 2024/7/31
 * @description: 标注在controller方法上，实现自动导出功能
 * @version:
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Export {

}
