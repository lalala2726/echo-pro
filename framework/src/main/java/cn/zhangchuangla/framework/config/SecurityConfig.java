package cn.zhangchuangla.framework.config;

import cn.zhangchuangla.framework.annotation.Anonymous;
import cn.zhangchuangla.framework.security.filter.JwtAuthenticationTokenFilter;
import cn.zhangchuangla.framework.security.handel.AuthenticationEntryPointImpl;
import cn.zhangchuangla.framework.security.handel.LogoutSuccessHandlerImpl;
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
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final AuthenticationEntryPointImpl authenticationEntryPoint;
    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    private final LogoutSuccessHandlerImpl logoutSuccessHandler;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    public SecurityConfig(UserDetailsService userDetailsService, AuthenticationEntryPointImpl authenticationEntryPoint, JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter, LogoutSuccessHandlerImpl logoutSuccessHandler, RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jwtAuthenticationTokenFilter = jwtAuthenticationTokenFilter;
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
    }

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
                // 禁用 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 禁用HTTP响应标头
                .headers(headers -> headers
                        .cacheControl(HeadersConfigurer.CacheControlConfig::disable)
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
                // 基于token，所以不需要session
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 过滤请求
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register", "/logout").permitAll()  // 明确允许登录和注册接口
                        .requestMatchers("/", "/druid/**").permitAll()  // 允许所有请求，包括根路径和所有子路径
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/**", "/webjars/**").permitAll()  // Swagger相关资源
                        // 静态资源允许访问
                        .requestMatchers("/static/**", "/profile/**", "/**.html", "/**.css", "/**.js", "/favicon.ico").permitAll()
                        // 添加自定义匿名访问的URL
                        .requestMatchers(anonymousUrls.toArray(new String[0])).permitAll()
                        .anyRequest().authenticated()  // 其他请求需要认证
                )
                // 添加JWT filter
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // 添加退出登录filter
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler))
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
