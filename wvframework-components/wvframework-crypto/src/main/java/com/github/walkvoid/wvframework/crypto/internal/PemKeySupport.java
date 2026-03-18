package com.github.walkvoid.wvframework.crypto.internal;

import com.github.walkvoid.wvframework.crypto.CryptoAlgorithmType;
import com.github.walkvoid.wvframework.crypto.CryptoException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import java.io.StringReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * PEM 解析（依赖 BouncyCastle PKIX）。
 */
public final class PemKeySupport {

    private PemKeySupport() {
    }

    public static PublicKey parsePublicKey(String pem, CryptoAlgorithmType type) {
        if (pem == null || pem.trim().isEmpty()) {
            throw new CryptoException("publicKeyPem 为空");
        }
        BouncyCastleSupport.ensureProvider();
        String trimmed = pem.trim();
        try {
            if (type == CryptoAlgorithmType.SM2) {
                try (PEMParser parser = new PEMParser(new StringReader(trimmed))) {
                    Object o = parser.readObject();
                    JcaPEMKeyConverter conv = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);
                    if (o instanceof org.bouncycastle.asn1.x509.SubjectPublicKeyInfo) {
                        return conv.getPublicKey((org.bouncycastle.asn1.x509.SubjectPublicKeyInfo) o);
                    }
                }
            }
            byte[] der = pemToDer(trimmed);
            String keyAlg = (type == CryptoAlgorithmType.RSA || type == CryptoAlgorithmType.SM2) ? "RSA" : "EC";
            if (type == CryptoAlgorithmType.SM2) {
                keyAlg = "EC";
            }
            try {
                return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(der));
            } catch (Exception e) {
                return KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME).generatePublic(new X509EncodedKeySpec(der));
            }
        } catch (Exception e) {
            throw new CryptoException("解析公钥失败", e);
        }
    }

    public static PrivateKey parsePrivateKey(String pem, char[] password, CryptoAlgorithmType type) {
        if (pem == null || pem.trim().isEmpty()) {
            throw new CryptoException("privateKeyPem 为空");
        }
        BouncyCastleSupport.ensureProvider();
        String trimmed = pem.trim();
        try (PEMParser parser = new PEMParser(new StringReader(trimmed))) {
            Object o = parser.readObject();
            JcaPEMKeyConverter conv = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);
            if (o instanceof PEMEncryptedKeyPair) {
                if (password == null) {
                    throw new CryptoException("私钥已加密，需提供 password");
                }
                PEMKeyPair kp = ((PEMEncryptedKeyPair) o).decryptKeyPair(
                        new JcePEMDecryptorProviderBuilder().build(password));
                return conv.getPrivateKey(kp.getPrivateKeyInfo());
            }
            if (o instanceof PEMKeyPair) {
                return conv.getPrivateKey(((PEMKeyPair) o).getPrivateKeyInfo());
            }
            if (o instanceof org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo) {
                if (password == null) {
                    throw new CryptoException("私钥已加密，需提供 password");
                }
                org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo enc =
                        (org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo) o;
                PrivateKeyInfo info = enc.decryptPrivateKeyInfo(
                        new JceOpenSSLPKCS8DecryptorProviderBuilder().build(password));
                return conv.getPrivateKey(info);
            }
            if (o instanceof PrivateKeyInfo) {
                return conv.getPrivateKey((PrivateKeyInfo) o);
            }
            byte[] der = pemToDer(trimmed);
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
        } catch (CryptoException e) {
            throw e;
        } catch (Exception e) {
            try {
                byte[] der = pemToDer(trimmed);
                return KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME)
                        .generatePrivate(new PKCS8EncodedKeySpec(der));
            } catch (Exception e2) {
                throw new CryptoException("解析私钥失败", e);
            }
        }
    }

    private static byte[] pemToDer(String pem) {
        String b64 = pem.replaceAll("-----BEGIN [^-]+-----", "")
                .replaceAll("-----END [^-]+-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(b64);
    }
}
