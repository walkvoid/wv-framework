package com.github.walkvoid.wvframework.crypto.loader;

import java.security.PublicKey;

@FunctionalInterface
public interface PublicKeyLoader {

    PublicKey loadPublicKey() throws Exception;
}
