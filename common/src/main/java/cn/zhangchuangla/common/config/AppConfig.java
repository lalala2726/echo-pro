package cn.zhangchuangla.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app.config")
@Configuration
@Data
public class AppConfig {

    /**
     * 本地上传路径
     */
    private String uploadPath;


    /**
     * oss文件访问域名
     */
    private String fileDomain;
}
