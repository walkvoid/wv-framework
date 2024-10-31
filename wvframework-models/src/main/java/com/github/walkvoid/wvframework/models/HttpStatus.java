package com.github.walkvoid.wvframework.models;

/**
 * @author walkvoid
 * @version v1.0.0
 * @date 2023/9/15
 * @desc http状态，只定义了一些业务系统会用到的
 * 强烈建议所有的web响应都使用下面这些浏览器可识别的状态码，为了区分不同的错误，建议根据不同错误场景返回不同的错误码，而不是所有的状态码
 * 都返回200，将错误码单独定义在响应信息里面，这会曲解http状态码的含义，并且在此种情况下，当你需要在众多请求找出异常的那一个将变得更困难。
 */
public enum HttpStatus {

    /**
     * {@code 200 OK}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.3.1">HTTP/1.1: Semantics and Content, section 6.3.1</a>
     */
    OK(200,  "OK"),

    /**
     * {@code 400 Bad Request}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.1">HTTP/1.1: Semantics and Content, section 6.5.1</a>
     */
    BAD_REQUEST(400,  "Bad Request"),

    /**
     * {@code 401 Unauthorized}.
     * @see <a href="https://tools.ietf.org/html/rfc7235#section-3.1">HTTP/1.1: Authentication, section 3.1</a>
     */
    UNAUTHORIZED(401,  "Unauthorized"),

    /**
     * {@code 402 Payment Required}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.2">HTTP/1.1: Semantics and Content, section 6.5.2</a>
     */
    PAYMENT_REQUIRED(402,  "Payment Required"),

    /**
     * {@code 403 Forbidden}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.3">HTTP/1.1: Semantics and Content, section 6.5.3</a>
     */
    FORBIDDEN(403,  "Forbidden"),

    /**
     * {@code 404 Not Found}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.5.4">HTTP/1.1: Semantics and Content, section 6.5.4</a>
     */
    NOT_FOUND(404,  "Not Found"),

    /**
     * {@code 500 Internal Server Error}.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-6.6.1">HTTP/1.1: Semantics and Content, section 6.6.1</a>
     */
    INTERNAL_SERVER_ERROR(500,"Internal Server Error"),

    ;

    private final int value;

    private final String message;

    HttpStatus(int value, String message) {
        this.value = value;
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }
}
