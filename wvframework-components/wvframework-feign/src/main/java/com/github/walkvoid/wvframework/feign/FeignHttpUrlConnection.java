package com.github.walkvoid.wvframework.feign;

import feign.Client;
import feign.Request;
import feign.Response;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Feign HttpURLConnection 客户端
 * 支持配置连接超时和读取超时
 * @author jiangjunqing
 * @date 2025/11/5
 * @description: 自定义 Feign Client，使用 HttpURLConnection 并支持超时配置
 * @version: 1.0
 */
public class FeignHttpUrlConnection extends Client.Default {

    private final int connectTimeout;
    private final int readTimeout;

    public FeignHttpUrlConnection(int connectTimeout, int readTimeout) {
        this(connectTimeout, readTimeout, null, null);
    }

    public FeignHttpUrlConnection(int connectTimeout, int readTimeout,
                                  SSLSocketFactory sslContextFactory,
                                  HostnameVerifier hostnameVerifier) {
        super(sslContextFactory, hostnameVerifier);
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    @Override
    public Response execute(Request input, Request.Options options) throws IOException {
        // 使用配置的超时时间，而不是 options 中的超时时间
        Request.Options customOptions = new Request.Options(
                connectTimeout,
                TimeUnit.MILLISECONDS,
                readTimeout,
                TimeUnit.MILLISECONDS,
                options.isFollowRedirects()
        );
        return super.execute(input, customOptions);
    }
}

