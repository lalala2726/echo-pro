package cn.zhangchuangla.framework.websocket.interceptor;

import cn.zhangchuangla.common.core.exception.AuthorizationException;
import cn.zhangchuangla.framework.security.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Principal;

/**
 * STOMP 入站通道安全拦截器。
 *
 * @author Chuang
 */
@Component
@RequiredArgsConstructor
public class SecurityStompChannelInterceptor implements ChannelInterceptor {

    private final TokenService tokenService;

    /**
     * 在消息发送之前进行拦截处理
     *
     * @param message 消息
     * @param channel 通道
     * @return 拦截后的消息
     */
    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = resolveFromHeaders(accessor);
            var authentication = tokenService.parseAccessToken(token);
            if (authentication == null || authentication.getPrincipal() == null) {
                throw new AuthorizationException("Invalid token");
            }
            var principal = (cn.zhangchuangla.common.core.entity.security.SysUserDetails) authentication.getPrincipal();
            Principal user = new UsernamePasswordAuthenticationToken(String.valueOf(principal.getUserId()), null, authentication.getAuthorities());
            accessor.setUser(user);
        }

        return message;
    }

    /**
     * 从请求头中解析令牌
     *
     * @param accessor 请求头访问器
     * @return 令牌
     */
    private String resolveFromHeaders(StompHeaderAccessor accessor) {
        String auth = accessor.getFirstNativeHeader("Authorization");
        if (!StringUtils.hasText(auth)) {
            auth = accessor.getFirstNativeHeader("token");
        }
        if (!StringUtils.hasText(auth)) {
            throw new AuthorizationException("Missing token");
        }
        return auth;
    }
}


