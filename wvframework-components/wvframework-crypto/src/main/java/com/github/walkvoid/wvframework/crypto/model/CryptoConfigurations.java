package com.github.walkvoid.wvframework.crypto.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 算法与分组参数（不含密钥加载器）。
 */
public final class CryptoConfigurations {

    private final String algorithm;
    private final String transformation;
    private final String mode;
    private final String padding;
    private final Integer blockSize;
    private final byte[] iv;
    private final byte[] nonce;
    private final String signatureAlgorithm;
    private final String provider;
    private final byte[] sm2UserId;
    private final String oaepHash;
    private final Map<String, Object> extended;

    private CryptoConfigurations(Builder b) {
        this.algorithm = b.algorithm;
        this.transformation = b.transformation;
        this.mode = b.mode;
        this.padding = b.padding;
        this.blockSize = b.blockSize;
        this.iv = b.iv;
        this.nonce = b.nonce;
        this.signatureAlgorithm = b.signatureAlgorithm;
        this.provider = b.provider;
        this.sm2UserId = b.sm2UserId;
        this.oaepHash = b.oaepHash;
        this.extended = b.extended != null ? Collections.unmodifiableMap(new HashMap<>(b.extended)) : Collections.emptyMap();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static CryptoConfigurations merge(CryptoConfigurations base, CryptoConfigurations overlay) {
        if (base == null) {
            return overlay;
        }
        if (overlay == null) {
            return base;
        }
        Builder b = base.toBuilder();
        if (overlay.algorithm != null) {
            b.algorithm(overlay.algorithm);
        }
        if (overlay.transformation != null) {
            b.transformation(overlay.transformation);
        }
        if (overlay.mode != null) {
            b.mode(overlay.mode);
        }
        if (overlay.padding != null) {
            b.padding(overlay.padding);
        }
        if (overlay.blockSize != null) {
            b.blockSize(overlay.blockSize);
        }
        if (overlay.iv != null) {
            b.iv(overlay.iv);
        }
        if (overlay.nonce != null) {
            b.nonce(overlay.nonce);
        }
        if (overlay.signatureAlgorithm != null) {
            b.signatureAlgorithm(overlay.signatureAlgorithm);
        }
        if (overlay.provider != null) {
            b.provider(overlay.provider);
        }
        if (overlay.sm2UserId != null) {
            b.sm2UserId(overlay.sm2UserId);
        }
        if (overlay.oaepHash != null) {
            b.oaepHash(overlay.oaepHash);
        }
        if (!overlay.extended.isEmpty()) {
            Map<String, Object> m = new HashMap<>(b.extended != null ? b.extended : new HashMap<>());
            m.putAll(overlay.extended);
            b.extended(m);
        }
        return b.build();
    }

    public Builder toBuilder() {
        Builder b = builder().algorithm(algorithm);
        b.transformation(transformation).mode(mode).padding(padding).blockSize(blockSize);
        b.iv(iv).nonce(nonce).signatureAlgorithm(signatureAlgorithm).provider(provider);
        b.sm2UserId(sm2UserId).oaepHash(oaepHash);
        if (!extended.isEmpty()) {
            b.extended(new HashMap<>(extended));
        }
        return b;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getTransformation() {
        return transformation;
    }

    public String getMode() {
        return mode;
    }

    public String getPadding() {
        return padding;
    }

    public Integer getBlockSize() {
        return blockSize;
    }

    public byte[] getIv() {
        return iv;
    }

    public byte[] getNonce() {
        return nonce;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public String getProvider() {
        return provider;
    }

    public byte[] getSm2UserId() {
        return sm2UserId;
    }

    public String getOaepHash() {
        return oaepHash;
    }

    public Map<String, Object> getExtended() {
        return extended;
    }

    public static final class Builder {
        private String algorithm;
        private String transformation;
        private String mode;
        private String padding;
        private Integer blockSize;
        private byte[] iv;
        private byte[] nonce;
        private String signatureAlgorithm;
        private String provider;
        private byte[] sm2UserId;
        private String oaepHash;
        private Map<String, Object> extended;

        public Builder algorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder transformation(String transformation) {
            this.transformation = transformation;
            return this;
        }

        public Builder mode(String mode) {
            this.mode = mode;
            return this;
        }

        public Builder padding(String padding) {
            this.padding = padding;
            return this;
        }

        public Builder blockSize(Integer blockSize) {
            this.blockSize = blockSize;
            return this;
        }

        public Builder iv(byte[] iv) {
            this.iv = iv;
            return this;
        }

        public Builder nonce(byte[] nonce) {
            this.nonce = nonce;
            return this;
        }

        public Builder signatureAlgorithm(String signatureAlgorithm) {
            this.signatureAlgorithm = signatureAlgorithm;
            return this;
        }

        public Builder provider(String provider) {
            this.provider = provider;
            return this;
        }

        public Builder sm2UserId(byte[] sm2UserId) {
            this.sm2UserId = sm2UserId;
            return this;
        }

        public Builder oaepHash(String oaepHash) {
            this.oaepHash = oaepHash;
            return this;
        }

        public Builder extended(Map<String, Object> extended) {
            this.extended = extended;
            return this;
        }

        public CryptoConfigurations build() {
            Objects.requireNonNull(algorithm, "algorithm");
            return new CryptoConfigurations(this);
        }
    }
}
