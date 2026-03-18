package com.github.walkvoid.wvframework.crypto.internal;

import com.github.walkvoid.wvframework.crypto.CryptoException;

import java.security.Provider;
import java.security.Security;

/**
 * 懒加载注册 BouncyCastle Provider（SM2/SM4 等）。
 */
public final class BouncyCastleSupport {

    private static volatile boolean registered;

    private BouncyCastleSupport() {
    }

    public static void ensureProvider() {
        if (registered) {
            return;
        }
        synchronized (BouncyCastleSupport.class) {
            if (registered) {
                return;
            }
            try {
                Class<?> clazz = Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
                Provider p = (Provider) clazz.getDeclaredConstructor().newInstance();
                if (Security.getProvider(p.getName()) == null) {
                    Security.addProvider(p);
                }
                registered = true;
            } catch (Exception e) {
                throw new CryptoException("BouncyCastle 不在 classpath，无法使用 SM2/SM4。请添加 bcprov-jdk18on。", e);
            }
        }
    }

    public static boolean isAvailable() {
        try {
            Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
