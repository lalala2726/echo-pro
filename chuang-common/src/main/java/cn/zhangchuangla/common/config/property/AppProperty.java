package cn.zhangchuangla.common.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * 软件系统配置
 */
@ConfigurationProperties(prefix = "app.config")
@Configuration
@Data
public class AppProperty {


    /**
     * 本地上传路径
     */
    private String uploadPath;

    /**
     * 是否开启回收站
     */
    private boolean enableTrash;

    /**
     * 文件访问路径
     */
    private String fileDomain;


}
