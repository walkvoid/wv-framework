package com.github.walkvoid.wvframework.core;

import com.github.walkvoid.wvframework.models.ApiResult;
import com.github.walkvoid.wvframework.models.BaseResultCodeEnum;
import com.github.walkvoid.wvframework.models.BizException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // --------------------------1、自定义业务异常 BizException --------------------------
    @ExceptionHandler(BizException.class)
    public ResponseEntity<ApiResult<?>> handleBizException(BizException e) {
        ApiResult<?> apiResult = ApiResult.error(e.getCode(), e.getMsg());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResult);
    }

    // --------------------------2、JSR303 @Valid 参数校验异常（JSON请求体） --------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<?>> handleValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String errorMsg = fieldErrors.isEmpty() ? "参数非法" : fieldErrors.get(0).getDefaultMessage();
        ApiResult<?> resp = ApiResult.error(BaseResultCodeEnum.PARAM_INVALID.getCode(), errorMsg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    // --------------------------3、表单提交 @Valid 绑定异常 --------------------------
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResult<?>> handleBindException(BindException e) {
        String errorMsg = e.getFieldError() != null ? e.getFieldError().getDefaultMessage() : "参数非法";
        ApiResult<?> resp = ApiResult.error(BaseResultCodeEnum.PARAM_INVALID.getCode(), errorMsg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    // --------------------------4、请求方法不支持 405 --------------------------
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResult<?>> handleMethodNotSupport(HttpRequestMethodNotSupportedException e) {
        ApiResult<?> resp = ApiResult.error(405, "请求方法不支持");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(resp);
    }

    // --------------------------5、Content-Type 不支持 415 --------------------------
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResult<?>> handleMediaType(HttpMediaTypeNotSupportedException e) {
        ApiResult<?> resp = ApiResult.error(415, "请求数据格式不支持");
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(resp);
    }

    // --------------------------6、权限拒绝 403 --------------------------
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResult<?>> handleAccessDenied(AccessDeniedException e) {
        ApiResult<?> resp = ApiResult.error(403, "没有访问权限");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resp);
    }

    // --------------------------7、兜底捕获：所有其他未知异常 --------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<?>> handleAllException(Exception e) {
        ApiResult<?> resp = ApiResult.error(
                BaseResultCodeEnum.UNKNOWN_ERROR.getCode(),
                BaseResultCodeEnum.UNKNOWN_ERROR.getMsg());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }
}
