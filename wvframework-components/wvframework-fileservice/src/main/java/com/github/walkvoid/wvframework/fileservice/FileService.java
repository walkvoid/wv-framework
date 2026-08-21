package com.github.walkvoid.wvframework.fileservice;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.walkvoid.wvframework.models.PageRequest;
import com.github.walkvoid.wvframework.fileservice.config.FileServiceProperties;
import com.github.walkvoid.wvframework.fileservice.dao.FileInfoDAO;
import com.github.walkvoid.wvframework.fileservice.entity.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 文件服务
 *
 * @author walkvoid
 */
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    private static final Set<String> TEXT_EXTS = Set.of(
            "md", "markdown", "txt", "text", "json", "csv", "log", "xml", "yml", "yaml", "html", "htm");

    @Autowired
    private FileStorage fileStorage;

    @Autowired
    private FileInfoDAO fileInfoDAO;

    @Autowired
    private FileServiceProperties properties;

    /**
     * 上传文件并记录元数据（默认 expire_time = now + expireDays）
     */
    public FileInfo upload(MultipartFile file, String bizCode) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        if (bizCode == null || bizCode.isBlank()) {
            bizCode = "common";
        }

        try {
            String originalName = file.getOriginalFilename();
            String ext = getExtension(originalName);
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String objectName = bizCode + "/" + datePath + "/" + uuid + (ext.isEmpty() ? "" : "." + ext);

            String contentType = file.getContentType();
            long size = file.getSize();

            // 上传至存储后端
            fileStorage.upload(file.getInputStream(), objectName, contentType, size);

            LocalDateTime now = LocalDateTime.now();
            int expireDays = Math.max(1, properties.getExpireDays());

            // 保存元数据
            FileInfo info = new FileInfo();
            info.setFileKey(uuid);
            info.setOriginalName(originalName);
            info.setObjectName(objectName);
            info.setBizCode(bizCode);
            info.setContentType(contentType);
            info.setFileSize(size);
            info.setFileExt(ext);
            info.setCreateTime(now);
            info.setUpdateTime(now);
            info.setExpireTime(now.plusDays(expireDays));
            fileInfoDAO.insert(info);

            log.info("上传成功: {} -> {}, expireTime={}", originalName, objectName, info.getExpireTime());
            return info;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("上传失败", e);
            throw new RuntimeException("上传失败", e);
        }
    }

    /**
     * 下载文件
     */
    public InputStream download(Long id) {
        FileInfo info = getFileInfoOrThrow(id);
        return fileStorage.download(info.getObjectName());
    }

    /**
     * 删除文件 (删储储且逻辑删除)
     */
    public void delete(Long id) {
        FileInfo info = getFileInfoOrThrow(id);
        try {
            fileStorage.delete(info.getObjectName());
        } catch (Exception e) {
            log.warn("删除存储失败，已逻辑删除元数据: {}", info.getObjectName(), e);
        }
        fileInfoDAO.deleteById(id);
        log.info("删除成功: {}", info.getObjectName());
    }

    /**
     * 分页查询
     */
    public PageDTO<FileInfo> page(PageRequest<Void> pageRequest) {
        return fileInfoDAO.page(pageRequest);
    }

    /**
     * 获取详情（不过滤过期，供管理查看）
     */
    public FileInfo getById(Long id) {
        return fileInfoDAO.selectById(id);
    }

    /**
     * 清理已过期文件（删 MinIO + 逻辑删元数据），返回清理条数。
     */
    public int cleanupExpired(int limit) {
        int batch = Math.max(1, Math.min(limit, 500));
        List<FileInfo> expired = fileInfoDAO.selectExpired(LocalDateTime.now(), batch);
        int deleted = 0;
        for (FileInfo info : expired) {
            try {
                delete(info.getId());
                deleted++;
            } catch (Exception e) {
                log.warn("清理过期文件失败 id={}: {}", info.getId(), e.getMessage());
            }
        }
        if (deleted > 0) {
            log.info("清理过期文件 {} 个", deleted);
        }
        return deleted;
    }

    /**
     * 获取临时访问链接
     */
    public String getAccessUrl(Long id, Duration expiry) {
        FileInfo info = getFileInfoOrThrow(id);
        String url = fileStorage.getAccessUrl(info.getObjectName(), expiry);
        info.setAccessUrl(url);
        info.setUpdateTime(LocalDateTime.now());
        fileInfoDAO.updateById(info);
        return url;
    }

    /**
     * 按文本方式读取文件内容（供 Prompt 注入等场景）。
     *
     * @param maxChars 最大字符数，超出截断并追加提示
     */
    public String readAsText(Long id, int maxChars) {
        FileInfo info = getFileInfoOrThrow(id);
        String ext = info.getFileExt() == null ? "" : info.getFileExt().toLowerCase(Locale.ROOT);
        String contentType = info.getContentType() == null ? "" : info.getContentType().toLowerCase(Locale.ROOT);
        boolean textLike = TEXT_EXTS.contains(ext) || contentType.startsWith("text/")
                || contentType.contains("json") || contentType.contains("xml") || contentType.contains("yaml");
        if (!textLike) {
            throw new IllegalArgumentException("暂仅支持文本类文档注入（md/txt/json/csv 等），当前: "
                    + info.getOriginalName());
        }
        int limit = Math.max(1_000, maxChars);
        try (InputStream in = fileStorage.download(info.getObjectName())) {
            byte[] bytes = in.readAllBytes();
            String text = new String(bytes, StandardCharsets.UTF_8);
            if (text.length() <= limit) {
                return text;
            }
            return text.substring(0, limit) + "\n\n...[文档已截断，共 " + text.length() + " 字符，仅保留前 " + limit + " 字符]";
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("读取文件失败: " + info.getOriginalName(), e);
        }
    }

    private FileInfo getFileInfoOrThrow(Long id) {
        FileInfo info = fileInfoDAO.selectById(id);
        if (info == null) {
            throw new IllegalArgumentException("文件不存在: " + id);
        }
        assertNotExpired(info);
        return info;
    }

    private static void assertNotExpired(FileInfo info) {
        if (info.getExpireTime() != null && info.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("文件已过期: " + info.getOriginalName()
                    + "（expireTime=" + info.getExpireTime() + "）");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
