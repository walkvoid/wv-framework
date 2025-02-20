package com.github.walkvoid.wvframework.models;

/**
 * @author jiangjunqing
 * @date 2024/12/30
 * @description:
 * @version:
 */
public class CommonResponse<R> implements BaseResponse<String, R>, Traceable {
    private static final long serialVersionUID = -4436287742766304519L;

    private String traceId;

    private String code;

    private String message;

    /**
     * 返回的数据
     */
    private R data;

    protected CommonResponse(String code, String message, R data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 提供一个更灵活的构造WebResponse的方法
     * @param code
     * @param message
     * @param data
     * @return
     * @param <R>
     */
    public static <R> CommonResponse<R> of(String code, String message, R data) {
        return new CommonResponse<R>(code, message, data);
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getTraceId() {
        return traceId;
    }

    @Override
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public R getData() {
        return data;
    }

    public void setData(R data) {
        this.data = data;
    }
}
