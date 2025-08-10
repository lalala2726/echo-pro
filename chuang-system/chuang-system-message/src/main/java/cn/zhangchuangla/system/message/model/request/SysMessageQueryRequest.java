package cn.zhangchuangla.system.message.model.request;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import cn.zhangchuangla.common.core.entity.base.TimeRange;
import cn.zhangchuangla.system.message.enums.MessageLevelEnum;
import cn.zhangchuangla.system.message.enums.MessageSendMethodEnum;
import cn.zhangchuangla.system.message.enums.MessageTypeEnum;
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
     * 消息类型
     */
    @Schema(description = "消息类型", allowableValues = {"system", "notice", "announcement"})
    private MessageTypeEnum type;

    /**
     * 消息级别
     */
    @Schema(description = "消息级别", allowableValues = {"normal", "important", "urgent"})
    private MessageLevelEnum level;

    /**
     * 发送者姓名
     */
    @Schema(description = "发送者姓名")
    private String senderName;

    /**
     * 目标类型
     */
    @Schema(description = "目标类型", allowableValues = {"user", "role", "dept", "all"})
    private MessageSendMethodEnum targetType;

    /**
     * 发布时间范围
     */
    @Schema(description = "时间范围")
    private TimeRange publishTimeRange;

}
