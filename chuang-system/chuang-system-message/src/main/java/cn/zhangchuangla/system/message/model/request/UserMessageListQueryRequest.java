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
    @Schema(description = "消息标题")
    private String title;

    /**
     * 消息类型：1-系统消息 2-通知消息 3-公告消息
     */
    @Schema(description = "消息类型：1-系统消息 2-通知消息 3-公告消息")
    private Integer type;

    /**
     * 是否查询我发送的消息
     * true：仅查询我自己发送的消息
     * false 或 null：查询我接收到的消息
     */
    @Schema(description = "是否查询我发送的消息（true：我发送的；false：接收的）")
    private Boolean sentByMyself;

    /**
     * 消息级别：1-普通 2-重要 3-紧急
     */
    @Schema(description = "消息级别：1-普通 2-重要 3-紧急")
    private Integer level;

    /**
     * 是否已读
     */
    @Schema(description = "是否已读")
    private Boolean isRead;


    /**
     * 发送者姓名
     */
    @Schema(description = "发送者姓名")
    private String senderName;


}
