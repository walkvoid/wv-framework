package com.github.walkvoid.wvframework.crypto.loader;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@FunctionalInterface
public interface SymmetricKeyLoader {

    /**
     * @return 对称密钥原始字节，长度须符合算法要求
     */
    byte[] loadSymmetricKeyBytes() throws Exception;

    default SecretKey toSecretKey(String algorithm) throws Exception {
        return new SecretKeySpec(loadSymmetricKeyBytes(), algorithm);
    }
}
