package cn.zhangchuangla.infrastructure.config;

import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.common.constant.Constants;
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
 * created 2025/2/19 02:15
 */
@Configuration
@Slf4j
public class ResourcesConfig implements WebMvcConfigurer {

    @Resource
    private AppConfig appConfig;


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
        registry.addResourceHandler(Constants.RESOURCE_PREFIX + "/**")
                .addResourceLocations("file:" + appConfig.getUploadPath() + "/");
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
