package com.wvframework.models;

/**
 * @author jiangjunqing
 * @date 2024/5/30
 * @description:
 * @version:
 */
public class DubboResponse<R> extends Traceable {

    private boolean isSuccess;

    public  R data;

    private Exception exception;

    public static DubboResponse of(){
        return null;
    }

    private DubboResponse(boolean isSuccess, R data, Exception exception) {
        this.isSuccess = isSuccess;
        this.exception = exception;
        this.data = data;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
