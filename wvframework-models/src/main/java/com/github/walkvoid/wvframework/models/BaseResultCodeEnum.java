package com.github.walkvoid.wvframework.models;

public enum BaseResultCodeEnum implements ResultCode {

    SUCCESS(0, "成功"),
    FAIL(1, "失败"),
    PARAM_INVALID(2, "参数校验失败"),
    UNKNOWN_ERROR(9999, "系统繁忙，请稍后再试");

    private final Integer code;

    private final String msg;

    BaseResultCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
