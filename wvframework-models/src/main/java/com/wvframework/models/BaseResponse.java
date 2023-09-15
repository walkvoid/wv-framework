package com.wvframework.models;


/**
 * @author jiangjunqing
 * @version v1.0.0
 * @date 2023/9/15
 * @desc 这是一个基础的响应体，只是为了代码的复用，不建议业务系统使用该类
 */
class BaseResponse extends Traceable {

    protected String code;

    protected String msg;

    protected BaseResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
