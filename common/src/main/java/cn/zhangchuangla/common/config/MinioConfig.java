package cn.zhangchuangla.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /**
     * Minio文件访问域名
     */
    private String fileDomain;

    /**
     * minio服务地址
     */
    private String endpoint;

    /**
     * minio accessKey
     */
    private String accessKey;

    /**
     * minio secretKey
     */
    private String secretKey;

    /**
     * minio bucketName
     */
    private String bucketName;

}
