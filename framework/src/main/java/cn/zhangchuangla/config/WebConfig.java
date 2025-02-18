package cn.zhangchuangla.config;

import cn.zhangchuangla.common.config.AppConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 01:03
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {


    private final AppConfig appConfig;

    public WebConfig(AppConfig appConfig) {
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

    /**
     * 配置RestTemplate
     *
     * @return RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
