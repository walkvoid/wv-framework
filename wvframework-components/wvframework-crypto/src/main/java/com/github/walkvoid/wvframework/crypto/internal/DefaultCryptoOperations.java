package com.github.walkvoid.wvframework.crypto.internal;

import com.github.walkvoid.wvframework.crypto.CryptoException;
import com.github.walkvoid.wvframework.crypto.api.CryptoOperations;
import com.github.walkvoid.wvframework.crypto.model.CryptoConfigurations;
import com.github.walkvoid.wvframework.crypto.model.CryptoContext;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Locale;

/**
 * 无状态实现；Cipher/Signature 每次调用新建（线程安全）。
 */
public final class DefaultCryptoOperations implements CryptoOperations {

    public static final DefaultCryptoOperations INSTANCE = new DefaultCryptoOperations();

    private static final byte[] DEFAULT_SM2_USER_ID = "1234567812345678".getBytes(java.nio.charset.StandardCharsets.UTF_8);

    private DefaultCryptoOperations() {
    }

    @Override
    public byte[] encrypt(byte[] plain, CryptoContext context) {
        if (plain == null) {
            throw new CryptoException("plain 不能为空");
        }
        CryptoConfigurations c = context.getConfigurations();
        String alg = norm(c.getAlgorithm());
        try {
            if (isSymmetric(alg)) {
                return encryptSymmetric(context, plain, c, alg);
            }
            if ("RSA".equals(alg)) {
                return encryptAsymmetricRsa(context, plain, c);
            }
            if ("SM2".equals(alg)) {
                return encryptSm2(context, plain, c);
            }
            if ("EC".equals(alg)) {
                return encryptEc(context, plain, c);
            }
            throw new CryptoException("不支持的加密算法: " + alg);
        } catch (CryptoException e) {
            throw e;
        } catch (Exception e) {
            throw new CryptoException("加密失败", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] cipher, CryptoContext context) {
        if (cipher == null) {
            throw new CryptoException("cipher 不能为空");
        }
        CryptoConfigurations c = context.getConfigurations();
        String alg = norm(c.getAlgorithm());
        try {
            if (isSymmetric(alg)) {
                return decryptSymmetric(context, cipher, c, alg);
            }
            if ("RSA".equals(alg)) {
                return decryptAsymmetricRsa(context, cipher, c);
            }
            if ("SM2".equals(alg)) {
                return decryptSm2(context, cipher, c);
            }
            if ("EC".equals(alg)) {
                return decryptEc(context, cipher, c);
            }
            throw new CryptoException("不支持的解密算法: " + alg);
        } catch (CryptoException e) {
            throw e;
        } catch (Exception e) {
            throw new CryptoException("解密失败", e);
        }
    }

    @Override
    public byte[] sign(byte[] data, CryptoContext context) {
        if (data == null) {
            throw new CryptoException("data 不能为空");
        }
        if (context.getPrivateKeyLoader() == null) {
            throw new CryptoException("签名需要 privateKeyLoader");
        }
        CryptoConfigurations c = context.getConfigurations();
        String alg = norm(c.getAlgorithm());
        try {
            PrivateKey pk = context.getPrivateKeyLoader().loadPrivateKey();
            if ("SM2".equals(alg)) {
                return signSm2(data, pk, c);
            }
            if ("RSA".equals(alg) || "EC".equals(alg)) {
                String sigAlg = signatureAlgorithm(c, alg);
                Signature sig = createSignature(sigAlg, c);
                sig.initSign(pk);
                sig.update(data);
                return sig.sign();
            }
            throw new CryptoException("算法不支持签名: " + alg);
        } catch (CryptoException e) {
            throw e;
        } catch (Exception e) {
            throw new CryptoException("签名失败", e);
        }
    }

    @Override
    public boolean verify(byte[] data, byte[] signature, CryptoContext context) {
        if (data == null || signature == null) {
            return false;
        }
        if (context.getPublicKeyLoader() == null) {
            throw new CryptoException("验签需要 publicKeyLoader");
        }
        CryptoConfigurations c = context.getConfigurations();
        String alg = norm(c.getAlgorithm());
        try {
            PublicKey pub = context.getPublicKeyLoader().loadPublicKey();
            if ("SM2".equals(alg)) {
                return verifySm2(data, signature, pub, c);
            }
            if ("RSA".equals(alg) || "EC".equals(alg)) {
                String sigAlg = signatureAlgorithm(c, alg);
                Signature sig = createSignature(sigAlg, c);
                sig.initVerify(pub);
                sig.update(data);
                return sig.verify(signature);
            }
            throw new CryptoException("算法不支持验签: " + alg);
        } catch (CryptoException e) {
            throw e;
        } catch (Exception e) {
            throw new CryptoException("验签失败", e);
        }
    }

    private static byte[] encryptSymmetric(CryptoContext ctx, byte[] plain, CryptoConfigurations c, String alg) throws Exception {
        if (ctx.getSymmetricKeyLoader() == null) {
            throw new CryptoException("对称加密需要 symmetricKeyLoader");
        }
        byte[] keyBytes = ctx.getSymmetricKeyLoader().loadSymmetricKeyBytes();
        String transformation = resolveTransformation(c, alg);
        String provider = providerName(c, alg);
        Cipher cipher = provider != null ? Cipher.getInstance(transformation, provider) : Cipher.getInstance(transformation);
        SecretKey key = new SecretKeySpec(keyBytes, baseAlg(alg));
        initCipherSymmetric(cipher, Cipher.ENCRYPT_MODE, key, c, transformation);
        return cipher.doFinal(plain);
    }

    private static byte[] decryptSymmetric(CryptoContext ctx, byte[] cipherBytes, CryptoConfigurations c, String alg) throws Exception {
        if (ctx.getSymmetricKeyLoader() == null) {
            throw new CryptoException("对称解密需要 symmetricKeyLoader");
        }
        byte[] keyBytes = ctx.getSymmetricKeyLoader().loadSymmetricKeyBytes();
        String transformation = resolveTransformation(c, alg);
        String provider = providerName(c, alg);
        Cipher cipher = provider != null ? Cipher.getInstance(transformation, provider) : Cipher.getInstance(transformation);
        SecretKey key = new SecretKeySpec(keyBytes, baseAlg(alg));
        initCipherSymmetric(cipher, Cipher.DECRYPT_MODE, key, c, transformation);
        return cipher.doFinal(cipherBytes);
    }

    private static void initCipherSymmetric(Cipher cipher, int mode, SecretKey key, CryptoConfigurations c, String transformation) throws Exception {
        String modePart = extractMode(transformation);
        if ("GCM".equalsIgnoreCase(modePart)) {
            byte[] nonce = c.getNonce() != null ? c.getNonce() : c.getIv();
            if (nonce == null) {
                throw new CryptoException("GCM 模式需要 nonce 或 iv");
            }
            cipher.init(mode, key, new GCMParameterSpec(128, nonce));
        } else if (needsIv(modePart)) {
            byte[] iv = c.getIv();
            if (iv == null) {
                throw new CryptoException(modePart + " 模式需要 iv");
            }
            cipher.init(mode, key, new IvParameterSpec(iv));
        } else {
            cipher.init(mode, key);
        }
    }

    private static byte[] encryptAsymmetricRsa(CryptoContext ctx, byte[] plain, CryptoConfigurations c) throws Exception {
        if (ctx.getPublicKeyLoader() == null) {
            throw new CryptoException("RSA 加密需要 publicKeyLoader");
        }
        PublicKey pub = ctx.getPublicKeyLoader().loadPublicKey();
        String transformation = resolveTransformation(c, "RSA");
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, pub);
        return cipher.doFinal(plain);
    }

