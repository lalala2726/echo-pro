package cn.zhangchuangla.storage.utils;

import cn.zhangchuangla.storage.constant.StorageConstants;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 存储服务通用工具类
 * 封装存储服务共用的方法，如判断文件类型、生成路径等
 *
 * @author Chuang
 * <p>
 * created on 2025/4/9 18:01
 */
@Slf4j
public class StorageUtils {


    /**
     * 生成文件名
     *
     * @param originalFilename 文件名
     * @return 文件名
     */
    public static String generateFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        if (extension.isEmpty()) {
            return uuid;
        }
        return uuid + "." + extension;
    }


    /**
     * 获取文件后缀名
     *
     * @param originalFilename 文件名
     * @return 文件后缀名, 如果没有则返回空字符串
     */
    public static String getFileExtension(String originalFilename) {
        if (originalFilename == null) {
            return "";
        }
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == originalFilename.length() - 1) {
            return "";
        }
        return originalFilename.substring(dotIndex + 1);
    }

    /**
     * 格式化文件大小
     *
     * @param size 文件大小（字节）
     * @return 格式化后的文件大小
     */
    public static String formatFileSize(long size) {
        if (size < 0) {
            return "0B";
        }

        if (size < 1024) {
            return size + "B";
        }

        if (size < 1024 * 1024) {
            double kbSize = size / 1024.0;
            return String.format("%.2fKB", kbSize);
        }

        double mbSize = size / (1024.0 * 1024.0);
        return String.format("%.2fMB", mbSize);
    }

    /**
     * 日期目录可以在这边统一修改{@link StorageConstants}
     */
    public static String createDateDir() {
        return new SimpleDateFormat(StorageConstants.FILE_UPLOAD_PATH_FORMAT).format(new Date());
    }

    /**
     * 从路径中移除resource前缀
     *
     * @param path 原始路径
     * @return 移除resource前缀后的路径
     */
    public static String removeResourcePrefix(String path) {
        if (path == null || path.isBlank()) {
            return path;
        }

        String resourcePrefix = StorageConstants.dirName.RESOURCE + "/";
        if (path.startsWith(resourcePrefix)) {
            return path.substring(resourcePrefix.length());
        }
        return path;
    }


}
