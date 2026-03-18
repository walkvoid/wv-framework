package com.github.walkvoid.wvframework.crypto.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "crypto")
public class CryptoProperties {

    private boolean enabled = true;
    private String defaultInstance;
    private Map<String, InstanceConfig> instances = new LinkedHashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDefaultInstance() {
        return defaultInstance;
    }

    public void setDefaultInstance(String defaultInstance) {
        this.defaultInstance = defaultInstance;
    }

    public Map<String, InstanceConfig> getInstances() {
        return instances;
    }

    public void setInstances(Map<String, InstanceConfig> instances) {
        this.instances = instances != null ? instances : new LinkedHashMap<>();
    }

    public static class InstanceConfig {
        private Algo algo = new Algo();
        private Configurations configurations = new Configurations();
        private Loaders loaders = new Loaders();

        public Algo getAlgo() {
            return algo;
        }

        public void setAlgo(Algo algo) {
            this.algo = algo != null ? algo : new Algo();
        }

        public Configurations getConfigurations() {
            return configurations;
        }

        public void setConfigurations(Configurations configurations) {
            this.configurations = configurations != null ? configurations : new Configurations();
        }

        public Loaders getLoaders() {
            return loaders;
        }

        public void setLoaders(Loaders loaders) {
            this.loaders = loaders != null ? loaders : new Loaders();
        }
    }

    public static class Algo {
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class Configurations {
        private String mode;
        private String padding;
        private Integer blockSize;
        private String ivBase64;
        private String nonceBase64;
        private String signatureAlgorithm;
        private String provider;
        private String sm2UserId;
        private String oaepHash;
        private String transformation;

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getPadding() {
            return padding;
        }

        public void setPadding(String padding) {
            this.padding = padding;
        }

        public Integer getBlockSize() {
            return blockSize;
        }

        public void setBlockSize(Integer blockSize) {
            this.blockSize = blockSize;
        }

        public String getIvBase64() {
            return ivBase64;
        }

        public void setIvBase64(String ivBase64) {
            this.ivBase64 = ivBase64;
        }

        public String getNonceBase64() {
            return nonceBase64;
        }

        public void setNonceBase64(String nonceBase64) {
            this.nonceBase64 = nonceBase64;
        }

        public String getSignatureAlgorithm() {
            return signatureAlgorithm;
        }

        public void setSignatureAlgorithm(String signatureAlgorithm) {
            this.signatureAlgorithm = signatureAlgorithm;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getSm2UserId() {
            return sm2UserId;
        }

        public void setSm2UserId(String sm2UserId) {
            this.sm2UserId = sm2UserId;
        }

        public String getOaepHash() {
            return oaepHash;
        }

        public void setOaepHash(String oaepHash) {
            this.oaepHash = oaepHash;
        }

        public String getTransformation() {
            return transformation;
        }

        public void setTransformation(String transformation) {
            this.transformation = transformation;
        }
    }

    public static class Loaders {
        private String publicKeyPem;
        private String privateKeyPem;
        private String privateKeyPassword;
        private String symmetricKeyRef;

        public String getPublicKeyPem() {
            return publicKeyPem;
        }

        public void setPublicKeyPem(String publicKeyPem) {
            this.publicKeyPem = publicKeyPem;
        }

        public String getPrivateKeyPem() {
            return privateKeyPem;
        }

        public void setPrivateKeyPem(String privateKeyPem) {
            this.privateKeyPem = privateKeyPem;
        }

        public String getPrivateKeyPassword() {
            return privateKeyPassword;
        }

        public void setPrivateKeyPassword(String privateKeyPassword) {
            this.privateKeyPassword = privateKeyPassword;
        }

        public String getSymmetricKeyRef() {
            return symmetricKeyRef;
        }

        public void setSymmetricKeyRef(String symmetricKeyRef) {
            this.symmetricKeyRef = symmetricKeyRef;
        }
    }
}
