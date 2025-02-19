package cn.zhangchuangla.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangchuang
 */
@ConfigurationProperties(prefix = "aliyun.oss")
@Configuration
@Data
public class AliyunOSSConfig {

    /**
     * 文件访问域名
     */
    private String FileDomain;

    /**
     * 地域节点
     */
    private String endPoint;

    /**
     * accessKeyId
     */
    private String accessKeyId;

    /**
     * accessKeySecret
     */
    private String accessKeySecret;

    /**
     * bucketName
     */
    private String bucketName;


}
