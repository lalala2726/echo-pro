package cn.zhangchuangla.system.message.model.request;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import cn.zhangchuangla.common.core.entity.base.TimeRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统消息表查询请求参数
 *
 * @author Chuang
 * created on 2025/5/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统消息表查询请求参数")
public class SysMessageQueryRequest extends BasePageRequest {

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
     * 时间范围
     */
    @Schema(description = "时间范围")
    private TimeRange timeRange;

}
