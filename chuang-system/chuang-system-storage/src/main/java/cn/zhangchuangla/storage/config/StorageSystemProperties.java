package cn.zhangchuangla.storage.config;

import cn.zhangchuangla.storage.constant.StorageConstants;
import com.alibaba.fastjson2.JSON;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一存储系统配置属性。
 * <p>
 * 激活逻辑:
 * 1. 优先读取 {@code storage.active-type}  application.yml。
 * 2. 若未配置，则尝试从数据库加载主存储配置。
 * 3. 若均未配置，则默认使用本地存储 (local)。
 * </p>
 *
 * @author Chuang
 */
@Data
@ConfigurationProperties(prefix = "storage")
public class StorageSystemProperties implements Serializable {

    /**
     * 在 application.yml 中显式指定的活动存储类型。
     * 可选值: "local", "minio", "aliyun_oss", "tencent_cos"。
     * 如果为空或未设置，将尝试从数据库加载或降级到本地。
     */
    private String activeType = StorageConstants.StorageType.LOCAL;
    private LocalConfig local;
    private MinioConfig minio;
    private AliyunOssConfig aliyunOss;
    private TencentCosConfig tencentCos;
    private AmazonS3Config amazonS3;

    /**
     * 本地存储配置
     */
    @Data
    public static class LocalConfig implements Serializable {


        @Serial
        private static final long serialVersionUID = 4295014936740407753L;

        /**
         * 本地存储的根路径 (绝对路径)。
         * 示例: /var/www/uploads 或 D:/uploads
         * 应用程序需要对此路径有写权限。
         */
        private String uploadPath;

        /**
         * 文件的公共可访问基础URL (如果由Web服务器或Spring资源处理器直接提供服务)。
         * <p>示例: <a href="https://yourdomain.com/uploads">...</a> 或 /resources/uploads (如果Spring提供服务)</p>
         */
        private String fileDomain;


        /**
         * 是否真实删除文件
         */
        private boolean realDelete = false;

        public String toJson() {
            return JSON.toJSONString(this);
        }

    }

    /**
     * MinIO 配置
     */
    @Data
    public static class MinioConfig implements Serializable {


        @Serial
        private static final long serialVersionUID = -237517564861379358L;

        /**
         * MinIO 服务器的端点。
         */
        private String endpoint;

        /**
         * MinIO 的访问密钥。
         */
        private String accessKey;

        /**
         * MinIO 的密钥。
         */
        private String secretKey;

        /**
         * MinIO 的存储桶名称。
         */
        private String bucketName;

        /**
         * MinIO 的文件访问域名。
         */
        private String fileDomain;

        /**
         * 是否真实删除
         */
        private boolean realDelete = true;

        public String toJson() {
            return JSON.toJSONString(this);
        }

    }

    /**
     * 阿里云OSS 配置
     */
    @Data
    public static class AliyunOssConfig implements Serializable {


        @Serial
        private static final long serialVersionUID = -2047201893309898520L;

        /**
         * 访问端点
         */
        private String endpoint;

        /**
         * 访问密钥
         */
        private String accessKeyId;

        /**
         * 密钥
         */
        private String accessKeySecret;

        /**
         * 存储桶名称
         */
        private String bucketName;

        /**
         * 文件域名
         */
        private String fileDomain;

        /**
         * 是否真实删除
         */
        private boolean realDelete = true;

        public String toJson() {
            return JSON.toJSONString(this);
        }
    }

    /**
     * 腾讯云COS 配置
     */
    @Data
    public static class TencentCosConfig implements Serializable {


        @Serial
        private static final long serialVersionUID = -7118344925027990875L;

        /**
         * 访问区域
         */
        private String region;

        /**
         * 访问密钥
         */
        private String secretId;

        /**
         * 访问密钥
         */
        private String secretKey;

        /**
         * 存储桶名称
         */
        private String bucketName;

        /**
         * 文件访问域名
         */
        private String fileDomain;

        /**
         * 是否真实删除
         */
        private boolean realDelete = true;

        public String toJson() {
            return JSON.toJSONString(this);
        }
    }

    /**
     * 亚马逊S3配置
     */
    @Data
    public static class AmazonS3Config implements Serializable {

        @Serial
        private static final long serialVersionUID = -7118344925027990875L;

        /**
         * 存储服务地址
         */
        private String endpoint;

        /**
         * 访问密钥
         */
        private String accessKey;

        /**
         * 密钥
         */
        private String secretKey;

        /**
         * 存储桶名称
         */
        private String bucketName;

        /**
         * 存储桶区域
         */
        private String region;

        /**
         * 文件访问域名
         */
        private String fileDomain;

        /**
         * 是否真实删除文件
         */
        private boolean realDelete = true;

        public String toJson() {
            return JSON.toJSONString(this);
        }
    }
}
