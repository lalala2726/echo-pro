package cn.zhangchuangla.message.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 消息发送请求DTO
 *
 * @author zhangchuang
 * @since 2024-01-01
 */
@Data
public class MessageSendRequest {

    /**
     * 消息标题
     */
    @NotBlank(message = "消息标题不能为空")
    private String title;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 消息类型：1-系统消息 2-通知消息 3-公告消息
     */
    @NotNull(message = "消息类型不能为空")
    private Integer type;

    /**
     * 消息级别：1-普通 2-重要 3-紧急
     */
    private Integer level = 1;

    /**
     * 发送者ID，NULL表示系统发送
     */
    private Long senderId;

    /**
     * 发送者姓名
     */
    private String senderName;

    /**
     * 目标类型：1-指定用户 2-全部用户 3-角色用户
     */
    @NotNull(message = "目标类型不能为空")
    private Integer targetType;

    /**
     * 目标用户ID列表，target_type=1时使用
     */
    private List<Long> targetUserIds;

    /**
     * 目标角色ID列表，target_type=3时使用
     */
    private List<Long> roleIds;

    /**
     * 推送方式：1-仅站内信 2-仅WebSocket 3-站内信+WebSocket
     */
    private Integer pushType = 1;

    /**
     * 定时发送时间，NULL表示立即发送
     */
    private Date scheduledTime;

    /**
     * 过期时间，NULL表示不过期
     */
    private Date expireTime;
}
