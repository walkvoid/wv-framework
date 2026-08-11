-- 文件信息表
CREATE TABLE IF NOT EXISTS `file_info` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `file_key`      VARCHAR(64)  NOT NULL                COMMENT '文件Key',
    `original_name` VARCHAR(256) NOT NULL                COMMENT '原始文件名',
    `object_name`   VARCHAR(512) NOT NULL                COMMENT '存储对象名称',
    `biz_code`      VARCHAR(64)  NOT NULL DEFAULT 'common' COMMENT '业务编码',
    `content_type`  VARCHAR(128) DEFAULT NULL            COMMENT '文件类型',
    `file_size`     BIGINT       DEFAULT 0               COMMENT '文件大小 (字节)',
    `file_ext`      VARCHAR(32)  DEFAULT NULL            COMMENT '文件扩展名',
    `access_url`    VARCHAR(1024) DEFAULT NULL           COMMENT '访问链接',
    `create_time`   DATETIME     NOT NULL                COMMENT '创建时间',
    `update_time`   DATETIME     DEFAULT NULL            COMMENT '更新时间',
    `deleted`       TINYINT      NOT NULL DEFAULT 0      COMMENT '逻辑删除: 0=未删, 1=已删',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_file_key` (`file_key`),
    KEY `idx_biz_code` (`biz_code`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件信息表';
