package cn.zhangchuangla.framework.config;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.redis.ConfigCacheService;
import cn.zhangchuangla.common.entity.file.LocalFileConfigEntity;
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

    private final ConfigCacheService configCacheService;

    public ResourcesConfig(ConfigCacheService configCacheService) {
        this.configCacheService = configCacheService;
    }


    /**
     * 配置静态资源访问路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 本地文件上传路径
        LocalFileConfigEntity localFileConfig = configCacheService.getLocalFileConfig();
        if (localFileConfig != null) {
            registry.addResourceHandler(Constants.RESOURCE_PREFIX + "/**")
                    .addResourceLocations("file:" + localFileConfig.getUploadPath());
        }


        // 添加静态资源映射规则
        registry.addResourceHandler("classpath:/META-INF/resources/static/**")
                .addResourceLocations("classpath:/META-INF/resources/static/");


        // 添加静态资源映射规则
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        //配置 knife4j 的静态资源请求映射地址
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }


}
