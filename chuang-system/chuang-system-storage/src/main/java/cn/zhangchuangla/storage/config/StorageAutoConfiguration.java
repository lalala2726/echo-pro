package cn.zhangchuangla.storage.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 存储模块自动配置
 *
 * @author Chuang
 */
@Configuration
@EnableConfigurationProperties(StorageSystemProperties.class)
public class StorageAutoConfiguration {

}
