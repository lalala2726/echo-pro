package cn.zhangchuangla.common.websocket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

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
@Slf4j
@Component
public class WebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry simpUserRegistry;

    public WebSocketPublisher(SimpMessagingTemplate messagingTemplate, SimpUserRegistry simpUserRegistry) {
        this.messagingTemplate = messagingTemplate;
        this.simpUserRegistry = simpUserRegistry;
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
     * 获取所有已认证用户的ID列表。
     *
     * <p>只返回已通过认证（Principal 不为空且名称不是 "anonymous"）的用户ID。</p>
     *
     * @return 认证用户ID列表
     */
    public List<Long> getAuthenticatedUserIds() {
        return simpUserRegistry.getUsers().stream()
                .map(SimpUser::getPrincipal)
                .filter(principal -> principal != null && !"anonymous".equals(principal.getName()))
                .map(Principal::getName)
                .map(Long::valueOf)
                .toList();
    }

    /**
     * 广播消息到指定目的地（仅认证用户）。
     *
     * <p>该方法会筛选出所有已认证的用户，然后点对点发送消息，确保只有认证用户能收到消息。</p>
     *
     * @param destination 目的地（例如：/queue/message）
     * @param payload     消息体
     */
    public void broadcastToAuthenticatedUsers(@NonNull String destination, Object payload) {
        List<Long> authenticatedUserIds = getAuthenticatedUserIds();
        if (authenticatedUserIds.isEmpty()) {
            log.debug("没有认证用户在线，跳过广播消息到目的地: {}", destination);
            return;
        }

        log.debug("广播消息给 {} 个认证用户，目的地: {}", authenticatedUserIds.size(), destination);
        sendToUsers(authenticatedUserIds, destination, payload);
    }

    /**
     * 广播消息到指定目的地（所有订阅者，包括未认证用户）。
     *
     * <p>适用于公开信息的广播，不区分认证状态。</p>
     *
     * @param destination 广播目的地（例如：/topic/message/new）
     * @param payload     消息体
     */
    public void broadcast(@NonNull String destination, Object payload) {
        messagingTemplate.convertAndSend(destination, payload);
    }
}


