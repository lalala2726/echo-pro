package cn.zhangchuangla.infrastructure.config;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.entity.file.LocalFileConfigEntity;
import cn.zhangchuangla.storage.config.loader.SysFileConfigLoader;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 资源路径配置类
 * <p>
 * 该类用于配置 Spring Boot 的静态资源映射，支持本地存储文件访问和 API 文档访问
 *
 * @author Chuang
 * @date 2025/2/19 02:15
 */
@Configuration
@Slf4j
public class ResourcesConfig implements WebMvcConfigurer {

    @Resource
    private SysFileConfigLoader sysFileConfigLoader;

    /**
     * 配置静态资源访问路径
     */
    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
        configureLocalFileAccess(registry);
        configureStaticResources(registry);
        configureApiDocsResources(registry);
    }

    /**
     * 配置本地文件存储的访问路径
     */
    private void configureLocalFileAccess(ResourceHandlerRegistry registry) {
        String currentDefaultUploadType = sysFileConfigLoader.getCurrentDefaultUploadType();
        log.info("当前默认存储类型：{}", currentDefaultUploadType);

        // 仅当存储类型为本地文件时，才进行资源映射
        if (Constants.LOCAL_FILE_UPLOAD.equals(currentDefaultUploadType)) {
            LocalFileConfigEntity localFileConfig = sysFileConfigLoader.getLocalFileConfig();
            if (localFileConfig.getUploadPath() != null) {
                log.info("静态资源加载映射: {}", localFileConfig.getUploadPath());
                registry.addResourceHandler(Constants.RESOURCE_PREFIX + "/**")
                        .addResourceLocations("file:" + localFileConfig.getUploadPath() + "/");

            }
            log.info("本地文件存储路径未配置，跳过静态资源映射");
        }
    }

    /**
     * 配置静态资源（如 `static` 目录下的 CSS、JS、图片等）
     */
    private void configureStaticResources(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    /**
     * 配置 API 文档（如 `knife4j`）的静态资源映射
     */
    private void configureApiDocsResources(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
