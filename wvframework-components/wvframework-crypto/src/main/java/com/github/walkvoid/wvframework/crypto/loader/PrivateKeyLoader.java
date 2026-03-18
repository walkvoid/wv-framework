package com.github.walkvoid.wvframework.crypto.loader;

import java.security.PrivateKey;

@FunctionalInterface
public interface PrivateKeyLoader {

    PrivateKey loadPrivateKey() throws Exception;
}
