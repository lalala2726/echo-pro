package cn.zhangchuangla.framework.config;

import cn.zhangchuangla.common.constant.SecurityConstants;
import cn.zhangchuangla.framework.annotation.Anonymous;
import cn.zhangchuangla.framework.security.filter.TokenAuthenticationFilter;
import cn.zhangchuangla.framework.security.handel.AuthenticationEntryPointImpl;
import cn.zhangchuangla.framework.security.token.TokenManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public SecurityConfig(
            UserDetailsService userDetailsService,
            AuthenticationEntryPointImpl authenticationEntryPoint,
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenAuthenticationFilter tokenAuthenticationFilter) throws Exception {
        // 获取所有标记了@Anonymous注解的接口
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        Set<String> anonymousUrls = findAnonymousUrls(handlerMethods);
        log.info("Discovered anonymous URLs: {}", anonymousUrls);

        return http
                // 添加 CORS 配置
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .headers(headers -> headers
                        .cacheControl(HeadersConfigurer.CacheControlConfig::disable)
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 静态白名单优先匹配
                        .requestMatchers(SecurityConstants.WHITELIST).permitAll()
                        .requestMatchers(SecurityConstants.SWAGGER_WHITELIST).permitAll()
                        .requestMatchers(SecurityConstants.STATIC_RESOURCES_WHITELIST).permitAll()
                        // 动态注解白名单
                        .requestMatchers(anonymousUrls.toArray(new String[0])).permitAll()
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )
                // 使用注入的 Filter Bean
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
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
     * @param tokenManager Token管理器
     * @return TokenAuthenticationFilter 实例
     */
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter(TokenManager tokenManager) {
        return new TokenAuthenticationFilter(tokenManager);
    }

    /**
     * CORS (跨域资源共享) 配置
     *
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);
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
}
