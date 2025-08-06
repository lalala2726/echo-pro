package cn.zhangchuangla.framework.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * CORS跨域配置
 * <p>
 * 提供灵活的跨域访问配置，支持开发环境、测试环境和生产环境的不同需求
 * </p>
 *
 * @author Chuang
 * created on 2025/8/6
 */
@Configuration
@ConfigurationProperties(prefix = "app.cors")
@Slf4j
@Data
public class CorsConfig {

    /**
     * 允许的源地址列表
     */
    private List<String> allowedOrigins = Arrays.asList(
            "http://localhost:3000",
            "http://localhost:8080",
            "http://localhost:8081",
            "http://127.0.0.1:3000",
            "http://127.0.0.1:8080",
            "http://127.0.0.1:8081"
    );

    /**
     * 允许的源地址模式列表
     */
    private List<String> allowedOriginPatterns = Arrays.asList(
            "http://localhost:*",
            "http://127.0.0.1:*",
            "https://localhost:*",
            "https://127.0.0.1:*",
            "http://192.168.*.*:*",
            "https://192.168.*.*:*",
            "http://10.*.*.*:*",
            "https://10.*.*.*:*",
            "http://172.16.*.*:*",
            "https://172.16.*.*:*"
    );

    /**
     * 允许的HTTP方法
     */
    private List<String> allowedMethods = Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
    );

    /**
     * 允许的请求头
     */
    private List<String> allowedHeaders = List.of("*");

    /**
     * 暴露的响应头
     */
    private List<String> exposedHeaders = Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Total-Count",
            "X-Request-ID",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials"
    );

    /**
     * 是否允许携带认证信息
     */
    private boolean allowCredentials = true;

    /**
     * 预检请求的缓存时间（秒）
     */
    private long maxAge = 3600L;

    /**
     * 创建增强的CORS过滤器
     * <p>
     * 提供比Spring Security默认CORS配置更灵活的跨域处理
     * </p>
     */
    @Bean
    public CorsFilter corsFilter() {
        log.info("配置增强的CORS过滤器");

        CorsConfiguration config = new CorsConfiguration();

        // 设置允许的源地址
        if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            config.setAllowedOrigins(allowedOrigins);
            log.info("CORS允许的源地址: {}", allowedOrigins);
        }

        // 设置允许的源地址模式
        if (allowedOriginPatterns != null && !allowedOriginPatterns.isEmpty()) {
            config.setAllowedOriginPatterns(allowedOriginPatterns);
            log.info("CORS允许的源地址模式: {}", allowedOriginPatterns);
        }

        // 设置允许的HTTP方法
        config.setAllowedMethods(allowedMethods);
        log.info("CORS允许的HTTP方法: {}", allowedMethods);

        // 设置允许的请求头
        config.setAllowedHeaders(allowedHeaders);

        // 设置暴露的响应头
        config.setExposedHeaders(exposedHeaders);

        // 设置是否允许携带认证信息
        config.setAllowCredentials(allowCredentials);

        // 设置预检请求的缓存时间
        config.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        log.info("增强的CORS过滤器配置完成");
        return new CorsFilter(source);
    }

    /**
     * 创建用于Spring Security的CORS配置源
     */
    @Bean("securityCorsConfigurationSource")
    public CorsConfigurationSource securityCorsConfigurationSource() {
        log.info("配置Spring Security的CORS配置源");

        CorsConfiguration configuration = new CorsConfiguration();

        // 设置允许的源地址模式
        configuration.setAllowedOriginPatterns(allowedOriginPatterns);

        // 设置允许的HTTP方法
        configuration.setAllowedMethods(allowedMethods);

        // 设置允许的请求头
        configuration.setAllowedHeaders(allowedHeaders);

        // 设置暴露的响应头
        configuration.setExposedHeaders(exposedHeaders);

        // 设置是否允许携带认证信息
        configuration.setAllowCredentials(allowCredentials);

        // 设置预检请求的缓存时间
        configuration.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
