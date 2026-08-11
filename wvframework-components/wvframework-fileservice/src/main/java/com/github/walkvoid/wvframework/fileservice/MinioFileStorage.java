package com.github.walkvoid.wvframework.fileservice;

import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.time.Duration;

/**
 * FileStorage MinIO配置
 *
 * @author walkvoid
 */
public class MinioFileStorage implements FileStorage {

    private static final Logger log = LoggerFactory.getLogger(MinioFileStorage.class);

    private final MinioClient client;
    private final String bucket;

    public MinioFileStorage(MinioClient client, String bucket) {
        this.client = client;
        this.bucket = bucket;
    }

    @Override
    public String upload(InputStream inputStream, String objectName, String contentType, long size) {
        try {
            client.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .build());
            log.debug("上传文件 成功: {}", objectName);
            return objectName;
        } catch (Exception e) {
            log.error("上传文件 失败: {}", objectName, e);
            throw new RuntimeException("上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public InputStream download(String objectName) {
        try {
            return client.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("下载文件 失败: {}", objectName, e);
            throw new RuntimeException("下载 失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String objectName) {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            log.debug("删除文件 成功: {}", objectName);
        } catch (Exception e) {
            log.error("删除文件 失败: {}", objectName, e);
            throw new RuntimeException("删除失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String getAccessUrl(String objectName, Duration expiry) {
        try {
            return client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .method(Method.GET)
                    .expiry((int) expiry.getSeconds())
                    .build());
        } catch (Exception e) {
            log.error("获取链接 失败: {}", objectName, e);
            throw new RuntimeException("获取链接 失败: " + e.getMessage(), e);
        }
    }
}
