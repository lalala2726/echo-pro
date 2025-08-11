package cn.zhangchuangla.common.websocket.constant;

/**
 * WebSocket 目的地常量定义。
 *
 * @author Chuang
 */
public interface WebSocketDestinations {

    /**
     * 点对点：用户的新消息通知。
     */
    String USER_QUEUE_MESSAGE = "/queue/message";

    /**
     * 点对点：用户的消息徽标数量变更通知。
     */
    String USER_QUEUE_MESSAGE_BADGE = "/queue/message/badge";
}


