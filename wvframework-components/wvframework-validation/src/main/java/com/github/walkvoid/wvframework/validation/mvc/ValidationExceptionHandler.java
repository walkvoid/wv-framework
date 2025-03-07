package com.github.walkvoid.wvframework.validation.mvc;

import com.github.walkvoid.wvframework.models.HttpStatus;
import com.github.walkvoid.wvframework.models.MessageLevel;
import com.github.walkvoid.wvframework.models.WebResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;


@RestControllerAdvice
public class ValidationExceptionHandler {

    /**
     * @RequestBody 上校验失败后抛出的异常是 MethodArgumentNotValidException 异常。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public WebResponse<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String messages = bindingResult.getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(";"));
        return WebResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, messages, null, MessageLevel.ERROR);
    }

    /**
     * 不加 @RequestBody注解，校验失败抛出的则是 BindException
     */
    @ExceptionHandler(value = BindException.class)
    public WebResponse<?> exceptionHandler(BindException e) {
        String messages = e.getBindingResult().getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(";"));
        return WebResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, messages, null, MessageLevel.ERROR);
    }

    /**
     * @RequestParam 上校验失败后抛出的异常是 ConstraintViolationException
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public WebResponse<?> methodArgumentNotValid(ConstraintViolationException exception) {
        String messages = exception.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));
        return WebResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, messages, null, MessageLevel.ERROR);
    }
}