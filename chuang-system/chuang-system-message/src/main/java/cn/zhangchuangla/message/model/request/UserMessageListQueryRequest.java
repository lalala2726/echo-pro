package cn.zhangchuangla.message.model.request;

import cn.zhangchuangla.common.core.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

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
    @Schema(description = "消息类型：1-系统消息 2-通知消息 3-公告消息,4-已发送消息")
    private List<Long> type;

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

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    private LocalDateTime publishTime;


}
