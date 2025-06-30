package cn.zhangchuangla.storage.utils;

import lombok.extern.slf4j.Slf4j;

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

}
