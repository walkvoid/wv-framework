package com.github.walkvoid.wvframework.crypto;

import com.github.walkvoid.wvframework.crypto.model.CryptoContext;

/**
 * 通用入口：通过算法类型选择实现，入参统一为 {@link CryptoContext}。
 */
public final class CryptoUtils {

    private CryptoUtils() {
    }

    public static byte[] encrypt(CryptoAlgorithmType type, byte[] plain, CryptoContext context) {
        CryptoContext ctx = context.withAlgorithm(type.name());
        return AlgorithmRegistry.resolve(type).encrypt(plain, ctx);
    }

    public static byte[] decrypt(CryptoAlgorithmType type, byte[] cipher, CryptoContext context) {
        CryptoContext ctx = context.withAlgorithm(type.name());
        return AlgorithmRegistry.resolve(type).decrypt(cipher, ctx);
    }

    public static byte[] sign(CryptoAlgorithmType type, byte[] data, CryptoContext context) {
        CryptoContext ctx = context.withAlgorithm(type.name());
        return AlgorithmRegistry.resolve(type).sign(data, ctx);
    }

    public static boolean verify(CryptoAlgorithmType type, byte[] data, byte[] signature, CryptoContext context) {
        CryptoContext ctx = context.withAlgorithm(type.name());
        return AlgorithmRegistry.resolve(type).verify(data, signature, ctx);
    }
}
