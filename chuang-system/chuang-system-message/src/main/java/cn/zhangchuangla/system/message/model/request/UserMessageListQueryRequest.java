package cn.zhangchuangla.system.message.model.request;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
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
    @Schema(description = "消息标题", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "系统消息")
    private String title;

    /**
     * 消息类型：1-系统消息 2-通知消息 3-公告消息
     */
    @Schema(description = "消息类型：1-系统消息 2-通知消息 3-公告消息", type = "integer", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1")
    private Integer type;


    /**
     * 消息级别：1-普通 2-重要 3-紧急
     */
    @Schema(description = "消息级别：1-普通 2-重要 3-紧急", type = "integer", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1")
    private Integer level;

    /**
     * 是否已读
     */
    @Schema(description = "是否已读", type = "boolean", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "true")
    private Boolean isRead;


    /**
     * 发送者姓名
     */
    @Schema(description = "发送者姓名", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "张三")
    private String senderName;


}
