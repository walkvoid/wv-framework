package com.github.walkvoid.wvframework.fileservice;

import java.io.InputStream;
import java.time.Duration;

/**
 * 文件存储抽象，屏蔽具体存储后端差异
 *
 * @author walkvoid
 */
public interface FileStorage {

    /**
     * 上传文件到存储后端
     *
     * @param inputStream 文件输入流
     * @param objectName  存储对象名称（包含路径）
     * @param contentType MIME类型
     * @param size        文件大小（字节）
     * @return 存储后的对象名称
     */
    String upload(InputStream inputStream, String objectName, String contentType, long size);

    /**
     * 下载文件
     *
     * @param objectName 存储对象名称（包含路径）
     * @return 文件输入流，调用方需要close
     */
    InputStream download(String objectName);

    /**
     * 删除文件
     *
     * @param objectName 存储对象名称（包含路径）
     */
    void delete(String objectName);

    /**
     * 获取临时访问URL
     *
     * @param objectName 存储对象名称（包含路径）
     * @param expiry     有效时长
     * @return 临时访问URL
     */
    String getAccessUrl(String objectName, Duration expiry);
}