    private static byte[] decryptAsymmetricRsa(CryptoContext ctx, byte[] cipherBytes, CryptoConfigurations c) throws Exception {
        if (ctx.getPrivateKeyLoader() == null) {
            throw new CryptoException("RSA 解密需要 privateKeyLoader");
        }
        PrivateKey pk = ctx.getPrivateKeyLoader().loadPrivateKey();
        String transformation = resolveTransformation(c, "RSA");
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, pk);
        return cipher.doFinal(cipherBytes);
    }

    private static byte[] encryptSm2(CryptoContext ctx, byte[] plain, CryptoConfigurations c) throws Exception {
        BouncyCastleSupport.ensureProvider();
        if (ctx.getPublicKeyLoader() == null) {
            throw new CryptoException("SM2 加密需要 publicKeyLoader");
        }
        PublicKey pub = ctx.getPublicKeyLoader().loadPublicKey();
        Cipher cipher = Cipher.getInstance("SM2", BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, pub, new SecureRandom());
        return cipher.doFinal(plain);
    }

    private static byte[] decryptSm2(CryptoContext ctx, byte[] cipherBytes, CryptoConfigurations c) throws Exception {
        BouncyCastleSupport.ensureProvider();
        if (ctx.getPrivateKeyLoader() == null) {
            throw new CryptoException("SM2 解密需要 privateKeyLoader");
        }
        PrivateKey pk = ctx.getPrivateKeyLoader().loadPrivateKey();
        Cipher cipher = Cipher.getInstance("SM2", BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(Cipher.DECRYPT_MODE, pk);
        return cipher.doFinal(cipherBytes);
    }

    private static byte[] encryptEc(CryptoContext ctx, byte[] plain, CryptoConfigurations c) throws Exception {
        return encryptAsymmetricRsa(ctx, plain, c);
    }

    private static byte[] decryptEc(CryptoContext ctx, byte[] cipherBytes, CryptoConfigurations c) throws Exception {
        return decryptAsymmetricRsa(ctx, cipherBytes, c);
    }

    private static byte[] signSm2(byte[] data, PrivateKey pk, CryptoConfigurations c) throws Exception {
        BouncyCastleSupport.ensureProvider();
        Signature signature = Signature.getInstance("SM3withSM2", BouncyCastleProvider.PROVIDER_NAME);
        byte[] userId = c.getSm2UserId() != null ? c.getSm2UserId() : DEFAULT_SM2_USER_ID;
        org.bouncycastle.jcajce.spec.SM2ParameterSpec spec = new org.bouncycastle.jcajce.spec.SM2ParameterSpec(userId);
        signature.setParameter(spec);
        signature.initSign(pk);
        signature.update(data);
        return signature.sign();
    }

    private static boolean verifySm2(byte[] data, byte[] sig, PublicKey pub, CryptoConfigurations c) throws Exception {
        BouncyCastleSupport.ensureProvider();
        Signature signature = Signature.getInstance("SM3withSM2", BouncyCastleProvider.PROVIDER_NAME);
        byte[] userId = c.getSm2UserId() != null ? c.getSm2UserId() : DEFAULT_SM2_USER_ID;
        org.bouncycastle.jcajce.spec.SM2ParameterSpec spec = new org.bouncycastle.jcajce.spec.SM2ParameterSpec(userId);
        signature.setParameter(spec);
        signature.initVerify(pub);
        signature.update(data);
        return signature.verify(sig);
    }

    private static Signature createSignature(String sigAlg, CryptoConfigurations c) throws Exception {
        String p = c.getProvider();
        return p != null ? Signature.getInstance(sigAlg, p) : Signature.getInstance(sigAlg);
    }

    private static String signatureAlgorithm(CryptoConfigurations c, String alg) {
        if (c.getSignatureAlgorithm() != null && !c.getSignatureAlgorithm().isEmpty()) {
            return c.getSignatureAlgorithm();
        }
        if ("RSA".equals(alg)) {
            return "SHA256withRSA";
        }
        if ("EC".equals(alg)) {
            return "SHA256withECDSA";
        }
        throw new CryptoException("请配置 signatureAlgorithm");
    }

    private static String resolveTransformation(CryptoConfigurations c, String algorithm) {
        if (c.getTransformation() != null && !c.getTransformation().trim().isEmpty()) {
            return c.getTransformation();
        }
        String mode = c.getMode();
        String pad = c.getPadding();
        if (mode == null || mode.isEmpty()) {
            mode = "RSA".equalsIgnoreCase(algorithm) ? "ECB" : "CBC";
        }
        if (pad == null || pad.isEmpty()) {
            pad = "RSA".equalsIgnoreCase(algorithm) ? "PKCS1Padding" : "PKCS5Padding";
        }
        return algorithm + "/" + mode + "/" + pad;
    }

    private static String providerName(CryptoConfigurations c, String alg) {
        if (c.getProvider() != null && !c.getProvider().isEmpty()) {
            return c.getProvider();
        }
        if ("SM4".equalsIgnoreCase(alg) || "SM2".equalsIgnoreCase(alg)) {
            BouncyCastleSupport.ensureProvider();
            return BouncyCastleProvider.PROVIDER_NAME;
        }
        return null;
    }

    private static String baseAlg(String alg) {
        if ("DESEDE".equalsIgnoreCase(alg)) {
            return "DESede";
        }
        return alg;
    }

    private static boolean isSymmetric(String alg) {
        return "AES".equals(alg) || "SM4".equals(alg) || "DES".equals(alg) || "DESEDE".equals(alg) || "DESede".equals(alg);
    }

    private static String norm(String a) {
        return a == null ? "" : a.trim().toUpperCase(Locale.ROOT);
    }

    private static boolean needsIv(String modePart) {
        if (modePart == null) {
            return true;
        }
        String m = modePart.toUpperCase(Locale.ROOT);
        return !"ECB".equals(m);
    }

    private static String extractMode(String transformation) {
        int i = transformation.indexOf('/');
        if (i < 0) {
            return "ECB";
        }
        int j = transformation.indexOf('/', i + 1);
        if (j < 0) {
            return transformation.substring(i + 1);
        }
        return transformation.substring(i + 1, j);
    }
}
