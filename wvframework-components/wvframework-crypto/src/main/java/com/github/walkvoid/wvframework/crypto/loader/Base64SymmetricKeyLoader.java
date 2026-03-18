package com.github.walkvoid.wvframework.crypto.loader;

import java.util.Base64;

public final class Base64SymmetricKeyLoader implements SymmetricKeyLoader {

    private final String base64;

    public Base64SymmetricKeyLoader(String base64) {
        this.base64 = base64;
    }

    @Override
    public byte[] loadSymmetricKeyBytes() {
        return Base64.getDecoder().decode(base64.trim());
    }
}
