package com.github.walkvoid.wvframework.models;

import java.util.List;

/**
 * @author jiangjunqing
 * @date 2023/9/18
 * @desc 分页响应体，R（Result）：结果类型
 */
public class WebPageResponse<R> extends PageResponse<R> implements  BaseResponse<Integer,List<R>>, Traceable {
    private static final long serialVersionUID = -424577018334183404L;

    private String traceId;

    private Integer code;

    private String message;

    private String messageLevel;


    public WebPageResponse(Integer code, String message, List<R> data, String messageLevel) {
        super(0L,0,0L,data);
        this.code = code;
        this.message = message;
        this.messageLevel = messageLevel;
    }

    /**
     * 建议使用，代表这是一个成功的响应，但是用户不需要看到任何提示信息
     * @param data
     * @return
     * @param <R>
     */
    public static <R> WebPageResponse<R> ok(List<R> data) {
        return of(HttpStatus.OK, data, MessageLevel.SILENT);
    }

    /**
     * 建议使用，一个成功的响应，用户需要看到提示信息
     * @param data
     * @return
     * @param <R>
     */
    public static <R> WebPageResponse<R> info(List<R> data, String message) {
        return of(HttpStatus.OK.getValue(), message, data, MessageLevel.INFO);
    }

    /**
     * 建议使用，一个成功的响应，用户需要看到提醒信息
     * @param data
     * @return
     * @param <R>
     */
    public static <R> WebPageResponse<R> warning(List<R> data, String message) {
        return of(HttpStatus.OK.getValue(), message, data, MessageLevel.WARNING);
    }

    /**
     * 不建议使用，你应该使用抛出异常的方式,后续统一捕获处理，而不是直接返回 WebResponse
     * @param data
     * @return
     * @param <R>
     */
    public static <R> WebPageResponse<R> error(List<R> data) {
        return of(HttpStatus.INTERNAL_SERVER_ERROR, data, MessageLevel.ERROR);
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
    public static <R> WebPageResponse<R> of(Integer code, String message, List<R> data, String messageLevel) {
        return new WebPageResponse<R>(code, message, data, messageLevel);
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
    public static <R> WebPageResponse<R> of(Integer code, String message, List<R> data, MessageLevel messageLevel) {
        return new WebPageResponse<R>(code, message, data, messageLevel.name().toLowerCase());
    }

    /**
     * 不建议使用，但不是强制的，旨在提供一个更灵活的构造WebResponse的方法
     * @param status
     * @param data
     * @param messageLevel
     * @return
     * @param <R>
     */
    public static <R> WebPageResponse<R> of(HttpStatus status, List<R> data, MessageLevel messageLevel) {
        return new WebPageResponse<R>(status.getValue(), status.getMessage(), data, messageLevel.name().toLowerCase());
    }

    @Override
    public String getTraceId() {
        return this.traceId;
    }

    @Override
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageLevel() {
        return messageLevel;
    }

    public void setMessageLevel(String messageLevel) {
        this.messageLevel = messageLevel;
    }
}
