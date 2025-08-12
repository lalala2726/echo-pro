package cn.zhangchuangla.common.websocket.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

/**
 * WebSocket 认证成功提示事件监听器。
 *
 * <p>
 * 在 STOMP 会话建立（CONNECTED）后，如果存在认证用户，则向其发送一条认证成功的点对点消息。
 * </p>
 *
 * @author Chuang
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WsAuthEventListener {


    /**
     * 认证成功操作逻辑
     *
     * @param event 认证成功事件
     */
    @EventListener
    public void onConnected(SessionConnectedEvent event) {

    }
}


