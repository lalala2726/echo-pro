package cn.zhangchuangla.websocket.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * WebSocket消息推送DTO
 *
 * @author zhangchuang
 * @since 2024-01-01
 */
@Data
@Accessors(chain = true)
public class WebSocketMessageDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型：1-系统消息 2-通知消息 3-公告消息
     */
    private Integer type;

    /**
     * 消息级别：1-普通 2-重要 3-紧急
     */
    private Integer level;

    /**
     * 发送者姓名
     */
    private String senderName;

    /**
     * 目标用户ID列表
     */
    private List<Long> targetUserIds;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 消息推送类型：message-消息通知, system-系统通知
     */
    private String pushType = "message";

    /**
     * 扩展数据
     */
    private Object extraData;
}
