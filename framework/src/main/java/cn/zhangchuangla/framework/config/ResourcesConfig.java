package cn.zhangchuangla.framework.config;

import cn.zhangchuangla.common.config.AppConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 02:15
 */
@Configuration
public class ResourcesConfig implements WebMvcConfigurer {

    private final AppConfig appConfig;

    public ResourcesConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }


    /**
     * 配置静态资源访问路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String filePath = appConfig.getUploadPath();
        registry.addResourceHandler("/static/**")
                .addResourceLocations("file:" + filePath + "/");

        registry.addResourceHandler("classpath:/META-INF/resources/static/**")
                .addResourceLocations("classpath:/META-INF/resources/static/");
    }
}
