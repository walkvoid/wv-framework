package com.wvframework.utils;

import jdk.nashorn.internal.runtime.URIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * @author walkvoid
 * @desc file utils
 */
public class FileUtils {

    private static Logger log = LoggerFactory.getLogger(FileUtils.class);

    /**
     * java环境的临时目录
     */
    public final static String JAVA_TEMP_DIR = System.getProperty("java.io.tmpdir");

    /**
     * classpath的目录
     */
    public static String CLASSPATH;

    static {
        URL resource = FileUtils.class.getClassLoader().getResource("");
        if (resource == null) {
            log.warn("ClassLoader resource is null");
        } else {
            try {
                CLASSPATH = Paths.get(resource.toURI()).toString();
            } catch (URISyntaxException e) {
                log.error("resource(URL) to URI fail,resource:{}",resource, e);
            }
        }
    }


    /**
     * 根据传入的路径获取文件
     * @param basePath
     * @param morePath
     * @return
     */
    public static File get(String basePath, String... morePath) {
        Path path = Paths.get(basePath, morePath);
        return path.toFile();
    }

    /**
     *
     * @param antStylePath
     * @return
     */
    public static List<File> getMatched(String antStylePath){
        return getMatched(antStylePath,null);
    }

    /**
     * 传入一个可匹配的路径，获取符合条件的文件集合，例如: classpath:mappers/*Mapper.xml
     * @param antStylePath 一个模糊的路径，可以匹配多个具体的路径
     * @param filter 文件过滤器
     * @return
     */
    public static List<File> getMatched(String antStylePath, Predicate<File> filter){
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resourcePatternResolver.getResources(antStylePath);
            return Arrays.stream(resources).filter(Resource::exists).filter(Resource::isFile).map(r -> {
                try {
                    return r.getFile();
                } catch (IOException e) {
                    log.warn("resource getFile IOException,resource:{}",r);
                }
                return null;
            }).filter(file -> file != null && (filter == null || filter.test(file))).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("getMatched IOException,path:{}",antStylePath);
        }
        return Collections.emptyList();
    }

    /**
     * 解压文件集合，将结果写入到dest路径中
     * @param srcFiles
     * @param dest
     * @return
     */
    public static File zipFiles(List<File> srcFiles, Path dest){
        return null;

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
     * 保存字节内容到文件中
     * @param content 需要保存在文件的内容的字节数组
     * @param dest 写入文件的路径，包含了文件目录和文件名，一般采用Paths.of()
     */
    public static void writeToFile(byte[] content, Path dest) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(dest.toUri()));
            fileOutputStream.write(content);
        } catch (IOException e) {
            throw new RuntimeException("写入字节数组到文件失败。",e);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 保存字符串内容到文件中
     * @param content 需要保存在文件的内容的字符串
     * @param dest 写入文件的路径，包含了文件目录和文件名，一般采用Paths.of(fileParentDict，fileName的方式构造)
     */
    public static void writeToFile(String content, Path dest) {
        if (StringUtils.isNotEmpty(content)) {
            writeToFile(content.getBytes(StandardCharsets.UTF_8), dest);
        } else {
            //
        }
    }

    /**
     * 从文件中读取字符串内容并返回
     * @param dest 读取文件的路径，包含了文件目录和文件名，一般采用Paths.of(fileParentDict，fileName的方式构造)
     */
    public static String readFromFile(Path dest) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(new File(dest.toUri()));
            StringBuilder sb = new StringBuilder();
            int i;
            while ((i=fileReader.read())>0) {
                sb.append((char)i);
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException("读取文件的字符串内容失败.",e);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 保存字符串内容到文件中
     * @param dest 读取文件的路径，包含了文件目录和文件名，一般采用Paths.of(fileParentDict，fileName的方式构造)
     */
    public static byte[] readBytesFromFile(Path dest) {
        FileInputStream fileInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(dest.toUri()));
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int i;
            while ((i = fileInputStream.read(buf)) >0) {
                byteArrayOutputStream.write(buf, 0, i);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("读取文件的字节数组失败，文件:"+dest+",e:" + e);
        } finally {
            if (fileInputStream !=null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (byteArrayOutputStream !=null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }



    }


    /**
     * 输入流拷贝到输出流
     * @param source
     * @param sink
     * @return
     * @throws IOException
     */
    public static long copy(InputStream source, OutputStream sink) throws IOException {
        long nread = 0L;
        byte[] buf = new byte[8192];
        int n;
        while ((n = source.read(buf)) > 0) {
            sink.write(buf, 0, n);
            nread += n;
        }
        return nread;
    }

    public static void main(String[] args) throws Exception {
        ArrayList<String> strings = CollectionUtils.newArrayList("D:\\lls\\jiangjunqing\\Desktop\\whatisai.pdf",
                "D:\\lls\\jiangjunqing\\Desktop\\fapiao1.jpg", "D:\\lls\\jiangjunqing\\Desktop\\fapiao2.jpg");
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(new File("D:\\lls\\jiangjunqing\\Desktop\\xxxx.zip")));
        for (String filePath : strings) {
            File file = new File(filePath);
            zos.putNextEntry(new ZipEntry(file.getName()));
            zos.closeEntry();
        }
        zos.close();
    }
}
