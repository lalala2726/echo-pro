package cn.zhangchuangla.system.message.model.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 用户消息状态视图对象
 *
 * @author Chuang
 */
@Data
@Schema(description = "用户消息状态视图对象")
public class UserMessageStatusVo {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", type = "long", example = "1")
    private Long userId;

    /**
     * 消息ID
     */
    @Schema(description = "消息ID", type = "long", example = "1")
    private Long messageId;

    /**
     * 是否已读：0-未读 1-已读
     */
    @Schema(description = "是否已读：0-未读 1-已读", type = "integer", example = "0")
    private Integer isRead;

    /**
     * 首次阅读时间
     */
    @Schema(description = "首次阅读时间", type = "string", example = "2023-04-01T08:30:00Z")
    private Date firstReadTime;

    /**
     * 最后阅读时间
     */
    @Schema(description = "最后阅读时间", type = "string", example = "2023-04-01T08:30:00Z")
    private Date lastReadTime;
}


