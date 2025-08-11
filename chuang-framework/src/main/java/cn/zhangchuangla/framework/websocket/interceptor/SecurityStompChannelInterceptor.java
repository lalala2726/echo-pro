package cn.zhangchuangla.framework.websocket.interceptor;

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
            // 允许无 token 的连接；若带 token 则尝试解析并注入用户上下文
            String token = accessor.getFirstNativeHeader("token");
            if (StringUtils.hasText(token)) {
                var authentication = tokenService.parseAccessToken(token);
                if (authentication != null && authentication.getPrincipal() != null) {
                    var principal = (cn.zhangchuangla.common.core.entity.security.SysUserDetails) authentication.getPrincipal();
                    Principal user = new UsernamePasswordAuthenticationToken(String.valueOf(principal.getUserId()), null, authentication.getAuthorities());
                    accessor.setUser(user);
                }
            }
        }

        return message;
    }


}


