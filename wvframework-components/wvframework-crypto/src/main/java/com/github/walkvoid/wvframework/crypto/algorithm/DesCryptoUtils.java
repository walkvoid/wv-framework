package com.github.walkvoid.wvframework.crypto.algorithm;

import com.github.walkvoid.wvframework.crypto.internal.DefaultCryptoOperations;
import com.github.walkvoid.wvframework.crypto.model.CryptoConfigurations;
import com.github.walkvoid.wvframework.crypto.model.CryptoContext;

/**
 * DES：默认 CBC/PKCS5；密钥 8 字节。ECB 存在安全风险，不推荐生产默认使用。
 */
public final class DesCryptoUtils {

    private DesCryptoUtils() {
    }

    public static CryptoConfigurations defaultConfigurationsDes() {
        return CryptoConfigurations.builder()
                .algorithm("DES")
                .mode("CBC")
                .padding("PKCS5Padding")
                .blockSize(8)
                .build();
    }

    public static CryptoConfigurations defaultConfigurationsDesEde() {
        return CryptoConfigurations.builder()
                .algorithm("DESede")
                .mode("CBC")
                .padding("PKCS5Padding")
                .blockSize(8)
                .build();
    }

    public static byte[] encrypt(byte[] plain, CryptoContext context) {
        return DefaultCryptoOperations.INSTANCE.encrypt(plain, context);
    }

    public static byte[] decrypt(byte[] cipher, CryptoContext context) {
        return DefaultCryptoOperations.INSTANCE.decrypt(cipher, context);
    }
}
