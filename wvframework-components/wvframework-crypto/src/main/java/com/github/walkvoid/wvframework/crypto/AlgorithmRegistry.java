package com.github.walkvoid.wvframework.crypto;

import com.github.walkvoid.wvframework.crypto.api.CryptoOperations;
import com.github.walkvoid.wvframework.crypto.internal.DefaultCryptoOperations;

import java.util.EnumMap;
import java.util.Map;

/**
 * 算法类型到 {@link CryptoOperations} 的注册表；当前均为无状态默认实现。
 */
public final class AlgorithmRegistry {

    private static final Map<CryptoAlgorithmType, CryptoOperations> MAP = new EnumMap<>(CryptoAlgorithmType.class);

    static {
        CryptoOperations core = DefaultCryptoOperations.INSTANCE;
        for (CryptoAlgorithmType t : CryptoAlgorithmType.values()) {
            MAP.put(t, core);
        }
    }

    private AlgorithmRegistry() {
    }

    public static CryptoOperations resolve(CryptoAlgorithmType type) {
        CryptoOperations op = MAP.get(type);
        if (op == null) {
            throw new CryptoException("未注册的算法: " + type);
        }
        return op;
    }

    public static void register(CryptoAlgorithmType type, CryptoOperations operations) {
        MAP.put(type, operations);
    }
}
