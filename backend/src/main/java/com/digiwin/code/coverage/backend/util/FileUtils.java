package com.digiwin.code.coverage.backend.util;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @Package: com.netease.precisiontesting.common.utils.file
 * @Description: java类作用描述
 * @Author: rayduan
 * @CreateDate: 2023/4/19 13:57
 * @Version: 1.0
 * <p>
 */
public class FileUtils {
    /**
     * 搜索文件是否存在对应后缀的文件
     *
     * @param directory 目录
     * @param suffix    后缀
     * @return boolean
     */
    public static boolean searchFile(File directory,String suffix) {
        if (directory == null || !directory.isDirectory()) {
            return false;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return false;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                if (searchFile(file,suffix)) {
                    return true;
                }
            } else if (file.getName().endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }




    public static void downloadFile(String fileURL, String saveDir) throws IOException {
        URL url = new URL(fileURL);
        try (InputStream in = url.openStream()) {
            Files.copy(in, Paths.get(saveDir).resolve("temp" + ".zip"), StandardCopyOption.REPLACE_EXISTING);
        }


    }


    /**
     * @date:2021/4/5
     * @className:VersionControl
     * @author:Administrator
     * @description: 获取类本地地址
     */
    public static String getClassFilePath(String baseDir, String classPath) {
        StringBuilder builder = new StringBuilder(baseDir);
        builder.append("/");
        builder.append(classPath);
        return builder.toString();
    }

    /**
     * 解压ZIP文件。
     *
     * @param zipFilePath ZIP文件路径
     * @param destDirectory 解压目标目录
     * @throws IOException 如果解压过程出错
     */
    public static void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir(); // 创建目录
        }
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipIn.getNextEntry();
            // 遍历ZIP文件条目
            while (entry != null) {
                String filePath = destDirectory + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    // 如果是文件，解压
                    extractFile(zipIn, filePath);
                } else {
                    // 如果是目录，创建目录
                    File dir = new File(filePath);
                    dir.mkdir();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }


    /**
     * 解压文件。
     *
     * @param zipIn ZipInputStream
     * @param filePath 解压文件路径
     * @throws IOException 如果解压过程出错
     */
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[4096];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }

    public static LocalDateTime checkFileCreationTime(String filePath) {
        Path path = Paths.get(filePath);
        try {
            // 读取文件属性
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);

            // 获取文件的创建时间
            FileTime creationTime = attrs.creationTime();

            // 将FileTime转换为LocalDateTime
            LocalDateTime creationDateTime = LocalDateTime.ofInstant(creationTime.toInstant(), ZoneId.systemDefault());

            // 获取当前时间
            LocalDateTime now = LocalDateTime.now();

            // 检查文件是否在过去3小时内创建
            if (ChronoUnit.HOURS.between(creationDateTime, now) < 3) {
                // 文件创建于3小时之内
                return creationDateTime;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 文件创建时间超过3小时或发生异常
        return null;
    }

}
