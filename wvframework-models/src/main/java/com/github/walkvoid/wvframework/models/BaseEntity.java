package com.github.walkvoid.wvframework.models;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author jiangjunqing
 * @date 2026/4/28
 * @description:
 * @version:
 */
public class BaseEntity implements Serializable {

    private int enable;

    private LocalDateTime createTime;

    private String createBy;

    private LocalDateTime updateTime;

    private String updateBy;

}
