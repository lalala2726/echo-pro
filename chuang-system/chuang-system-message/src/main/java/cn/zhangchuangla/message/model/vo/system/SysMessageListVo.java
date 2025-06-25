package cn.zhangchuangla.message.model.vo.system;

import cn.zhangchuangla.common.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 系统消息表列表视图对象
 *
 * @author Chuang
 * created on 2025/5/25
 */
@Data
@Schema(description = "系统消息表列表视图对象")
public class SysMessageListVo {

    /**
     * 消息ID
     */
    @Schema(description = "消息ID")
    @Excel(name = "消息ID", sort = 0)
    private Long id;

    /**
     * 消息标题
     */
    @Schema(description = "消息标题")
    @Excel(name = "消息标题", sort = 1)
    private String title;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容")
    @Excel(name = "消息内容", sort = 2)
    private String content;

    /**
     * 消息类型：1-系统消息 2-通知消息 3-公告消息
     */
    @Schema(description = "消息类型：1-系统消息 2-通知消息 3-公告消息")
    @Excel(name = "消息类型：1-系统消息 2-通知消息 3-公告消息", sort = 3)
    private Integer type;

    /**
     * 消息级别：1-普通 2-重要 3-紧急
     */
    @Schema(description = "消息级别：1-普通 2-重要 3-紧急")
    @Excel(name = "消息级别：1-普通 2-重要 3-紧急", sort = 4)
    private Integer level;


    /**
     * 发送者姓名
     */
    @Schema(description = "发送者姓名")
    @Excel(name = "发送者姓名", sort = 6)
    private String senderName;

    /**
     * 目标类型：1-指定用户 2-全部用户 3-角色用户
     */
    @Schema(description = "目标类型：1-指定用户 2-全部用户 3-角色用户")
    @Excel(name = "目标类型：1-指定用户 2-全部用户 3-角色用户", sort = 7)
    private Integer targetType;

    /**
     * 目标用户ID列表，JSON格式，target_type=1时使用
     */
    @Schema(description = "目标用户ID列表，JSON格式，target_type=1时使用")
    @Excel(name = "目标用户ID列表，JSON格式，target_type=1时使用", sort = 8)
    private String targetIds;

    /**
     * 目标角色ID列表，JSON格式，target_type=3时使用
     */
    @Schema(description = "目标角色ID列表，JSON格式，target_type=3时使用")
    @Excel(name = "目标角色ID列表，JSON格式，target_type=3时使用", sort = 9)
    private String roleIds;

    /**
     * 推送方式：1-仅站内信 2-仅WebSocket 3-站内信+WebSocket
     */
    @Schema(description = "推送方式：1-仅站内信 2-仅WebSocket 3-站内信+WebSocket")
    @Excel(name = "推送方式：1-仅站内信 2-仅WebSocket 3-站内信+WebSocket", sort = 10)
    private Integer pushType;

    /**
     * 是否已发布：0-未发布 1-已发布
     */
    @Schema(description = "是否已发布：0-未发布 1-已发布")
    @Excel(name = "是否已发布：0-未发布 1-已发布", sort = 11)
    private Integer isPublished;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    @Excel(name = "发布时间", sort = 12)
    private Date publishTime;

    /**
     * 定时发送时间，NULL表示立即发送
     */
    @Schema(description = "定时发送时间，NULL表示立即发送")
    @Excel(name = "定时发送时间，NULL表示立即发送", sort = 13)
    private LocalDateTime scheduledTime;

    /**
     * 过期时间，NULL表示不过期
     */
    @Schema(description = "过期时间，NULL表示不过期")
    @Excel(name = "过期时间，NULL表示不过期", sort = 14)
    private LocalDateTime expireTime;

    /**
     * 是否删除：0-未删除 1-已删除
     */
    @Schema(description = "是否删除：0-未删除 1-已删除")
    @Excel(name = "是否删除：0-未删除 1-已删除", sort = 15)
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @Excel(name = "创建时间", sort = 16)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @Excel(name = "更新时间", sort = 17)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    @Excel(name = "创建人", sort = 18)
    private String createBy;

    /**
     * 更新人
     */
    @Schema(description = "更新人")
    @Excel(name = "更新人", sort = 19)
    private String updateBy;
}
