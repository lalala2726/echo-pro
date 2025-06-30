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
     * 本地存储
     */
    public static final String LOCAL = "local";

    /**
     * 阿里云OSS存储
     */
    public static final String ALIYUN_OSS = "aliyun_oss";

    /**
     * 腾讯云COS存储
     */
    public static final String TENCENT_COS = "tencent_cos";

    /**
     * MinIO存储
     */
    public static final String MINIO = "minio";

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
     * 文件URL
     */
    public static final String FILE_URL = "fileUrl";

    /**
     * 完整URL
     */
    public static final String COMPRESSED_URL = "compressedUrl";

    /**
     * 文件相对路径
     */
    public static final String RELATIVE_FILE_LOCATION = "relativeFileLocation";

    /**
     * 缩略图文件夹
     */
    public static final String FILE_PREVIEW_FOLDER = "preview";

    /**
     * 原始文件夹
     */
    public static final String FILE_ORIGINAL_FOLDER = "original";

    /**
     * 当前默认上传类型
     */
    public static final String CURRENT_DEFAULT_UPLOAD_TYPE = "currentDefaultUploadType";

    /**
     * 存储配置表主键
     */
    public static final String STORAGE_KEY = "storageKey";

    /**
     * 存储类型
     */
    public static final String STORAGE_TYPE = "storageType";

    /**
     * 存储名称
     */
    public static final String STORAGE_NAME = "storageName";

    /**
     * 不是文件上传主配置
     */
    public static final Integer IS_NOT_FILE_UPLOAD_MASTER = 0;

    /**
     * 是文件上传主配置
     */
    public static final Integer IS_FILE_UPLOAD_MASTER = 1;

    /**
     * 存储值(JSON格式)
     */
    public static final String STORAGE_VALUE = "storageValue";

    /**
     * 系统默认文件配置ID
     */
    public static final Long SYSTEM_DEFAULT_FILE_CONFIG_ID = 1L;
    public static final String STORAGE_DIR_FILE = "file";
    public static final String STORAGE_DIR_IMAGES = "images";
    public static final String TRASH_DIR = "trash";
    //标记为已存放在回收站
    public static final Integer IS_TRASH = 1;
    public static final Integer IS_NOT_TRASH = 0;
    public static final Integer ENABLE_TRASH = 1;
    public static final Integer DISABLE_TRASH = 0;

    /**
     * 文件已删除状态
     */
    public static final Integer IS_DELETED = 1;

    /**
     * 文件未删除状态
     */
    public static final Integer IS_NOT_DELETED = 0;

    /**
     * 本地存储类型
     */
    public static final String STORAGE_TYPE_LOCAL = "LOCAL";

    /**
     * 回收站目录名称
     */
    public static final String TRASH_DIR_NAME = "trash";

    /**
     * 新文件名 (通常是存储系统中的唯一文件名)
     */
    public static final String FILE_NAME = "fileName";

    /**
     * 原始文件名 (用户上传时的文件名)
     */
    public static final String ORIGINAL_FILE_NAME = "originalFileName";

    /**
     * 文件在存储系统中的相对路径
     */
    public static final String RELATIVE_PATH = "relativePath";

    /**
     * 缩略图文件在存储系统中的相对路径
     */
    public static final String THUMBNAIL_RELATIVE_PATH = "thumbnailRelativePath";


    /**
     * 本地存储服务名称
     */
    public static final String LOCAL_STORAGE_SERVICE = "localStorageService";

    /**
     * 阿里云OSS存储服务名称
     */
    public static final String ALIYUN_OSS_STORAGE_SERVICE = "aliyunOssStorageService";

    /**
     * MinIO存储服务名称
     */
    public static final String MINIO_STORAGE_SERVICE = "minioStorageService";

    /**
     * 腾讯云COS存储服务名称
     */
    public static final String TENCENT_COS_STORAGE_SERVICE = "tencentCosStorageService";

}
