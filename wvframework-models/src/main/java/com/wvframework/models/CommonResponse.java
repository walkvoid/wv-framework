package com.wvframework.models;

import java.io.Serializable;

/**
 * @author jiangjunqing
 * @version v0.0.1
 * @date 2023/9/14
 * @desc
 */
public class CommonResponse<R> implements Serializable {
    private static final long serialVersionUID = 2246851850352656128L;

    private String code;

    private String msg;

    private R data;


    protected CommonResponse(String code, String msg, R data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <R> CommonResponse<R> of(String code, String msg, R data) {
        return new CommonResponse<R>(code, msg, data);
    }

    public static <R> CommonResponse<R> ok(String code, String msg, R data) {
        return new CommonResponse<R>(code, msg, data);
    }

    public static <R> CommonResponse<R> error(String code, String msg, R data) {
        return new CommonResponse<R>(code, msg, data);
    }

    public static <R> CommonResponse<R> toast(String code, String msg, R data) {
        return new CommonResponse<R>(code, msg, data);
    }
}
