package cn.zhangchuangla.system.message.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/24 22:47
 */
@Data
@Schema(name = "消息", description = "发送消息请求参数")
public class MessageRequest {

    /**
     * 消息标题
     */
    @Schema(description = "消息标题", type = "string", example = "系统消息")
    private String title;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容", type = "string", example = "消息内容示例")
    private String content;

    /**
     * 消息类型：1-系统消息 2-通知消息 3-公告消息
     */
    @Schema(description = "消息类型：1-系统消息 2-通知消息 3-公告消息", type = "integer", example = "1")
    private Integer type;

    /**
     * 消息级别：1-普通 2-重要 3-紧急
     */
    @Schema(description = "消息级别：1-普通 2-重要 3-紧急", type = "integer", example = "1")
    private Integer level;

    /**
     * 推送方式：1-仅站内信 2-仅WebSocket 3-站内信+WebSocket
     */
    @Schema(description = "推送方式：1-仅站内信 2-仅WebSocket 3-站内信+WebSocket", type = "integer", example = "1")
    private Integer pushType;

    /**
     * 是否已发布：0-未发布 1-已发布
     */
    @Schema(description = "是否已发布：0-未发布 1-已发布", type = "integer", example = "0")
    private Integer isPublished;

    /**
     * 定时发送时间，NULL表示立即发送
     */
    @Schema(description = "定时发送时间，NULL表示立即发送", type = "LocalDateTime")
    private LocalDateTime scheduledTime;

    /**
     * 过期时间，NULL表示不过期
     */
    @Schema(description = "过期时间，NULL表示不过期", type = "LocalDateTime")
    private LocalDateTime expireTime;

}
