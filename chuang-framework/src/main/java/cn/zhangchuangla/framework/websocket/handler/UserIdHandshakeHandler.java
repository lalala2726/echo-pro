package cn.zhangchuangla.framework.websocket.handler;

import cn.zhangchuangla.common.core.constant.RolesConstant;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * 握手阶段将用户ID注入为 Principal 名称的处理器。
 *
 * @author Chuang
 */
@Component
public class UserIdHandshakeHandler extends DefaultHandshakeHandler {

    /**
     * 获取用户ID
     *
     * @param request    请求
     * @param wsHandler  处理器
     * @param attributes 属性
     * @return 用户ID
     */
    @Override
    protected Principal determineUser(@NotNull ServerHttpRequest request, @NotNull WebSocketHandler wsHandler, Map<String, Object> attributes) {
        Object val = attributes.get("wsUserId");
        if (val == null) {
            return new UsernamePasswordAuthenticationToken(RolesConstant.ANONYMOUS, null);
        }
        return new UsernamePasswordAuthenticationToken(String.valueOf(val), null);
    }
}


