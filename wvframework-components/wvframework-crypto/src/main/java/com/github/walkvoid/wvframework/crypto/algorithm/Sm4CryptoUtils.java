package com.github.walkvoid.wvframework.crypto.algorithm;

import com.github.walkvoid.wvframework.crypto.internal.DefaultCryptoOperations;
import com.github.walkvoid.wvframework.crypto.model.CryptoConfigurations;
import com.github.walkvoid.wvframework.crypto.model.CryptoContext;

public final class Sm4CryptoUtils {

    private Sm4CryptoUtils() {
    }

    public static CryptoConfigurations defaultConfigurations() {
        return CryptoConfigurations.builder()
                .algorithm("SM4")
                .mode("CBC")
                .padding("PKCS5Padding")
                .provider("BC")
                .blockSize(16)
                .build();
    }

    public static byte[] encrypt(byte[] plain, CryptoContext context) {
        return DefaultCryptoOperations.INSTANCE.encrypt(plain, context);
    }

    public static byte[] decrypt(byte[] cipher, CryptoContext context) {
        return DefaultCryptoOperations.INSTANCE.decrypt(cipher, context);
    }
}
