package cn.zhangchuangla.message.model.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 系统消息表视图对象
 *
 * @author Chuang
 * created on 2025/5/25
 */
@Data
@Schema(description = "系统消息表视图对象")
public class SysMessageVo {

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
     * 目标接受者ID列表
     */
    private List<Long> targetIds;


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
     * 是否已发布：0-未发布 1-已发布
     */
    @Schema(description = "是否已发布：0-未发布 1-已发布")
    private Integer isPublished;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    private Date publishTime;

    /**
     * 过期时间，NULL表示不过期
     */
    @Schema(description = "过期时间，NULL表示不过期")
    private LocalDateTime expireTime;

}
