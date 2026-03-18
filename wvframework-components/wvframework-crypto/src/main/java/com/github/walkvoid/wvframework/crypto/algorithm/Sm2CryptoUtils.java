package com.github.walkvoid.wvframework.crypto.algorithm;

import com.github.walkvoid.wvframework.crypto.internal.DefaultCryptoOperations;
import com.github.walkvoid.wvframework.crypto.model.CryptoConfigurations;
import com.github.walkvoid.wvframework.crypto.model.CryptoContext;

public final class Sm2CryptoUtils {

    private Sm2CryptoUtils() {
    }

    public static CryptoConfigurations defaultConfigurations() {
        return CryptoConfigurations.builder()
                .algorithm("SM2")
                .provider("BC")
                .signatureAlgorithm("SM3withSM2")
                .build();
    }

    public static byte[] encrypt(byte[] plain, CryptoContext context) {
        return DefaultCryptoOperations.INSTANCE.encrypt(plain, context);
    }

    public static byte[] decrypt(byte[] cipher, CryptoContext context) {
        return DefaultCryptoOperations.INSTANCE.decrypt(cipher, context);
    }

    public static byte[] sign(byte[] data, CryptoContext context) {
        return DefaultCryptoOperations.INSTANCE.sign(data, context);
    }

    public static boolean verify(byte[] data, byte[] signature, CryptoContext context) {
        return DefaultCryptoOperations.INSTANCE.verify(data, signature, context);
    }
}
