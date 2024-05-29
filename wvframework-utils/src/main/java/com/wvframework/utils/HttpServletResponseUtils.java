package com.wvframework.utils;

import org.springframework.http.MediaType;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author jiangjunqing
 * @date 2024/5/29
 * @description:
 * @version:
 */
public class HttpServletResponseUtils {

    /**
     * 将文件写入到HttpServletResponse，默认取文件名
     * @param file
     * @param response
     */
    public static void writeToHttpServletResponse(File file, HttpServletResponse response) {
        writeToHttpServletResponse(file,file.getName(),response);
    }

    /**
     * 将文件写入到HttpServletResponse，并支持使用newFileName进行重命名
     * @param file
     * @param newFileName
     * @param response
     */
    public static void writeToHttpServletResponse(File file, String newFileName, HttpServletResponse response) {
        Assert.notNull(file,"file must not be null");
        try {
            writeToHttpServletResponse(new FileInputStream(file), newFileName, response);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将文件输入流写到HttpServletResponse中
     * @param inputStream
     * @param fileName
     * @param response
     */
    public static void writeToHttpServletResponse(InputStream inputStream, String fileName, HttpServletResponse response) {
        Assert.hasText(fileName, "fileName must have length; it must not be null or empty");
        Assert.notNull(response,"response must not be null");

        String encodeFileName = "";
        try {
            encodeFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ignored) {

        }
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodeFileName + "\"");
        try {
            long len = FileUtils.copy(inputStream, response.getOutputStream());
            response.addHeader("Content-Length", "" + len);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
