package cn.zhangchuangla.system.message.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 系统消息表添加请求参数
 *
 * @author Chuang
 * created on 2025/5/25
 */
@Data
@Schema(description = "系统消息表添加请求参数")
public class SysMessageAddRequest {

    /**
     * 消息标题
     */
    @Schema(description = "消息标题")
    private String title;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容")
    private String content;

    /**
     * 消息类型：1-系统消息 2-通知消息 3-公告消息
     */
    @Schema(description = "消息类型：1-系统消息 2-通知消息 3-公告消息")
    private Integer type;

    /**
     * 消息级别：1-普通 2-重要 3-紧急
     */
    @Schema(description = "消息级别：1-普通 2-重要 3-紧急")
    private Integer level;

    /**
     * 发送者姓名
     */
    @Schema(description = "发送者姓名")
    private String senderName;

    /**
     * 目标类型：1-指定用户 2-全部用户 3-角色用户
     */
    @Schema(description = "目标类型：1-指定用户 2-全部用户 3-角色用户")
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
     * 发布时间
     */
    @Schema(description = "发布时间")
    private Date publishTime;

    /**
     * 是否删除：0-未删除 1-已删除
     */
    @Schema(description = "是否删除：0-未删除 1-已删除")
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
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
