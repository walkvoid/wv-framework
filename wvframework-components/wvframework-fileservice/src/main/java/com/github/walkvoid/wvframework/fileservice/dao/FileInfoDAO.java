package com.github.walkvoid.wvframework.fileservice.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.walkvoid.wvframework.models.PageRequest;
import com.github.walkvoid.wvframework.fileservice.entity.FileInfo;
import com.github.walkvoid.wvframework.fileservice.mapper.FileInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文件信息 DAO
 *
 * @author walkvoid
 */
@Repository
public class FileInfoDAO {

    @Autowired
    private FileInfoMapper mapper;

    public int insert(FileInfo entity) {
        return mapper.insert(entity);
    }

    public int updateById(FileInfo entity) {
        return mapper.updateById(entity);
    }

    public int deleteById(Long id) {
        return mapper.deleteById(id);
    }

    public FileInfo selectById(Long id) {
        return mapper.selectById(id);
    }

    public FileInfo selectByFileKey(String fileKey) {
        return mapper.selectOne(new QueryWrapper<FileInfo>()
                .eq("file_key", fileKey));
    }

    public List<FileInfo> selectByBizCode(String bizCode) {
        return mapper.selectList(new QueryWrapper<FileInfo>()
                .eq("biz_code", bizCode)
                .orderByDesc("create_time"));
    }

    public List<FileInfo> selectExpired(LocalDateTime now, int limit) {
        return mapper.selectList(new QueryWrapper<FileInfo>()
                .isNotNull("expire_time")
                .lt("expire_time", now)
                .orderByAsc("expire_time")
                .last("LIMIT " + Math.max(1, limit)));
    }

    public PageDTO<FileInfo> page(PageRequest<Void> pageRequest) {
        Page<FileInfo> mpPage = mapper.selectPage(
                new Page<>(pageRequest.getCurrent(), pageRequest.getSize()),
                new QueryWrapper<FileInfo>().orderByDesc("create_time"));
        PageDTO<FileInfo> result = new PageDTO<>(mpPage.getCurrent(), mpPage.getSize(), mpPage.getTotal());
        result.setRecords(mpPage.getRecords());
        return result;
    }
}
