package cn.zhangchuangla.infrastructure.security.filter;

import cn.zhangchuangla.common.core.model.entity.LoginUser;
import cn.zhangchuangla.infrastructure.web.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Token认证拦截器
 * 该拦截器用于验证请求中的JWT token，并将用户信息设置到Spring Security的上下文中。
 *
 * @author Chuang
 * created on 2025/2/19 17:47
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public JwtAuthenticationTokenFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }


    /**
     * 拦截请求，判断请求头中是否包含token，并且验证token是否正确
     *
     * @param request     请求
     * @param response    响应
     * @param filterChain 过滤器链
     * @throws ServletException servlet异常
     * @throws IOException      io异常
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        // 获取token
        // 验证token是否存在且有效
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser != null) {
            // 创建认证对象并设置到SecurityContext中
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }


}
