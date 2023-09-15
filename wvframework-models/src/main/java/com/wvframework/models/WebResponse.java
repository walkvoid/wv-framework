package com.wvframework.models;

import java.io.Serializable;

/**
 * @author jiangjunqing
 * @version v0.0.1
 * @date 2023/9/14
 * @desc web响应体
 */
public class WebResponse<R> extends BaseResponse implements Serializable {
    private static final long serialVersionUID = 2246851850352656128L;

    /**
     * 返回的数据
     */
    private R data;

    /**
     * msg的消息等级，需要和前端配合使用
     */
    private String msgLevel;

    protected WebResponse(String code, String msg, R data, String msgLevel) {
        super(code, msg);
        this.data = data;
        this.msgLevel = msgLevel;
    }

    /**
     * 建议使用，代表这是一个成功的响应，但是用户不需要看到任何提示信息
     * @param data
     * @return
     * @param <R>
     */
    public static <R> WebResponse<R> ok(R data) {
        return of(HttpStatus.OK, data, MsgLevel.SILENT);
    }

    /**
     * 建议使用，一个成功的响应，用户需要看到提示信息
     * @param data
     * @return
     * @param <R>
     */
    public static <R> WebResponse<R> info(R data) {
        return of(HttpStatus.OK, data, MsgLevel.INFO);
    }

    /**
     * 建议使用，一个成功的响应，用户需要看到提醒信息
     * @param data
     * @return
     * @param <R>
     */
    public static <R> WebResponse<R> warning(R data) {
        return of(HttpStatus.OK, data, MsgLevel.WARNING);
    }

    /**
     * 不建议使用，你应该使用抛出异常的方式，而不是直接返回 WebResponse
     * @param data
     * @return
     * @param <R>
     */
    public static <R> WebResponse<R> error(R data) {
        return of(HttpStatus.INTERNAL_SERVER_ERROR, data, MsgLevel.ERROR);
    }

    /**
     * 不建议使用，但不是强制的，旨在提供一个更灵活的构造WebResponse的方法
     * @param code
     * @param msg
     * @param data
     * @param msgLevel
     * @return
     * @param <R>
     */
    public static <R> WebResponse<R> of(String code, String msg, R data, String msgLevel) {
        return new WebResponse<R>(code, msg, data, msgLevel);
    }

    /**
     * 不建议使用，但不是强制的，旨在提供一个更灵活的构造WebResponse的方法
     * @param code
     * @param msg
     * @param data
     * @param msgLevel
     * @return
     * @param <R>
     */
    public static <R> WebResponse<R> of(String code, String msg, R data, MsgLevel msgLevel) {
        return new WebResponse<R>(code, msg, data, msgLevel.name().toLowerCase());
    }

    /**
     * 不建议使用，但不是强制的，旨在提供一个更灵活的构造WebResponse的方法
     * @param status
     * @param data
     * @param msgLevel
     * @return
     * @param <R>
     */
    public static <R> WebResponse<R> of(HttpStatus status, R data, MsgLevel msgLevel) {
        return new WebResponse<R>(String.valueOf(status.getValue()), status.getMsg(), data, msgLevel.name().toLowerCase());
    }


    /**
     * 给前端用户的提示消息等级
     */
    protected enum MsgLevel {

        /**
         * 响应成功，前端用户不需要看到任何提示信息
         */
        SILENT,

        /**
         * 响应成功，前端用户需要看到这个提示信息，以便知道下一步将做什么，常用用户异步接口返回
         */
        INFO,

        /**
         * 响应成功，前端用户需要看到这个提醒信息，提醒用户如果需要进一步体验完整的服务，需要进行更多的操作，常见于补充资料等场景
         */
        WARNING,

        /**
         * 响应失败，前端用户需要看到这个错误消息，以便知道他们遇到了异常情况，以及需要如何处理，适用于一切错误场景
         */
        ERROR,
    }



}
