package com.github.walkvoid.wvframework.fileservice.config;

import com.github.walkvoid.wvframework.fileservice.FileService;
import com.github.walkvoid.wvframework.fileservice.FileStorage;
import com.github.walkvoid.wvframework.fileservice.MinioFileStorage;
import io.minio.MinioClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * 文件服务自动配置
 *
 * @author walkvoid
 */
@AutoConfiguration
@EnableConfigurationProperties(FileServiceProperties.class)
@ConditionalOnProperty(prefix = "wv.file", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan({"com.github.walkvoid.wvframework.fileservice"})
@MapperScan("com.github.walkvoid.wvframework.fileservice.mapper")
public class FileServiceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MinioClient minioClient(FileServiceProperties properties) {
        MinioClient.Builder builder = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey());
        if (properties.getRegion() != null && !properties.getRegion().isBlank()) {
            builder.region(properties.getRegion());
        }
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public FileStorage fileStorage(MinioClient minioClient, FileServiceProperties properties) {
        return new MinioFileStorage(minioClient, properties.getBucket());
    }

    @Bean
    @ConditionalOnMissingBean
    public FileService fileService() {
        return new FileService();
    }
}
