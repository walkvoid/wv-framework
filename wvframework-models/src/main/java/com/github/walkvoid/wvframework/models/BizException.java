package com.github.walkvoid.wvframework.models;

import java.io.Serializable;

/**
 * 基础业务异常，通过 ResultCode 定义错误码和消息模板，
 *
 * @author jiangjunqing
 * @version 0.0.1
 * @date 2023/9/15
 */
public class BizException extends RuntimeException {

    private final Serializable code;

    private final String msg;

    public BizException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }

    /**
     * @param resultCode 错误码，
     * @param args       占位符参数，按顺序替换 msg 中的%s
     */
    public BizException(ResultCode resultCode, Object... args) {
        super(String.format(resultCode.getMsg(), args));
        this.code = resultCode.getCode();
        this.msg = super.getMessage();
    }

    /**
     * @param msg 直接指定消息
     */
    public BizException(String msg) {
        super(msg);
        this.code = BaseResultCodeEnum.FAIL;
        this.msg = msg;
    }

    public Serializable getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


}
