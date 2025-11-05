package com.github.walkvoid.wvframework.feign;

import feign.Client;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * @author walkvoid
 * @date 2025/11/5
 * @description:
 * @version: feign自动配置类
 */
@Configuration
@ConditionalOnClass(Feign.class)
@EnableConfigurationProperties(FeignProperties.class)
@Import(FeignClientsConfiguration.class)
public class FeignAutoConfiguration {

    private final FeignProperties properties;

    public FeignAutoConfiguration(FeignProperties properties) {
        this.properties = properties;
    }

    /**
     * 配置 HttpURLConnection 客户端（默认客户端）
     * 支持配置连接超时和读取超时
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(Client.class)
    @ConditionalOnProperty(name = "wv.feign.okhttp.enabled", havingValue = "false", matchIfMissing = true)
    public Client feignDefaultClient() {
        return new FeignHttpUrlConnection(
                (int) properties.getHttp().getConnectTimeout(),
                (int) properties.getHttp().getReadTimeout()
        );
    }

    /**
     * 配置 OkHttpClient（可选客户端）
     * 只有当 wv.feign.okhttp.enabled=true 或 wv.feign.client.type=okhttp 时才会创建
     */
    @Bean
    @ConditionalOnMissingBean(okhttp3.OkHttpClient.class)
    @ConditionalOnProperty(name = "wv.feign.okhttp.enabled", havingValue = "true")
    @ConditionalOnClass(okhttp3.OkHttpClient.class)
    public okhttp3.OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(properties.getHttp().getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(properties.getHttp().getReadTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(properties.getHttp().getWriteTimeout(), TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(properties.getOkhttp().isRetryOnConnectionFailure())
                .connectionPool(new ConnectionPool(
                        properties.getOkhttp().getPool().getMaxIdleConnections(),
                        properties.getOkhttp().getPool().getKeepAliveDuration(),
                        TimeUnit.MINUTES
                ));

        // 添加日志拦截器
        if (properties.getInterceptor().isEnableLogging()) {
            //builder.addInterceptor(new FeignLoggingInterceptor());
        }

        // 添加重试拦截器
        if (properties.getInterceptor().getMaxRetries() > 0) {
            //builder.addInterceptor(new FeignRetryInterceptor(properties.getInterceptor().getMaxRetries()));
        }

        return builder.build();
    }

    /**
     * 配置 Feign OkHttpClient
     * Spring Cloud OpenFeign 会自动检测并使用此 Bean
     */
    @Bean
    @ConditionalOnMissingBean(feign.okhttp.OkHttpClient.class)
    @ConditionalOnProperty(name = "wv.feign.okhttp.enabled", havingValue = "true")
    @ConditionalOnClass(feign.okhttp.OkHttpClient.class)
    public feign.okhttp.OkHttpClient feignOkHttpClient(okhttp3.OkHttpClient okHttpClient) {
        return new feign.okhttp.OkHttpClient(okHttpClient);
    }

    /**
     * 配置 JSON 编码器
     */
    @Bean
    @ConditionalOnMissingBean(Encoder.class)
    public Encoder feignEncoder() {
        HttpMessageConverter<?> jacksonConverter = new MappingJackson2HttpMessageConverter();
        ObjectFactory<HttpMessageConverters> objectFactory = () ->
                new HttpMessageConverters(jacksonConverter);
        return new SpringEncoder(objectFactory);
    }

    /**
     * 配置 JSON 解码器
     */
    @Bean
    @ConditionalOnMissingBean(Decoder.class)
    public Decoder feignDecoder() {
        HttpMessageConverter<?> jacksonConverter = new MappingJackson2HttpMessageConverter();
        ObjectFactory<HttpMessageConverters> objectFactory = () ->
                new HttpMessageConverters(jacksonConverter);
        return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
    }

    /**
     * 配置错误解码器
     */
    @Bean
    @ConditionalOnMissingBean(ErrorDecoder.class)
    public ErrorDecoder errorDecoder() {
        //return new FeignErrorDecoder();
        return null;
    }
}
