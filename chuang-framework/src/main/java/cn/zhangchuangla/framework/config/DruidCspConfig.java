package cn.zhangchuangla.framework.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * Druid监控专用CSP配置
 * <p>
 * 为Druid监控界面提供特殊的CSP策略，解决内联脚本和样式的安全策略问题
 * </p>
 *
 * @author Chuang
 * created on 2025/8/6
 */
@Slf4j
@Configuration
@ConditionalOnClass(name = "com.alibaba.druid.support.http.StatViewServlet")
public class DruidCspConfig {

    /**
     * 构建Druid专用的CSP策略
     * <p>
     * Druid监控界面需要内联脚本和样式，因此需要特殊的CSP配置
     * </p>
     *
     * @return Druid专用CSP策略字符串
     */
    public static String buildDruidCspPolicy() {
        return String.join(" ",
                // 默认源：允许同源
                "default-src 'self';",
                // 脚本源：允许同源、内联脚本和eval（Druid需要）
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
                // 框架祖先：允许iframe嵌入
                "frame-ancestors 'self' *;",
                // 框架源：允许同源
                "frame-src 'self';"
        );
    }

    /**
     * 检查请求是否为Druid监控页面
     *
     * @param requestUri 请求URI
     * @return 是否为Druid页面
     */
    public static boolean isDruidRequest(String requestUri) {
        return requestUri != null && requestUri.startsWith("/druid/");
    }

    /**
     * 获取Druid页面的安全头部配置
     *
     * @return 安全头部配置
     */
    public static java.util.Map<String, String> getDruidSecurityHeaders() {
        java.util.Map<String, String> headers = new java.util.HashMap<>();
        headers.put("Content-Security-Policy", buildDruidCspPolicy());
        headers.put("X-Content-Type-Options", "nosniff");
        // 禁用XSS保护以避免与CSP冲突
        headers.put("X-XSS-Protection", "0");
        headers.put("Referrer-Policy", "strict-origin-when-cross-origin");
        return headers;
    }
}
