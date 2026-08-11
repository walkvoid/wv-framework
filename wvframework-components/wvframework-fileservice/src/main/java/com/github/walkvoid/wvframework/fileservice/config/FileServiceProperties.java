package com.github.walkvoid.wvframework.fileservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文件服务配置属性
 *
 * @author walkvoid
 */
@ConfigurationProperties(prefix = "wv.file")
public class FileServiceProperties {

    /** MinIO endpoint */
    private String endpoint = "http://localhost:9000";

    /** MinIO access key */
    private String accessKey;

    /** MinIO secret key */
    private String secretKey;

    /** MinIO bucket */
    private String bucket = "zone";

    /** MinIO region (optional) */
    private String region;

    /** 最大文件大小 (bytes), default 100MB */
    private long maxFileSize = 100 * 1024 * 1024;

    /** 临时链接默认有效时间 (seconds) */
    private long accessUrlExpirySeconds = 3600;

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getAccessKey() { return accessKey; }
    public void setAccessKey(String accessKey) { this.accessKey = accessKey; }

    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }

    public String getBucket() { return bucket; }
    public void setBucket(String bucket) { this.bucket = bucket; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public long getMaxFileSize() { return maxFileSize; }
    public void setMaxFileSize(long maxFileSize) { this.maxFileSize = maxFileSize; }

    public long getAccessUrlExpirySeconds() { return accessUrlExpirySeconds; }
    public void setAccessUrlExpirySeconds(long accessUrlExpirySeconds) { this.accessUrlExpirySeconds = accessUrlExpirySeconds; }
}
