package com.github.walkvoid.wvframework.crypto.loader;

import com.github.walkvoid.wvframework.crypto.CryptoAlgorithmType;
import com.github.walkvoid.wvframework.crypto.internal.PemKeySupport;

import java.security.PrivateKey;

public final class PemPrivateKeyLoader implements PrivateKeyLoader {

    private final String pem;
    private final char[] password;
    private final CryptoAlgorithmType type;

    public PemPrivateKeyLoader(String pem, CryptoAlgorithmType type) {
        this(pem, null, type);
    }

    public PemPrivateKeyLoader(String pem, char[] password, CryptoAlgorithmType type) {
        this.pem = pem;
        this.password = password;
        this.type = type;
    }

    @Override
    public PrivateKey loadPrivateKey() {
        return PemKeySupport.parsePrivateKey(pem, password, type);
    }
}
