package com.github.walkvoid.wvframework.httplog.advisor;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import org.springframework.util.StreamUtils;

/**
 * 请求体可重复读包装
 *
 * <p>继承 HttpServletRequestWrapper，在构造时一次性读取并缓存请求体。
 * 后续 getInputStream() / getReader() 均从缓存读取。
 *
 * @author walkvoid
 */
public class CachedBodyRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] cachedBody;

    public CachedBodyRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.cachedBody = StreamUtils.copyToByteArray(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() {
        return new CachedBodyServletInputStream(this.cachedBody);
    }

    @Override
    public BufferedReader getReader() {
        String encoding = getCharacterEncoding();
        if (encoding == null) {
            encoding = "UTF-8";
        }
        return new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(this.cachedBody), java.nio.charset.Charset.forName(encoding)));
    }

    /**
     * 获取缓存的请求体
     */
    public byte[] getCachedBody() {
        return this.cachedBody;
    }

    /**
     * 获取请求体字符串
     */
    public String getCachedBodyAsString() {
        String encoding = getCharacterEncoding();
        if (encoding == null) {
            encoding = "UTF-8";
        }
        return new String(this.cachedBody, java.nio.charset.Charset.forName(encoding));
    }

    /**
     * 缓存请求体的 ServletInputStream 实现
     */
    private static class CachedBodyServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream inputStream;

        CachedBodyServletInputStream(byte[] cachedBody) {
            this.inputStream = new ByteArrayInputStream(cachedBody);
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() {
            return inputStream.read();
        }
    }
}
