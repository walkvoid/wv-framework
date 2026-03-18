package com.github.walkvoid.wvframework.crypto;

/**
 * 支持的算法类型，供 {@link CryptoUtils} 与 Spring 配置选用。
 */
public enum CryptoAlgorithmType {
    AES,
    SM4,
    DES,
    DESEDE,
    RSA,
    SM2,
    EC
}
