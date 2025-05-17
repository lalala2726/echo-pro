package cn.zhangchuangla.storage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 统一存储系统配置属性。
 * <p>
 * 激活逻辑:
 * 1. 优先读取 {@code storage.active-type} จาก application.yml。
 * 2. 若未配置，则尝试从数据库加载主存储配置。
 * 3. 若均未配置，则默认使用本地存储 (local)。
 * </p>
 *
 * @author zhangchuang
 */
@ConfigurationProperties(prefix = "storage")
@Data
public class StorageSystemProperties {

    /**
     * 在 application.yml 中显式指定的活动存储类型。
     * 可选值: "local", "minio", "aliyun_oss", "tencent_cos"。
     * 如果为空或未设置，将尝试从数据库加载或降级到本地。
     */
    private String activeType;

    private LocalConfig local;
    private MinioConfig minio;
    private AliyunOssConfig aliyunOss;
    private TencentCosConfig tencentCos;

    /**
     * 本地存储配置
     */
    @Data
    public static class LocalConfig implements TrashConfigurable {
        /**
         * 本地存储的根路径 (绝对路径)。
         * 示例: /var/www/uploads 或 D:/uploads
         * 应用程序需要对此路径有写权限。
         */
        private String rootPathOrBucketName; // 对应旧的 rootPathOrBucketName

        /**
         * 文件的公共可访问基础URL (如果由Web服务器或Spring资源处理器直接提供服务)。
         * 示例: https://yourdomain.com/uploads 或 /resources/uploads (如果Spring提供服务)
         */
        private String fileDomain;

        /**
         * 是否启用回收站功能。
         */
        private boolean enableTrash = true;

        /**
         * 回收站目录的名称。
         * 默认为 "trash"。
         */
        private String trashDirectoryName = "trash";
    }

    /**
     * MinIO 配置
     */
    @Data
    public static class MinioConfig implements TrashConfigurable {
        private String endpoint;
        private String accessKey;
        private String secretKey;
        private String rootPathOrBucketName;
        private String fileDomain;
        private boolean enableTrash = true;
        private String trashDirectoryName = "trash";
    }

    /**
     * 阿里云OSS 配置
     */
    @Data
    public static class AliyunOssConfig implements TrashConfigurable {
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
        private String rootPathOrBucketName;
        private String fileDomain;
        private boolean enableTrash = true;
        private String trashDirectoryName = "trash";
    }

    /**
     * 腾讯云COS 配置
     */
    @Data
    public static class TencentCosConfig implements TrashConfigurable {
        private String region;
        private String secretId;
        private String secretKey;
        private String rootPathOrBucketName;
        private String fileDomain;
        private boolean enableTrash = true;
        private String trashDirectoryName = "trash";
    }
}
