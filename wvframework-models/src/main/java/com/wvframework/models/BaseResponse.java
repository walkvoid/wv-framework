package com.wvframework.models;

/**
 * @author jiangjunqing
 * @version v1.0.0
 * @date 2023/9/15
 * @desc 这是一个基础的响应体，只是为了代码的复用，不建议业务系统使用该类
 */
public class BaseResponse extends Traceable {

    protected String code;

    protected String message;

    protected BaseResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
