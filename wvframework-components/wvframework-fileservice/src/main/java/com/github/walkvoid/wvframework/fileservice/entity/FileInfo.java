package com.github.walkvoid.wvframework.fileservice.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件信息表
 *
 * @author walkvoid
 */
@Data
@TableName("file_info")
public class FileInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    /** 文件Key */
    private String fileKey;

    /** 原始文件名 */
    private String originalName;

    /** 存储对象名称 */
    private String objectName;

    /** 业务编码 */
    private String bizCode;

    /** 文件类型 */
    private String contentType;

    /** 文件大小 */
    private Long fileSize;

    /** 文件扩展名 */
    private String fileExt;

    /** 访问链接 */
    private String accessUrl;

    /** 过期时间；到期后不可下载，由清理任务删除 MinIO 对象 */
    private LocalDateTime expireTime;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除: 0=未删, 1=已删 */
    @TableLogic
    private Integer deleted;
}
