package com.github.walkvoid.wvframework.models;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author jiangjunqing
 * @date 2026/4/28
 * @description:
 * @version:
 */
public class BaseVO implements Serializable {


    private LocalDateTime createTime;

    private Long createBy;

    private String createByName;

    private LocalDateTime updateTime;

    private Long updateBy;

    private String updateByName;
}
