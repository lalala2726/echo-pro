package cn.zhangchuangla.storage.constant;

import java.util.List;

/**
 * 存储类型常量
 *
 * @author Chuang
 * <p>
 * created on 2025/4/3
 */
public class StorageConstants {

    /**
     * 文件上传路径格式
     */
    public static final String FILE_UPLOAD_PATH_FORMAT = "yyyy/MM/dd";
    /**
     * 图片后缀
     */
    public static List<String> imageSuffix = List.of(
            "jpg",
            "jpeg",
            "png",
            "gif",
            "bmp",
            "webp",
            "heic",
            "heif",
            "jfif",
            "raw"
    );

    /**
     * 文件目录常量
     */
    public interface dirName {

        /**
         * 图片目录
         */
        String IMAGE = "image";

        /**
         * 文件目录
         */
        String FILE = "file";

        /**
         * 回收站文件夹
         */
        String TRASH = "trash";

        /**
         * 原图目录
         */
        String ORIGINAL = "original";

        /**
         * 预览目录
         */
        String PREVIEW = "preview";

        /**
         * 资源目录
         */
        String RESOURCE = "resource";
    }


    /**
     * 存储类型常量
     */
    public interface StorageType {
        /**
         * 本地存储
         */
        String LOCAL = "local";

        /**
         * 阿里云OSS存储
         */
        String ALIYUN_OSS = "aliyun_oss";

        /**
         * 腾讯云COS存储
         */
        String TENCENT_COS = "tencent_cos";

        /**
         * MinIO存储
         */
        String MINIO = "minio";
    }

    /**
     * 数据验证常量
     */
    public interface dataVerifyConstants {
        /**
         * 不是文件上传主配置
         */
        Integer IS_NOT_FILE_UPLOAD_MASTER = 0;

        /**
         * 是文件上传主配置
         */
        Integer IS_FILE_UPLOAD_MASTER = 1;

        /**
         * 文件已删除
         */
        Integer FILE_DELETED = 1;

        /**
         * 在回收站
         */
        Integer IN_TRASH = 1;

        /**
         * 不在回收站
         */
        Integer NOT_IN_TRASH = 0;
    }


    /**
     * Spring Bean名称
     */
    public interface springBeanName {
        /**
         * 本地存储服务名称
         */
        String LOCAL_STORAGE_SERVICE = "localStorageService";

        /**
         * 阿里云OSS存储服务名称
         */
        String ALIYUN_OSS_STORAGE_SERVICE = "aliyunOssStorageService";

        /**
         * MinIO存储服务名称
         */
        String MINIO_STORAGE_SERVICE = "minioStorageService";

        /**
         * 腾讯云COS存储服务名称
         */
        String TENCENT_COS_STORAGE_SERVICE = "tencentCosStorageService";
    }


}
