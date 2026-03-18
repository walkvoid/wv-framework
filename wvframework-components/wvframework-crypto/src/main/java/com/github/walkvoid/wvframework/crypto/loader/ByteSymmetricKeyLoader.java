package com.github.walkvoid.wvframework.crypto.loader;

import java.util.Arrays;

public final class ByteSymmetricKeyLoader implements SymmetricKeyLoader {

    private final byte[] key;

    public ByteSymmetricKeyLoader(byte[] key) {
        this.key = Arrays.copyOf(key, key.length);
    }

    @Override
    public byte[] loadSymmetricKeyBytes() {
        return Arrays.copyOf(key, key.length);
    }
}
