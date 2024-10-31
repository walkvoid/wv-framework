package com.github.walkvoid.wvframework.models;

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

    public static <R> DubboResponse<R> of(boolean isSuccess, R data, Exception exception){
        return new DubboResponse<>(isSuccess, data, exception);
    }

    public static <R> DubboResponse<R> success(R data){
        return new DubboResponse<>(true, data, null);
    }

    public static <R> DubboResponse<R> fail(R data, Exception exception){
        return new DubboResponse<>(false, data, exception);
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
