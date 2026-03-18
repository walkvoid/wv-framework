package com.github.walkvoid.wvframework.crypto;

import com.github.walkvoid.wvframework.crypto.algorithm.AesCryptoUtils;
import com.github.walkvoid.wvframework.crypto.loader.ByteSymmetricKeyLoader;
import com.github.walkvoid.wvframework.crypto.model.CryptoContext;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class AesRoundTripTest {

    @Test
    void aesCbcRoundTrip() {
        byte[] key = new byte[16];
        new SecureRandom().nextBytes(key);
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        byte[] plain = "hello-crypto".getBytes(StandardCharsets.UTF_8);

        CryptoContext ctx = CryptoContext.builder()
                .symmetricKeyLoader(new ByteSymmetricKeyLoader(key))
                .configurations(AesCryptoUtils.defaultConfigurations().toBuilder().iv(iv).build())
                .build();

        byte[] enc = AesCryptoUtils.encrypt(plain, ctx);
        byte[] dec = AesCryptoUtils.decrypt(enc, ctx);
        assertArrayEquals(plain, dec);
    }

    @Test
    void cryptoUtilsWithType() {
        byte[] key = new byte[16];
        Arrays.fill(key, (byte) 1);
        byte[] iv = new byte[16];
        Arrays.fill(iv, (byte) 2);

        CryptoContext ctx = CryptoContext.builder()
                .symmetricKeyLoader(new ByteSymmetricKeyLoader(key))
                .configurations(AesCryptoUtils.defaultConfigurations().toBuilder().iv(iv).build())
                .build();

        byte[] p = "x".getBytes(StandardCharsets.UTF_8);
        byte[] e = CryptoUtils.encrypt(CryptoAlgorithmType.AES, p, ctx);
        byte[] d = CryptoUtils.decrypt(CryptoAlgorithmType.AES, e, ctx);
        assertArrayEquals(p, d);
    }
}
