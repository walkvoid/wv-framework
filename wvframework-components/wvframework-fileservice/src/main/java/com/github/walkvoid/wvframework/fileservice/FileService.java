package com.github.walkvoid.wvframework.fileservice;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.walkvoid.wvframework.models.PageRequest;
import com.github.walkvoid.wvframework.fileservice.dao.FileInfoDAO;
import com.github.walkvoid.wvframework.fileservice.entity.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件服务
 *
 * @author walkvoid
 */
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    @Autowired
    private FileStorage fileStorage;

    @Autowired
    private FileInfoDAO fileInfoDAO;

    /**
     * 上传文件并记录元数据
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

            // 保存元数据
            FileInfo info = new FileInfo();
            info.setFileKey(uuid);
            info.setOriginalName(originalName);
            info.setObjectName(objectName);
            info.setBizCode(bizCode);
            info.setContentType(contentType);
            info.setFileSize(size);
            info.setFileExt(ext);
            info.setCreateTime(LocalDateTime.now());
            info.setUpdateTime(LocalDateTime.now());
            fileInfoDAO.insert(info);

            log.info("上传成功: {} -> {}", originalName, objectName);
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
     * 获取详情
     */
    public FileInfo getById(Long id) {
        return fileInfoDAO.selectById(id);
    }

    /**
     * 获取临时访问链接
     */
    public String getAccessUrl(Long id, Duration expiry) {
        FileInfo info = getFileInfoOrThrow(id);
        String url = fileStorage.getAccessUrl(info.getObjectName(), expiry);
        // 更新链接
        info.setAccessUrl(url);
        info.setUpdateTime(LocalDateTime.now());
        fileInfoDAO.insert(info); // update by primary key
        return url;
    }

    private FileInfo getFileInfoOrThrow(Long id) {
        FileInfo info = fileInfoDAO.selectById(id);
        if (info == null) {
            throw new IllegalArgumentException("文件不存在: " + id);
        }
        return info;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
