package com.wvframework.models;


/**
 * @author jiangjunqing
 * @version v1.0.0
 * @date 2023/10/13
 * @desc 基础的webResponse,包含了一个供前端使用的消息等级，不建议在业务controller中直接使用此类
 */
public class BaseWebResponse extends Traceable {
    private static final long serialVersionUID = -4405513283022040676L;

    protected Integer code;

    protected String message;

    /**
     * msg的消息等级，需要和前端配合使用
     */
    private String messageLevel;

    protected BaseWebResponse(Integer code, String message, String messageLevel) {
        this.code = code;
        this.message = message;
        this.messageLevel = messageLevel;
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

    /**
     * 给前端用户的提示消息等级
     */
    public enum MessageLevel {

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

    public String getMessageLevel() {
        return messageLevel;
    }

    public void setMessageLevel(String messageLevel) {
        this.messageLevel = messageLevel;
    }
}
