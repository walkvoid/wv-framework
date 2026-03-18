package com.github.walkvoid.wvframework.crypto.model;

import com.github.walkvoid.wvframework.crypto.loader.PrivateKeyLoader;
import com.github.walkvoid.wvframework.crypto.loader.PublicKeyLoader;
import com.github.walkvoid.wvframework.crypto.loader.SymmetricKeyLoader;

import java.util.Objects;

/**
 * 一次加解密/验签的完整上下文。禁止在 toString 中打印密钥或 IV。
 */
public final class CryptoContext {

    private final PublicKeyLoader publicKeyLoader;
    private final PrivateKeyLoader privateKeyLoader;
    private final SymmetricKeyLoader symmetricKeyLoader;
    private final CryptoConfigurations configurations;

    private CryptoContext(Builder b) {
        this.publicKeyLoader = b.publicKeyLoader;
        this.privateKeyLoader = b.privateKeyLoader;
        this.symmetricKeyLoader = b.symmetricKeyLoader;
        this.configurations = Objects.requireNonNull(b.configurations, "configurations");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static CryptoContext merge(CryptoContext base, CryptoContext overlay) {
        if (base == null) {
            return overlay;
        }
        if (overlay == null) {
            return base;
        }
        return builder()
                .publicKeyLoader(overlay.publicKeyLoader != null ? overlay.publicKeyLoader : base.publicKeyLoader)
                .privateKeyLoader(overlay.privateKeyLoader != null ? overlay.privateKeyLoader : base.privateKeyLoader)
                .symmetricKeyLoader(overlay.symmetricKeyLoader != null ? overlay.symmetricKeyLoader : base.symmetricKeyLoader)
                .configurations(CryptoConfigurations.merge(base.configurations, overlay.configurations))
                .build();
    }

    /**
     * 使用指定算法类型覆盖配置中的 algorithm 字段。
     */
    public CryptoContext withAlgorithm(String algorithm) {
        CryptoConfigurations cfg = configurations.toBuilder().algorithm(algorithm).build();
        return builder()
                .publicKeyLoader(publicKeyLoader)
                .privateKeyLoader(privateKeyLoader)
                .symmetricKeyLoader(symmetricKeyLoader)
                .configurations(cfg)
                .build();
    }

    public PublicKeyLoader getPublicKeyLoader() {
        return publicKeyLoader;
    }

    public PrivateKeyLoader getPrivateKeyLoader() {
        return privateKeyLoader;
    }

    public SymmetricKeyLoader getSymmetricKeyLoader() {
        return symmetricKeyLoader;
    }

    public CryptoConfigurations getConfigurations() {
        return configurations;
    }

    @Override
    public String toString() {
        return "CryptoContext{algorithm=" + configurations.getAlgorithm() + "}";
    }

    public static final class Builder {
        private PublicKeyLoader publicKeyLoader;
        private PrivateKeyLoader privateKeyLoader;
        private SymmetricKeyLoader symmetricKeyLoader;
        private CryptoConfigurations configurations;

        public Builder publicKeyLoader(PublicKeyLoader publicKeyLoader) {
            this.publicKeyLoader = publicKeyLoader;
            return this;
        }

        public Builder privateKeyLoader(PrivateKeyLoader privateKeyLoader) {
            this.privateKeyLoader = privateKeyLoader;
            return this;
        }

        public Builder symmetricKeyLoader(SymmetricKeyLoader symmetricKeyLoader) {
            this.symmetricKeyLoader = symmetricKeyLoader;
            return this;
        }

        public Builder configurations(CryptoConfigurations configurations) {
            this.configurations = configurations;
            return this;
        }

        public CryptoContext build() {
            return new CryptoContext(this);
        }
    }
}
