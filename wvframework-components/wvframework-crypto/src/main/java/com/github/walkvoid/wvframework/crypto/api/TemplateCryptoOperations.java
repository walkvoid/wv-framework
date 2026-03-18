package com.github.walkvoid.wvframework.crypto.api;

import com.github.walkvoid.wvframework.crypto.internal.DefaultCryptoOperations;
import com.github.walkvoid.wvframework.crypto.model.CryptoContext;

/**
 * 基于模板上下文与运行时 {@link CryptoContext} 合并后执行（如仅覆盖 IV）。
 */
public final class TemplateCryptoOperations implements CryptoOperations {

    private final CryptoContext template;

    public TemplateCryptoOperations(CryptoContext template) {
        this.template = template;
    }

    public CryptoContext getTemplate() {
        return template;
    }

    @Override
    public byte[] encrypt(byte[] plain, CryptoContext context) {
        return DefaultCryptoOperations.INSTANCE.encrypt(plain, CryptoContext.merge(template, context));
    }

    @Override
    public byte[] decrypt(byte[] cipher, CryptoContext context) {
        return DefaultCryptoOperations.INSTANCE.decrypt(cipher, CryptoContext.merge(template, context));
    }

    @Override
    public byte[] sign(byte[] data, CryptoContext context) {
        return DefaultCryptoOperations.INSTANCE.sign(data, CryptoContext.merge(template, context));
    }

    @Override
    public boolean verify(byte[] data, byte[] signature, CryptoContext context) {
        return DefaultCryptoOperations.INSTANCE.verify(data, signature, CryptoContext.merge(template, context));
    }
}
