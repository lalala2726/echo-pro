package cn.zhangchuangla.system.message.model.vo.user;

import cn.zhangchuangla.system.message.enums.MessageLevelEnum;
import cn.zhangchuangla.system.message.enums.MessageTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/25 00:39
 */
@Data
@Schema(name = "用户消息列表", description = "用户消息列表")
public class UserMessageListVo {

    /**
     * 消息ID
     */
    @Schema(description = "消息ID", type = "integer", example = "1")
    private Long id;

    /**
     * 消息标题
     */
    @Schema(description = "消息标题", type = "string", example = "系统维护通知")
    private String title;

    /**
     * 消息类型
     */
    @Schema(description = "消息类型", type = "string", example = "system")
    private MessageTypeEnum type;

    /**
     * 消息级别
     */
    @Schema(description = "消息级别", type = "string", example = "important")
    private MessageLevelEnum level;

    /**
     * 是否已读
     */
    @Schema(description = "是否已读", type = "integer", example = "0")
    private Integer isRead;

    /**
     * 发送者名称
     */
    @Schema(description = "发送者名称", type = "string", example = "系统管理员")
    private String senderName;

    /**
     * 概要内容
     */
    @Schema(description = "概要内容", type = "string", example = "尊敬的用户，系统将于今晚进行维护...")
    private String content;

    /**
     * 创建时间(发送时间)
     */
    @Schema(description = "创建时间(发送时间)", type = "string", example = "2025-08-10T10:00:00Z")
    private Date createTime;
}
