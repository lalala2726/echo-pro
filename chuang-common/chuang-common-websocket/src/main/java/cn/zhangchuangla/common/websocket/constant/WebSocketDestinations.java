package cn.zhangchuangla.common.websocket.constant;

/**
 * WebSocket 目的地常量定义。
 *
 * @author Chuang
 */
public interface WebSocketDestinations {

    /**
     * 广播：有新系统消息产生。
     */
    String TOPIC_MESSAGE_NEW = "/topic/message/new";

    /**
     * 点对点：用户的新消息通知。
     */
    String USER_QUEUE_MESSAGE = "/queue/message";

    /**
     * 点对点：用户的消息徽标数量变更通知。
     */
    String USER_QUEUE_MESSAGE_BADGE = "/queue/message/badge";

    /**
     * 广播：角色级别的消息通知前缀。
     */
    String TOPIC_ROLE = "/topic/role/";

    /**
     * 广播：部门级别的消息通知前缀。
     */
    String TOPIC_DEPT = "/topic/dept/";
}


