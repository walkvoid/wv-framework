package com.github.walkvoid.wvframework.fileservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.walkvoid.wvframework.fileservice.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件信息 Mapper
 *
 * @author walkvoid
 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {
}
