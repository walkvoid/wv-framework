package com.wvframework.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * @author walkvoid
 * @desc file utils
 */
public class FileUtils {

    /**
     * java环境的临时目录
     */
    public final static String JAVA_TEMP_DIR = System.getProperty("java.io.tmpdir");


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
