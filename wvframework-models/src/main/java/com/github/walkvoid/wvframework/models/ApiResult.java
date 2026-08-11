package com.github.walkvoid.wvframework.models;

import java.io.Serializable;

import static com.github.walkvoid.wvframework.models.BaseResultCodeEnum.FAIL;
import static com.github.walkvoid.wvframework.models.BaseResultCodeEnum.SUCCESS;

/**
 * 统一API响应体，实现Traceable以支持链路追踪。
 *
 * @param <T> 响应数据类型
 * @author jiangjunqing
 */
public class ApiResult<T> implements Traceable {
    private static final long serialVersionUID = 1L;

    /** 状态码，支持String或Integer */
    private Serializable code;

    /** 提示消息 */
    private String msg;

    /** 响应数据 */
    private T data;

    /** 链路追踪ID */
    private String traceId;

    private ApiResult(Serializable code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 通过 ResultCode 构建响应（推荐）。
     * 适用于业务层定义好 ResultCode 实现（如枚举）后直接使用。
     */
    public static <T> ApiResult<T> of(ResultCode resultCode, T data) {
        return new ApiResult<>(resultCode.getCode(), resultCode.getMsg(), data);
    }

    /**
     * 通过 ResultCode 构建无数据的响应。
     */
    public static <T> ApiResult<T> of(ResultCode resultCode) {
        return new ApiResult<>(resultCode.getCode(), resultCode.getMsg(), null);
    }

    /** 成功响应（无提示消息） */
    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(SUCCESS.getCode(), SUCCESS.getMsg(), data);
    }

    /** 成功响应（自定义消息） */
    public static <T> ApiResult<T> ok(T data, String msg) {
        return new ApiResult<>(SUCCESS.getCode(), msg, data);
    }

    /** 失败响应 */
    public static <T> ApiResult<T> error() {
        return new ApiResult<>(FAIL.getCode(), FAIL.getMsg(), null);
    }

    /** 失败响应（默认500） */
    public static <T> ApiResult<T> error(String msg) {
        return new ApiResult<>(FAIL.getCode(), msg, null);
    }

    /** 失败响应（默认500） */
    public static <T> ApiResult<T> error(Serializable code, String msg) {
        return new ApiResult<>(code, msg, null);
    }


    public Serializable getCode() {
        return code;
    }

    public void setCode(Serializable code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String getTraceId() {
        return traceId;
    }

    @Override
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
