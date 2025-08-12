package cn.zhangchuangla.system.message.model.request;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import cn.zhangchuangla.common.core.entity.base.TimeRange;
import cn.zhangchuangla.system.message.enums.MessageLevelEnum;
import cn.zhangchuangla.system.message.enums.MessageReceiveTypeEnum;
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
    @Schema(description = "消息标题", type = "string", example = "系统维护通知")
    private String title;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容", type = "string", example = "尊敬的用户，系统将在今晚进行维护...")
    private String content;

    /**
     * 消息类型
     */
    @Schema(description = "消息类型", type = "string", allowableValues = {"system", "notice", "announcement"}, example = "notice")
    private MessageTypeEnum type;

    /**
     * 消息级别
     */
    @Schema(description = "消息级别", type = "string", allowableValues = {"normal", "important", "urgent"}, example = "important")
    private MessageLevelEnum level;

    /**
     * 发送者姓名
     */
    @Schema(description = "发送者姓名", type = "string", example = "管理员")
    private String senderName;

    /**
     * 目标类型
     */
    @Schema(description = "目标类型", type = "string", allowableValues = {"user", "role", "dept", "all"}, example = "all")
    private MessageReceiveTypeEnum targetType;

    /**
     * 发布时间范围
     */
    @Schema(description = "发布时间范围")
    private TimeRange publishTimeRange;

}
