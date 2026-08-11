package com.github.walkvoid.wvframework.models;

import java.io.Serializable;

public interface ResultCode {
    /**
     * 业务状态码，0代表成功
     */
    Serializable getCode();

    /**
     * 提示文案
     */
    String getMsg();
}