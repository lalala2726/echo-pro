package cn.zhangchuangla.message.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 系统消息表修改请求参数
 *
 * @author Chuang
 * created on 2025/5/25
 */
@Data
@Schema(description = "系统消息表修改请求参数")
public class SysMessageUpdateRequest {

    /**
     * 消息ID
     */
    @Schema(description = "消息ID")
    @NotNull(message = "消息ID不能为空")
    private Long id;

    /**
     * 消息标题
     */
    @Schema(description = "消息标题")
    @NotBlank(message = "消息标题不能为空")
    private String title;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容")
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 消息类型：1-系统消息 2-通知消息 3-公告消息
     */
    @Schema(description = "消息类型：1-系统消息 2-通知消息 3-公告消息")
    @NotNull(message = "消息类型：1-系统消息 2-通知消息 3-公告消息不能为空")
    private Integer type;

    /**
     * 消息级别：1-普通 2-重要 3-紧急
     */
    @Schema(description = "消息级别：1-普通 2-重要 3-紧急")
    @NotNull(message = "消息级别：1-普通 2-重要 3-紧急不能为空")
    private Integer level;

    /**
     * 发送者ID，NULL表示系统发送
     */
    @Schema(description = "发送者ID，NULL表示系统发送")
    private Long senderId;

    /**
     * 发送者姓名
     */
    @Schema(description = "发送者姓名")
    private String senderName;

    /**
     * 目标类型：1-指定用户 2-全部用户 3-角色用户
     */
    @Schema(description = "目标类型：1-指定用户 2-全部用户 3-角色用户")
    @NotNull(message = "目标类型：1-指定用户 2-全部用户 3-角色用户不能为空")
    private Integer targetType;

    /**
     * 目标用户ID列表，JSON格式，target_type=1时使用
     */
    @Schema(description = "目标用户ID列表，JSON格式，target_type=1时使用")
    private String targetIds;

    /**
     * 目标角色ID列表，JSON格式，target_type=3时使用
     */
    @Schema(description = "目标角色ID列表，JSON格式，target_type=3时使用")
    private String roleIds;

    /**
     * 推送方式：1-仅站内信 2-仅WebSocket 3-站内信+WebSocket
     */
    @Schema(description = "推送方式：1-仅站内信 2-仅WebSocket 3-站内信+WebSocket")
    @NotNull(message = "推送方式：1-仅站内信 2-仅WebSocket 3-站内信+WebSocket不能为空")
    private Integer pushType;

    /**
     * 是否已发布：0-未发布 1-已发布
     */
    @Schema(description = "是否已发布：0-未发布 1-已发布")
    @NotNull(message = "是否已发布：0-未发布 1-已发布不能为空")
    private Integer isPublished;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    private Date publishTime;

    /**
     * 定时发送时间，NULL表示立即发送
     */
    @Schema(description = "定时发送时间，NULL表示立即发送")
    private LocalDateTime scheduledTime;

    /**
     * 过期时间，NULL表示不过期
     */
    @Schema(description = "过期时间，NULL表示不过期")
    private LocalDateTime expireTime;

    /**
     * 是否删除：0-未删除 1-已删除
     */
    @Schema(description = "是否删除：0-未删除 1-已删除")
    @NotNull(message = "是否删除：0-未删除 1-已删除不能为空")
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @NotNull(message = "创建时间不能为空")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @NotNull(message = "更新时间不能为空")
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    private String updateBy;
}
