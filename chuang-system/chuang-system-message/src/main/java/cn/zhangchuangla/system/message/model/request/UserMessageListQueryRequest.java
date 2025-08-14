package cn.zhangchuangla.system.message.model.request;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import cn.zhangchuangla.system.message.enums.MessageLevelEnum;
import cn.zhangchuangla.system.message.enums.MessageTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/25 22:44
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "用户消息列表查询参数", description = "用于用户查询自己的消息时候的查询条件")
public class UserMessageListQueryRequest extends BasePageRequest {

    /**
     * 消息标题
     */
    @Schema(description = "消息标题", type = "string", example = "系统消息")
    private String title;

    /**
     * 消息类型
     */
    @Schema(description = "消息类型", allowableValues = {"system", "notice", "announcement"}, example = "system")
    private MessageTypeEnum type;


    /**
     * 消息级别
     */
    @Schema(description = "消息级别", allowableValues = {"normal", "important", "urgent"}, example = "normal")
    private MessageLevelEnum level;

    /**
     * 是否已读
     */
    @Schema(description = "是否已读", type = "boolean", example = "true")
    private Boolean isRead;


    /**
     * 发送者姓名
     */
    @Schema(description = "发送者姓名", type = "string", example = "张三")
    private String senderName;


}
