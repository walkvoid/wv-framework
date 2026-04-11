package com.github.walkvoid.wvframework.crypto.loader;

import com.github.walkvoid.wvframework.crypto.CryptoAlgorithmType;
import com.github.walkvoid.wvframework.crypto.internal.PemKeySupport;

import java.security.PublicKey;

public final class PemPublicKeyLoader implements PublicKeyLoader {

    private final String pem;
    private final CryptoAlgorithmType type;

    public PemPublicKeyLoader(String pem, CryptoAlgorithmType type) {
        this.pem = pem;
        this.type = type;
    }

    @Override
    public PublicKey loadPublicKey() {
        return PemKeySupport.parsePublicKey(pem, type);

        
    }
}
