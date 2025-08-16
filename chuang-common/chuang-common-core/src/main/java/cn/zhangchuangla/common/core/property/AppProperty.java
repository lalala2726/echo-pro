package cn.zhangchuangla.common.core.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * 软件系统配置
 *
 * @author Chuang
 */
@ConfigurationProperties(prefix = "app")
@Configuration
@Data
public class AppProperty {

    /**
     * 本地上传路径,用于映射静态资源
     */
    private String uploadPath;


}
