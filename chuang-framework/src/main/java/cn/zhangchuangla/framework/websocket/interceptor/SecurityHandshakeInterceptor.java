package cn.zhangchuangla.framework.websocket.interceptor;

import cn.zhangchuangla.framework.security.token.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 握手阶段的安全拦截器。
 *
 * @author Chuang
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityHandshakeInterceptor implements HandshakeInterceptor {

    private final TokenService tokenService;

    /**
     * 握手之前，解析请求中的 token，并设置到 attributes 中，供后续使用。
     *
     * @param request    请求
     * @param response   响应
     * @param wsHandler  处理器
     * @param attributes 属性
     * @return 是否允许握手
     */
    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = resolveToken(request);
        if (!StringUtils.hasText(token)) {
            // 允许无认证建立连接（扫码、引导页等场景）
            return true;
        }
        var authentication = tokenService.parseAccessToken(token);
        if (authentication == null || authentication.getPrincipal() == null) {
            // token 无效也允许握手，后续操作再鉴权
            return true;
        }
        var principal = (cn.zhangchuangla.common.core.entity.security.SysUserDetails) authentication.getPrincipal();
        attributes.put("wsUserId", String.valueOf(principal.getUserId()));
        attributes.put("wsDeptId", principal.getDeptId());
        return true;
    }

    /**
     * 握手成功，记录日志。
     *
     * @param request   请求
     * @param response  响应
     * @param wsHandler 处理器
     * @param exception 异常
     */
    @Override
    public void afterHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("WebSocket握手失败: {}", exception.getMessage());
            log.error("请求URL: {}", request.getURI());
            log.error("详细错误: ", exception);
        } else {
            log.info("WebSocket握手成功: {}", request.getURI());
        }
    }

    /**
     * 解析请求参数中的 token（仅支持参数传递）。
     *
     * @param request 请求
     * @return token
     */
    private String resolveToken(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest servletReq) {
            String token = servletReq.getServletRequest().getParameter("token");
            return StringUtils.hasText(token) ? token : null;
        }
        return null;
    }
}


