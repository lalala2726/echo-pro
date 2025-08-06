package cn.zhangchuangla.framework.config;

import cn.zhangchuangla.common.core.constant.SecurityConstants;
import cn.zhangchuangla.framework.annotation.Anonymous;
import cn.zhangchuangla.framework.security.filter.TokenAuthenticationFilter;
import cn.zhangchuangla.framework.security.handel.AuthenticationEntryPointImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 安全配置类
 * 该类配置了Spring Security的相关设置，包括认证、授权和过滤器链。
 *
 * @author Chuang
 * created on 2024/04/23 16:48
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final AuthenticationEntryPointImpl authenticationEntryPoint;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    public SecurityConfig(UserDetailsService userDetailsService, AuthenticationEntryPointImpl authenticationEntryPoint,
                          @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

    /**
     * Spring Security 过滤器链配置
     *
     * @param http                      HttpSecurity对象
     * @param tokenAuthenticationFilter 自定义的Token认证过滤器 (通过Bean注入)
     * @return SecurityFilterChain对象
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   TokenAuthenticationFilter tokenAuthenticationFilter) throws Exception {
        // 获取所有标记了@Anonymous注解的接口
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        Set<String> anonymousUrls = findAnonymousUrls(handlerMethods);
        log.info("Discovered anonymous URLs: {}", anonymousUrls);

        return http
                // CORS 配置
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 强制 HTTPS
                //.requiresChannel(channel -> channel.anyRequest().requiresSecure())
                // 统一异常处理：未认证和访问拒绝
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint))
                // 无状态会话
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 安全头增强：HSTS、CSP、XSS 保护
                .headers(headers -> headers
                        .cacheControl(HeadersConfigurer.CacheControlConfig::disable)
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                        .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
                        // 配置CSP策略，为Druid监控界面提供必要的权限
                        .contentSecurityPolicy(csp -> csp.policyDirectives(buildContentSecurityPolicy())))
                // 关闭 CSRF、表单登录、Basic Auth
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // 授权规则
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(SecurityConstants.WHITELIST).permitAll();
                    auth.requestMatchers(SecurityConstants.SWAGGER_WHITELIST).permitAll();
                    auth.requestMatchers(SecurityConstants.STATIC_RESOURCES_WHITELIST).permitAll();
                    if (!anonymousUrls.isEmpty()) {
                        auth.requestMatchers(anonymousUrls.toArray(new String[0])).permitAll();
                    }
                    auth.anyRequest().authenticated();
                })
                // 插入自定义 Token 认证过滤器
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 禁用 logout filter
                .logout(AbstractHttpConfigurer::disable)
                .build();
    }

    /**
     * 查找所有标记了 @Anonymous 注解的接口URL
     *
     * @param handlerMethods 处理器方法映射
     * @return 包含所有匿名URL的集合
     */
    private Set<String> findAnonymousUrls(Map<RequestMappingInfo, HandlerMethod> handlerMethods) {
        Set<String> anonymousUrls = new HashSet<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            Anonymous methodAnonymous = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Anonymous.class);
            Anonymous classAnonymous = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Anonymous.class);

            if (methodAnonymous != null || classAnonymous != null) {
                Set<String> patterns = entry.getKey().getPatternValues();
                if (!patterns.isEmpty()) {
                    anonymousUrls.addAll(patterns);
                }
            }
        }
        return anonymousUrls;
    }

    /**
     * 将 TokenAuthenticationFilter 声明为 Bean
     * 这样可以由 Spring 管理其生命周期，并允许注入其他依赖
     *
     * @return TokenAuthenticationFilter 实例
     */
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }


    /**
     * CORS (跨域资源共享) 配置
     *
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 身份验证管理器 Bean
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return new ProviderManager(daoAuthenticationProvider);
    }

    /**
     * 密码编码器 Bean (BCrypt)
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }


    /**
     * 构建Content Security Policy策略
     * <p>
     * 为Druid监控界面和其他组件提供适当的CSP权限，同时保持安全性
     * </p>
     *
     * @return CSP策略字符串
     */
    private String buildContentSecurityPolicy() {
        log.info("构建CSP策略 - 为Druid监控界面配置安全策略");
        String cspPolicy = String.join(" ",
                // 默认源：只允许同源
                "default-src 'self';",
                // 脚本源：允许同源和内联脚本（Druid需要）
                "script-src 'self' 'unsafe-inline' 'unsafe-eval';",
                // 样式源：允许同源和内联样式（Druid需要）
                "style-src 'self' 'unsafe-inline';",
                // 图片源：允许同源和data URI（用于图标和图表）
                "img-src 'self' data: blob:;",
                // 字体源：允许同源
                "font-src 'self';",
                // 连接源：允许同源（用于AJAX请求）
                "connect-src 'self';",
                // 媒体源：允许同源
                "media-src 'self';",
                // 对象源：禁止（安全考虑）
                "object-src 'none';",
                // 基础URI：限制为同源
                "base-uri 'self';",
                // 表单提交：允许同源
                "form-action 'self';",
                // 框架祖先：允许同源（Druid可能需要iframe）
                "frame-ancestors 'self';",
                // 框架源：允许同源
                "frame-src 'self';"
        );
        log.info("CSP策略构建完成: {}", cspPolicy);
        return cspPolicy;
    }
}
