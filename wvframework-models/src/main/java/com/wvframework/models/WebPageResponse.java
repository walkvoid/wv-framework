package com.wvframework.models;

import java.io.Serializable;
import java.util.List;

/**
 * @author jiangjunqing
 * @date 2023/9/18
 * @desc
 */
public class WebPageResponse<R> extends BaseResponse implements Serializable {
    private static final long serialVersionUID = -424577018334183404L;

    private List<R> data;

    public WebPageResponse(String code, String msg) {
        super(code, msg);
    }
}
