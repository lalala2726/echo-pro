package cn.zhangchuangla.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * 软件系统配置
 */
@ConfigurationProperties(prefix = "app.config")
@Configuration
@Data
public class AppConfig {

    /**
     * 本地上传路径
     */
    private String uploadPath;

}
