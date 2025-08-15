package cn.zhangchuangla.framework.config;

import lombok.Data;
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
@Data
public class CorsConfig {

    /**
     * 开关：是否启用严格的跨域白名单控制（默认开启）。
     * 当设置为 false 时，将放开为允许任意来源（基于 origin pattern 的 *）
     */
    private boolean enabled = true;

    /**
     * 允许的源地址列表
     */
    private List<String> allowedOrigins = Arrays.asList(
            "http://localhost:3000",
            "http://localhost:8080",
            "http://localhost:8081",
            "http://127.0.0.1:3000",
            "http://127.0.0.1:8080",
            "http://127.0.0.1:8081",
            "https://echo.zhangchuangla.cn",
            "http://echo.zhangchuangla.cn"
    );

    /**
     * 允许的源地址模式列表
     */
    private List<String> allowedOriginPatterns = Arrays.asList(
            "http://*:*",
            "https://*:*"
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

        CorsConfiguration config = new CorsConfiguration();

        // 根据开关设置不同的跨域策略
        if (enabled) {
            // 启用严格白名单控制
            // 设置允许的源地址
            if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
                config.setAllowedOrigins(allowedOrigins);
            }

            // 设置允许的源地址模式
            if (allowedOriginPatterns != null && !allowedOriginPatterns.isEmpty()) {
                config.setAllowedOriginPatterns(allowedOriginPatterns);
            }
        } else {
            // 禁用严格控制，允许所有来源
            config.setAllowedOriginPatterns(List.of("*"));
        }

        // 设置允许的HTTP方法
        config.setAllowedMethods(allowedMethods);

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
        return new CorsFilter(source);
    }

    /**
     * 创建用于Spring Security的CORS配置源
     */
    @Bean("securityCorsConfigurationSource")
    public CorsConfigurationSource securityCorsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 根据开关设置不同的跨域策略
        if (enabled) {
            // 启用严格白名单控制
            // 设置允许的源地址（精确匹配）
            if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
                configuration.setAllowedOrigins(allowedOrigins);
            }

            // 设置允许的源地址模式
            if (allowedOriginPatterns != null && !allowedOriginPatterns.isEmpty()) {
                configuration.setAllowedOriginPatterns(allowedOriginPatterns);
            }
        } else {
            // 禁用严格控制，允许所有来源
            configuration.setAllowedOriginPatterns(List.of("*"));
        }

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
