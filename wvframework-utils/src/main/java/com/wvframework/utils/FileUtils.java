package com.wvframework.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;


/**
 * @author walkvoid
 * @desc file utils
 */
public class FileUtils {


    /**
     * 将文件转化为输入流，支持读取classpath文件夹下的文件
     * 例如对于文件resource/template/index.html =>入参： basePath="classpath:", morePath="template/index.html"，当然你也可以
     * 只传一个参数 basePath="classpath:template/index.html"。
     *
     * classpath：只会到当前项目classpath路径中查找找文件。
     * classpath*：不仅包含当前项目classpath路径，还包括三方jar中的classpath路径
     * @param basePath 基础路径
     * @param morePath 更多路径
     * @return
     */
    public static File read(String basePath, String... morePath) {
        Path path = Paths.get(basePath, morePath);
        if(path.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
            ClassPathResource classPathResource = new ClassPathResource(path.subpath(1, path.getNameCount()).toString(), ClassUtils.getDefaultClassLoader());
            try {
                return classPathResource.getFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return path.toFile();
        }
    }

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
            long len = copy(inputStream, response.getOutputStream());
            response.addHeader("Content-Length", "" + len);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 如果不存在就创建文件夹
     * @param path
     */
    public void createDirectoryIfNotExist(String path){
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }


    /**
     * 输入流拷贝到输出流
     * @param source
     * @param sink
     * @return
     * @throws IOException
     */
    private static long copy(InputStream source, OutputStream sink) throws IOException {
        long nread = 0L;
        byte[] buf = new byte[8192];
        int n;
        while ((n = source.read(buf)) > 0) {
            sink.write(buf, 0, n);
            nread += n;
        }
        return nread;
    }
}
