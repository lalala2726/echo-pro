package cn.zhangchuangla.common.websocket.service;

import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * WebSocket 推送发布器。
 *
 * <p>作用：</p>
 * <ul>
 *     <li>封装 STOMP 推送的常见用法，包括点对点与广播</li>
 *     <li>对外提供简单易用的方法，避免业务层直接依赖底层模板</li>
 * </ul>
 *
 * <p>使用方式：</p>
 * <pre>
 *   // 点对点推送给用户 10086 的 /queue/message 目的地
 *   webSocketPublisher.sendToUser(10086L, "/queue/message", payload);
 *   // 广播给订阅了 /topic/message/new 的所有客户端
 *   webSocketPublisher.broadcast("/topic/message/new", payload);
 * </pre>
 *
 * <p>注意：</p>
 * <ul>
 *     <li>点对点推送依赖服务端在鉴权阶段将 Principal 名称设置为用户ID字符串</li>
 *     <li>结合 {@code WebSocketDestinations} 常量统一管理目的地</li>
 * </ul>
 *
 * @author Chuang
 */
@Component
public class WebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 向指定用户推送消息。
     *
     * <p>底层将调用 {@code convertAndSendToUser}，需要确保 Principal 名称为用户ID字符串。</p>
     *
     * @param userId      用户ID
     * @param destination 用户目的地（例如：/queue/message）
     * @param payload     消息体
     */
    public void sendToUser(@NonNull Long userId, @NonNull String destination, Object payload) {
        messagingTemplate.convertAndSendToUser(String.valueOf(userId), destination, payload);
    }

    /**
     * 向多个用户推送消息。
     *
     * @param userIds     用户ID集合
     * @param destination 用户目的地
     * @param payload     消息体
     */
    public void sendToUsers(@NonNull Collection<Long> userIds, @NonNull String destination, Object payload) {
        for (Long userId : userIds) {
            sendToUser(userId, destination, payload);
        }
    }

    /**
     * 广播消息到指定目的地。
     *
     * <p>适用于通知全体订阅者，或按业务自行划分的主题广播。</p>
     *
     * @param destination 广播目的地（例如：/topic/message/new）
     * @param payload     消息体
     */
    public void broadcast(@NonNull String destination, Object payload) {
        messagingTemplate.convertAndSend(destination, payload);
    }
}


