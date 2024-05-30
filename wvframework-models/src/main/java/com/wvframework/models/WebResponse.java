package com.wvframework.models;

import java.io.Serializable;

/**
 * @author jiangjunqing
 * @version v0.0.1
 * @date 2023/9/14
 * @desc web响应体
 */
public class WebResponse<R> extends BaseWebResponse implements Serializable {
    private static final long serialVersionUID = 2246851850352656128L;

    /**
     * 返回的数据
     */
    private R data;

    /**
     * message的消息等级，需要和前端配合使用
     */
    private String messageLevel;

    protected WebResponse(String code, String message, R data, String messageLevel) {
        super(code, message, messageLevel);
        this.data = data;
    }

    /**
     * 建议使用，代表这是一个成功的响应，但是用户不需要看到任何提示信息
     * @param data
     * @return
     * @param <R>
     */
    public static <R> WebResponse<R> ok(R data) {
        return of(HttpStatus.OK, data, BaseWebResponse.MessageLevel.SILENT);
    }

    /**
     * 建议使用，一个成功的响应，用户需要看到提示信息
     * @param message
     * @param data
     * @return
     * @param <R>
     */
    public static <R> WebResponse<R> info(String message,R data) {
        return of(String.valueOf(HttpStatus.OK.getValue()), message, data, BaseWebResponse.MessageLevel.INFO);
    }

    /**
     * 建议使用，一个成功的响应，用户需要看到提醒信息
     * @param message
     * @param data
     * @return
     * @param <R>
     */
    public static <R> WebResponse<R> warning(String message, R data) {
        return of(String.valueOf(HttpStatus.OK.getValue()), message, data, BaseWebResponse.MessageLevel.WARNING);
    }

    /**
     * 不建议使用，你应该使用抛出异常的方式,后续统一捕获处理，而不是直接返回 WebResponse
     * @param data
     * @return
     * @param <R>
     */
    public static <R> WebResponse<R> error(R data) {
        return of(HttpStatus.INTERNAL_SERVER_ERROR, data, BaseWebResponse.MessageLevel.ERROR);
    }

    /**
     * 不建议使用，但不是强制的，旨在提供一个更灵活的构造WebResponse的方法
     * @param code
     * @param message
     * @param data
     * @param messageLevel
     * @return
     * @param <R>
     */
    public static <R> WebResponse<R> of(String code, String message, R data, String messageLevel) {
        return new WebResponse<R>(code, message, data, messageLevel);
    }

    /**
     * 不建议使用，但不是强制的，旨在提供一个更灵活的构造WebResponse的方法
     * @param code
     * @param message
     * @param data
     * @param messageLevel
     * @return
     * @param <R>
     */
    public static <R> WebResponse<R> of(String code, String message, R data, BaseWebResponse.MessageLevel messageLevel) {
        return new WebResponse<R>(code, message, data, messageLevel.name().toLowerCase());
    }

    /**
     * 不建议使用，但不是强制的，旨在提供一个更灵活的构造WebResponse的方法
     * @param status
     * @param data
     * @param messageLevel
     * @return
     * @param <R>
     */
    public static <R> WebResponse<R> of(HttpStatus status, R data, BaseWebResponse.MessageLevel messageLevel) {
        return new WebResponse<R>(String.valueOf(status.getValue()), status.getMessage(), data, messageLevel.name().toLowerCase());
    }

    /**
     * 不建议使用，但不是强制的，旨在提供一个更灵活的构造WebResponse的方法
     * @param status
     * @param data
     * @param messageLevel
     * @return
     * @param <R>
     */
    public static <R> WebResponse<R> of(HttpStatus status, String message, R data, BaseWebResponse.MessageLevel messageLevel) {
        return new WebResponse<R>(String.valueOf(status.getValue()), message, data, messageLevel.name().toLowerCase());
    }



}
