package cn.zhangchuangla.infrastructure.config;


import cn.zhangchuangla.common.constant.SecurityConstants;
import cn.zhangchuangla.infrastructure.annotation.Anonymous;
import cn.zhangchuangla.infrastructure.security.filter.TokenAuthenticationFilter;
import cn.zhangchuangla.infrastructure.security.handel.AuthenticationEntryPointImpl;
import cn.zhangchuangla.infrastructure.security.token.TokenManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 安全配置类
 * 该类配置了Spring Security的相关设置，包括认证、授权和过滤器链。
 *
 * @author Chuang
 * created on 2025/2/19 01:13
 */
@Slf4j
@Configuration
@EnableMethodSecurity()
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final AuthenticationEntryPointImpl authenticationEntryPoint;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final TokenManager tokenManager;


    /**
     * Spring Security 过滤器链配置
     * 该方法配置了Spring Security的过滤器链，包括CSRF、会话管理、异常处理和请求授权等。
     *
     * @param http HttpSecurity对象
     * @return SecurityFilterChain对象
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 获取所有标记了@Anonymous注解的接口
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        Set<String> anonymousUrls = new HashSet<>();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();

            // 获取方法上的@Anonymous注解
            Anonymous methodAnonymous = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Anonymous.class);
            // 获取类上的@Anonymous注解
            Anonymous classAnonymous = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Anonymous.class);

            if (methodAnonymous != null || classAnonymous != null) {
                Set<String> patterns = entry.getKey().getPatternValues();
                anonymousUrls.addAll(patterns);
            }
        }

        return http
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
                // 基于token，所以不需要session
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 禁用HTTP响应标头
                .headers(headers -> headers
                        .cacheControl(HeadersConfigurer.CacheControlConfig::disable)
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .csrf(AbstractHttpConfigurer::disable)      // 禁用 CSRF 防护，前后端分离无需此防护机制
                .formLogin(AbstractHttpConfigurer::disable) // 禁用默认的表单登录功能，前后端分离采用 Token 认证方式
                .httpBasic(AbstractHttpConfigurer::disable) // 禁用 HTTP Basic 认证，避免弹窗式登录
                // 过滤请求
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SecurityConstants.WHITELIST).permitAll()
                        .requestMatchers(SecurityConstants.SWAGGER_WHITELIST).permitAll()
                        .requestMatchers(SecurityConstants.STATIC_RESOURCES_WHITELIST).permitAll()
                        .requestMatchers(anonymousUrls.toArray(new String[0])).permitAll()
                        .anyRequest().authenticated()  // 其他请求需要认证
                )
                // 添加JWT filter
                .addFilterBefore(new TokenAuthenticationFilter(tokenManager), UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    /**
     * 身份验证实现
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return new ProviderManager(daoAuthenticationProvider);
    }

    /**
     * 强散列哈希加密实现
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
