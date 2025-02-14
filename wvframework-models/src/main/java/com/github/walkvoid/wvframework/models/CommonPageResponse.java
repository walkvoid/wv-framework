package com.github.walkvoid.wvframework.models;

import java.util.List;

/**
 * @author jiangjunqing
 * @date 2024/12/30
 * @description:
 * @version:
 */
public class CommonPageResponse<R> extends PageResponse<R> implements Traceable {
    private static final long serialVersionUID = -9069349981489966401L;

    private String traceId;

    private String code;

    private String message;

    public static <R> CommonPageResponse<R> of(String code, String message, List<R> data) {
        return of(code, message, 0L, 0, 0L, data);
    }

    public static <R> CommonPageResponse<R> of(String code, String message, Long total, Integer size, Long current, List<R> data) {
        return new CommonPageResponse<>(code, message, total, size, current, data);
    }

    protected CommonPageResponse(String code, String message, Long total, Integer size, Long current, List<R> data) {
        super(total,size,current,data);
        this.code = code;
        this.message = message;
    }

    @Override
    public String getTraceId() {
        return traceId;
    }

    @Override
    public void setTraceId(String traceId) {
        this.traceId = traceId;
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
