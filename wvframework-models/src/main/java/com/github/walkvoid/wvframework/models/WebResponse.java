package com.github.walkvoid.wvframework.models;

/**
 * @author jiangjunqing
 * @version v0.0.1
 * @date 2023/9/14
 * @desc web响应体
 */
public class WebResponse<R> implements BaseWebResponse {
    private static final long serialVersionUID = 2246851850352656128L;

    private Integer code;

    private String traceId;

    private String message;

    private String messageLevel;

    /**
     * 返回的数据
     */
    private R data;

    protected WebResponse(Integer code, String message, R data, String messageLevel) {
        this.code = code;
        this.message = message;
        this.messageLevel = messageLevel;
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
        return of(HttpStatus.OK.getValue(), message, data, BaseWebResponse.MessageLevel.INFO);
    }

    /**
     * 建议使用，一个成功的响应，用户需要看到提醒信息
     * @param message
     * @param data
     * @return
     * @param <R>
     */
    public static <R> WebResponse<R> warning(String message, R data) {
        return of(HttpStatus.OK.getValue(), message, data, BaseWebResponse.MessageLevel.WARNING);
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
    public static <R> WebResponse<R> of(Integer code, String message, R data, String messageLevel) {
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
    public static <R> WebResponse<R> of(Integer code, String message, R data, BaseWebResponse.MessageLevel messageLevel) {
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
        return new WebResponse<R>(status.getValue(), status.getMessage(), data, messageLevel.name().toLowerCase());
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
        return new WebResponse<R>(status.getValue(), message, data, messageLevel.name().toLowerCase());
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getTraceId() {
        return traceId;
    }

    @Override
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessageLevel() {
        return messageLevel;
    }

    @Override
    public void setMessageLevel(String messageLevel) {
        this.messageLevel = messageLevel;
    }

    public R getData() {
        return data;
    }

    public void setData(R data) {
        this.data = data;
    }
}
