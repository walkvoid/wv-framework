package com.github.walkvoid.wvframework.crypto;

/**
 * 加解密/验签失败时抛出，保留 cause 便于排查。
 */
public class CryptoException extends RuntimeException {

    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
