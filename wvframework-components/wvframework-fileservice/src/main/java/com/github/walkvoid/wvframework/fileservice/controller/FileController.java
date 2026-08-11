package com.github.walkvoid.wvframework.fileservice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.github.walkvoid.wvframework.models.ApiResult;
import com.github.walkvoid.wvframework.models.PageRequest;
import com.github.walkvoid.wvframework.fileservice.FileService;
import com.github.walkvoid.wvframework.fileservice.config.FileServiceProperties;
import com.github.walkvoid.wvframework.fileservice.entity.FileInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 文件管理 Controller
 *
 * @author walkvoid
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileServiceProperties properties;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public ApiResult<FileInfo> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "bizCode", required = false) String bizCode) {
        FileInfo info = fileService.upload(file, bizCode);
        return ApiResult.ok(info, "上传成功");
    }

    @Operation(summary = "下载文件")
    @GetMapping("/download/{id}")
    public void download(@PathVariable Long id, HttpServletResponse response) {
        FileInfo info = fileService.getById(id);
        if (info == null) {
            response.setStatus(404);
            return;
        }
        try (InputStream is = fileService.download(id);
             OutputStream os = response.getOutputStream()) {
            response.setContentType(info.getContentType() != null ? info.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE);
            String filename = URLEncoder.encode(info.getOriginalName(), StandardCharsets.UTF_8).replace("+", "%20");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build().toString());
            byte[] buffer = new byte[8192];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        } catch (IOException e) {
            response.setStatus(500);
        }
    }

    @Operation(summary = "分页查询")
    @GetMapping("/page")
    public ApiResult<PageDTO<FileInfo>> page(PageRequest<Void> pageRequest) {
        PageDTO<FileInfo> result = fileService.page(pageRequest);
        return ApiResult.ok(result);
    }

    @Operation(summary = "获取详情")
    @GetMapping("/{id}")
    public ApiResult<FileInfo> getById(@PathVariable Long id) {
        FileInfo info = fileService.getById(id);
        if (info == null) {
            return ApiResult.error(404, "文件不存在");
        }
        return ApiResult.ok(info);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{id}")
    public ApiResult<String> delete(@PathVariable Long id) {
        fileService.delete(id);
        return ApiResult.ok("OK", "删除成功");
    }

    @Operation(summary = "获取临时访问链接")
    @GetMapping("/access-url/{id}")
    public ApiResult<String> getAccessUrl(@PathVariable Long id,
                                          @RequestParam(defaultValue = "3600") long expirySeconds) {
        String url = fileService.getAccessUrl(id, Duration.ofSeconds(expirySeconds));
        return ApiResult.ok(url);
    }
}
