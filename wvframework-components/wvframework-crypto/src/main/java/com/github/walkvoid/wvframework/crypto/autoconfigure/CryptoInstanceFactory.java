package com.github.walkvoid.wvframework.crypto.autoconfigure;

import com.github.walkvoid.wvframework.crypto.CryptoAlgorithmType;
import com.github.walkvoid.wvframework.crypto.CryptoException;
import com.github.walkvoid.wvframework.crypto.loader.Base64SymmetricKeyLoader;
import com.github.walkvoid.wvframework.crypto.loader.ByteSymmetricKeyLoader;
import com.github.walkvoid.wvframework.crypto.loader.PemPrivateKeyLoader;
import com.github.walkvoid.wvframework.crypto.loader.PemPublicKeyLoader;
import com.github.walkvoid.wvframework.crypto.loader.PrivateKeyLoader;
import com.github.walkvoid.wvframework.crypto.loader.PublicKeyLoader;
import com.github.walkvoid.wvframework.crypto.loader.SymmetricKeyLoader;
import com.github.walkvoid.wvframework.crypto.model.CryptoConfigurations;
import com.github.walkvoid.wvframework.crypto.model.CryptoContext;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class CryptoInstanceFactory {

    private CryptoInstanceFactory() {
    }

    public static CryptoContext build(CryptoProperties.InstanceConfig ic) {
        if (ic.getAlgo() == null || ic.getAlgo().getType() == null || ic.getAlgo().getType().isEmpty()) {
            throw new CryptoException("crypto.instances.*.algo.type 不能为空");
        }
        CryptoAlgorithmType type;
        try {
            type = CryptoAlgorithmType.valueOf(ic.getAlgo().getType().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CryptoException("不支持的算法类型: " + ic.getAlgo().getType(), e);
        }
        CryptoProperties.Configurations src = ic.getConfigurations();
        CryptoProperties.Loaders L = ic.getLoaders();
        CryptoConfigurations.Builder cb = CryptoConfigurations.builder().algorithm(type.name());
        if (src != null) {
            if (src.getTransformation() != null) {
                cb.transformation(src.getTransformation());
            }
            if (src.getMode() != null) {
                cb.mode(src.getMode());
            }
            if (src.getPadding() != null) {
                cb.padding(src.getPadding());
            }
            if (src.getBlockSize() != null) {
                cb.blockSize(src.getBlockSize());
            }
            if (src.getIvBase64() != null && !src.getIvBase64().isEmpty()) {
                cb.iv(Base64.getDecoder().decode(src.getIvBase64().trim()));
            }
            if (src.getNonceBase64() != null && !src.getNonceBase64().isEmpty()) {
                cb.nonce(Base64.getDecoder().decode(src.getNonceBase64().trim()));
            }
            if (src.getSignatureAlgorithm() != null) {
                cb.signatureAlgorithm(src.getSignatureAlgorithm());
            }
            if (src.getProvider() != null) {
                cb.provider(src.getProvider());
            }
            if (src.getSm2UserId() != null && !src.getSm2UserId().isEmpty()) {
                cb.sm2UserId(src.getSm2UserId().getBytes(StandardCharsets.UTF_8));
            }
            if (src.getOaepHash() != null) {
                cb.oaepHash(src.getOaepHash());
            }
        }
        PublicKeyLoader pub = blank(L.getPublicKeyPem()) ? null : new PemPublicKeyLoader(L.getPublicKeyPem(), type);
        PrivateKeyLoader priv = blank(L.getPrivateKeyPem()) ? null
                : new PemPrivateKeyLoader(L.getPrivateKeyPem(),
                L.getPrivateKeyPassword() != null ? L.getPrivateKeyPassword().toCharArray() : null, type);
        SymmetricKeyLoader sym = blank(L.getSymmetricKeyRef()) ? null : symmetricLoader(L.getSymmetricKeyRef());
        return CryptoContext.builder()
                .publicKeyLoader(pub)
                .privateKeyLoader(priv)
                .symmetricKeyLoader(sym)
                .configurations(cb.build())
                .build();
    }

    private static SymmetricKeyLoader symmetricLoader(String ref) {
        String r = ref.trim();
        if (r.startsWith("hex:")) {
            return hexLoader(r.substring(4).trim());
        }
        if (r.startsWith("base64:")) {
            return new Base64SymmetricKeyLoader(r.substring(7));
        }
        return new Base64SymmetricKeyLoader(r);
    }

    private static SymmetricKeyLoader hexLoader(String hex) {
        int n = hex.length();
        if ((n & 1) != 0) {
            throw new CryptoException("对称密钥 hex 长度须为偶数");
        }
        byte[] out = new byte[n / 2];
        for (int i = 0; i < n; i += 2) {
            out[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return new ByteSymmetricKeyLoader(out);
    }

    private static boolean blank(String s) {
        return s == null || s.isEmpty();
    }
}
