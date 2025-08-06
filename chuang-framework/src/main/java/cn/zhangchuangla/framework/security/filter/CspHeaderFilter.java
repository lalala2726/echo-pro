package cn.zhangchuangla.framework.security.filter;

import cn.zhangchuangla.framework.config.DruidCspConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * CSP (Content Security Policy) 头部过滤器
 * <p>
 * 确保所有响应都包含正确的CSP头部，特别是为Druid监控界面和iframe嵌入提供支持
 * </p>
 *
 * @author Chuang
 * created on 2025/8/6
 */
@Slf4j
@Component
@Order(1) // 确保在其他过滤器之前执行
public class CspHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // 检查是否为Druid监控页面
        if (DruidCspConfig.isDruidRequest(requestUri)) {
            // 为Druid页面应用专用的CSP策略和安全头部
            Map<String, String> druidHeaders = DruidCspConfig.getDruidSecurityHeaders();
            for (Map.Entry<String, String> header : druidHeaders.entrySet()) {
                response.setHeader(header.getKey(), header.getValue());
            }
        } else {
            // 为其他页面应用通用CSP策略
            String generalCspPolicy = buildGeneralCspPolicy();
            if (!generalCspPolicy.isEmpty()) {
                response.setHeader("Content-Security-Policy", generalCspPolicy);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 构建通用的CSP策略
     *
     * @return CSP策略字符串
     */
    private String buildGeneralCspPolicy() {
        return String.join(" ",
                "default-src 'self';",
                "script-src 'self' 'unsafe-inline';",
                "style-src 'self' 'unsafe-inline';",
                "img-src 'self' data: blob:;",
                "font-src 'self';",
                "connect-src 'self';",
                "media-src 'self';",
                "object-src 'none';",
                "base-uri 'self';",
                "form-action 'self';",
                "frame-ancestors 'self' http://localhost:* https://localhost:* http://192.168.*:*;",
                "frame-src 'self';"
        );
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // 对于静态资源，可以选择不应用CSP
        return path.startsWith("/static/") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.endsWith(".ico");
    }
}
