package com.github.walkvoid.wvframework.models;

/**
 * @author jiangjunqing
 * @date 2025/2/20
 * @description:
 * @version:
 */
public interface BaseResponse<C,T> {

    C getCode();

    String getMessage();

    T getData();

}
