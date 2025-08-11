package cn.zhangchuangla.system.message.push;

import cn.zhangchuangla.common.websocket.constant.WebSocketDestinations;
import cn.zhangchuangla.common.websocket.service.WebSocketPublisher;
import cn.zhangchuangla.system.message.model.dto.NewMessageNoticeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/8/11 20:01
 */
@Service
@RequiredArgsConstructor
public class MessagePushService {

    private final WebSocketPublisher webSocketPublisher;


    /**
     * 发送新消息通知
     *
     * @param userId 用户ID
     * @param notice 新消息通知
     */
    public void pushMessageNotifyToUser(List<Long> userId, NewMessageNoticeDTO notice) {
        webSocketPublisher.sendToUsers(userId, WebSocketDestinations.USER_QUEUE_MESSAGE, notice);
    }

    /**
     * 给全部用户发送消息
     *
     * @param notice 新消息通知
     */
    public void pushMessageNotifyToAllUser(NewMessageNoticeDTO notice) {
        webSocketPublisher.broadcastToAuthenticatedUsers(WebSocketDestinations.USER_QUEUE_MESSAGE, notice);
    }


    /**
     * 推送消息已读未读数量给用户
     *
     * @param userId 用户ID
     * @param read   已读数量
     * @param unread 未读数量
     */
    public void pushMessageReadCount(Long userId, Long read, Long unread) {
        HashMap<String, Long> response = new HashMap<>();
        response.put("read", read);
        response.put("unread", unread);
        webSocketPublisher.sendToUser(userId, WebSocketDestinations.USER_QUEUE_MESSAGE_BADGE, response);
    }


}
