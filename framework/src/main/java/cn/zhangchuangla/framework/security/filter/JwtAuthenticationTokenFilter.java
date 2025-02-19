package cn.zhangchuangla.framework.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * token认证拦截器
 *
 * @author Chuang
 * <p>
 * created on 2025/2/19 17:47
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //todo 验证token是否合法
        //直接放行
        filterChain.doFilter(request, response);
    }
}
