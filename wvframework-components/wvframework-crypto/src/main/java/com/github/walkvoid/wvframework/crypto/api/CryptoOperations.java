package com.github.walkvoid.wvframework.crypto.api;

import com.github.walkvoid.wvframework.crypto.model.CryptoContext;

public interface CryptoOperations {

    byte[] encrypt(byte[] plain, CryptoContext context);

    byte[] decrypt(byte[] cipher, CryptoContext context);

    byte[] sign(byte[] data, CryptoContext context);

    boolean verify(byte[] data, byte[] signature, CryptoContext context);
}
