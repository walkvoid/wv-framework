package com.github.walkvoid.wvframework.fileservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时清理已过期的 MinIO 文件（依据 file_info.expire_time）。
 */
@Component
@ConditionalOnProperty(prefix = "wv.file", name = "expire-cleanup-enabled", havingValue = "true", matchIfMissing = true)
public class FileExpireCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(FileExpireCleanupScheduler.class);

    private final FileService fileService;

    public FileExpireCleanupScheduler(FileService fileService) {
        this.fileService = fileService;
    }

    /** 每小时清理一批过期文件 */
    @Scheduled(cron = "${wv.file.expire-cleanup-cron:0 15 * * * ?}")
    public void cleanup() {
        try {
            int n = fileService.cleanupExpired(200);
            if (n > 0) {
                log.info("file expire cleanup done count={}", n);
            }
        } catch (Exception e) {
            log.warn("file expire cleanup failed: {}", e.getMessage());
        }
    }
}
