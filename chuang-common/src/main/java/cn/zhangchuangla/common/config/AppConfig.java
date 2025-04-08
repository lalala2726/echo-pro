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
     * 系统名称
     */
    private String name;

    /**
     * 系统版本
     */
    private String version;

    /**
     * 系统作者
     */
    private String author;

    /**
     * 系统描述
     */
    private String description;

    /**
     * 系统版权
     */
    private String copyright;


    /**
     * 本地上传路径
     */
    private String uploadPath;

    /**
     * 文件访问路径
     */
    private String fileDomain;


    /**
     * 是否开启ip地址解析
     */
    private boolean ipAddressEnable;

    /**
     * 是否开始登录严格模式
     */
    private boolean strictLoginEnable;


}
