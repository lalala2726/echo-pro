package cn.zhangchuangla.storage.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
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
        String replace = UUID.randomUUID().toString().replace("-", "");
        return replace + "." + Objects.requireNonNull(originalFilename).split("\\.")[1];
    }


}
