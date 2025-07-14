package cn.zhangchuangla.message.model.vo.user;

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
    @Schema(description = "消息ID")
    private Long id;

    /**
     * 消息标题
     */
    @Schema(description = "消息标题")
    private String title;

    /**
     * 消息类型
     */
    @Schema(description = "消息类型")
    private Integer type;

    /**
     * 消息级别
     */
    @Schema(description = "消息级别")
    private Integer level;

    /**
     * 是否已读
     */
    @Schema(description = "是否已读")
    private Integer isRead;

    /**
     * 发送者名称
     */
    @Schema(description = "发送者名称")
    private String senderName;

    /**
     * 概要内容
     */
    @Schema(description = "概要内容")
    private String content;

    /**
     * 创建时间(发送时间)
     */
    @Schema(description = "创建时间(发送时间)")
    private Date createTime;
}
