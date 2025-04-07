package cn.zhangchuangla.infrastructure.config;

import cn.zhangchuangla.infrastructure.interceptor.AccessLimitInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 注册应用内的拦截器和其他MVC相关配置
 *
 * @author Chuang
 * <p>
 * created on 2025/4/7 22:15
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 接口限流拦截器
     */
    private final AccessLimitInterceptor accessLimitInterceptor;

    /**
     * 配置RestTemplate
     *
     * @return RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 添加拦截器
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册接口限流拦截器并应用到所有API路径
        registry.addInterceptor(accessLimitInterceptor)
                .addPathPatterns("/**") // 应用到所有API路径
                .excludePathPatterns( // 排除一些不需要限流的路径
                        "/login",
                        "/logout",
                        "/error/**",
                        "/static/**",
                        "/favicon.ico");
    }
}
