package cn.zhangchuangla.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 01:03
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {


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
