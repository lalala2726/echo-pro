package cn.zhangchuangla.framework.websocket.interceptor;

import cn.zhangchuangla.common.core.constant.RolesConstant;
import cn.zhangchuangla.common.core.constant.SecurityConstants;
import cn.zhangchuangla.common.core.exception.AuthorizationException;
import cn.zhangchuangla.framework.security.token.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
            // 若握手阶段已经有认证用户，则沿用；否则尝试从会话属性或 STOMP 头中的 token 完成认证
            Principal user = accessor.getUser();
            String sessionId = accessor.getSessionId();
            if (user == null || RolesConstant.ANONYMOUS.equals(user.getName())) {
                // 1) 先从会话属性中恢复（握手阶段放入的 wsUserId）
                Object wsUserId = accessor.getSessionAttributes() != null ? accessor.getSessionAttributes().get("wsUserId") : null;
                if (wsUserId != null) {
                    user = new UsernamePasswordAuthenticationToken(String.valueOf(wsUserId), null);
                    accessor.setUser(user);
                    log.info("[WS CONNECT] 从会话属性恢复用户成功，userId={}，sessionId={}", wsUserId, sessionId);
                }
            }
            if (user == null || RolesConstant.ANONYMOUS.equals(user.getName())) {
                String token = accessor.getFirstNativeHeader("token");
                if (StringUtils.hasText(token)) {
                    try {
                        var authentication = tokenService.parseAccessToken(token);
                        if (authentication != null && authentication.getPrincipal() != null) {
                            var sysUserDetails = (cn.zhangchuangla.common.core.entity.security.SysUserDetails) authentication.getPrincipal();
                            user = new UsernamePasswordAuthenticationToken(String.valueOf(sysUserDetails.getUserId()), null, authentication.getAuthorities());
                            accessor.setUser(user);

                            // 将用户信息存储到会话属性中
                            if (accessor.getSessionAttributes() != null) {
                                accessor.getSessionAttributes().put("wsUserId", sysUserDetails.getUserId());
                                accessor.getSessionAttributes().put("wsDeptId", sysUserDetails.getDeptId());
                            }
                        }
                    } catch (Exception e) {
                        // Token 解析失败，保持匿名
                        log.warn("[WS CONNECT] STOMP 头部 token 解析失败，保持匿名，sessionId={}，err={}", sessionId, e.getMessage());
                    }
                } else {
                    log.info("[WS CONNECT] 未提供 STOMP 头部 token，保持匿名，sessionId={}", sessionId);
                }
            }

            // 认证成功的欢迎消息在事件监听器中异步发送，避免循环依赖
            Principal finalUser = accessor.getUser();
            if (finalUser == null || RolesConstant.ANONYMOUS.equals(finalUser.getName())) {
                log.info("[WS CONNECT] 会话为匿名用户，sessionId={}", sessionId);
            } else {
                log.info("[WS CONNECT] 会话为认证用户，userId={}，sessionId={}", finalUser.getName(), sessionId);
            }
        }
        // 处理 SUBSCRIBE 帧：进行订阅权限验证
        else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            handleSubscribe(accessor);
        }


        return message;
    }

    /**
     * 处理 STOMP SUBSCRIBE 帧的授权逻辑。
     *
     * @param accessor StompHeaderAccessor 对象
     * @throws AuthorizationException 如果用户无权订阅该目的地
     */
    private void handleSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        Principal principal = accessor.getUser();

        if (!StringUtils.hasText(destination)) {
            throw new AuthorizationException("Invalid subscribe destination");
        }

        // 获取用户认证状态
        boolean isAuthenticated = principal != null && !RolesConstant.ANONYMOUS.equals(principal.getName());

        if (!isAuthenticated) {
            // 匿名用户：仅允许订阅匿名白名单中的主题
            if (!isAnonymousDestinationAllowed(destination)) {
                throw new AuthorizationException("Anonymous users are not allowed to subscribe to: " + destination);
            }
        }
    }


    /**
     * 检查目的地是否在匿名用户白名单中。
     *
     * @param destination 目的地
     * @return 是否允许匿名用户订阅
     */
    private boolean isAnonymousDestinationAllowed(String destination) {
        for (String allowedPattern : SecurityConstants.WEBSOCKET_ANONYMOUS_SUBSCRIBE_WHITELIST) {
            if (destination.startsWith(allowedPattern)) {
                return true;
            }
        }
        return false;
    }


}


