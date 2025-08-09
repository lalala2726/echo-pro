package cn.zhangchuangla.system.message.model.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 用户消息状态视图对象
 */
@Data
@Schema(description = "用户消息状态视图对象")
public class UserMessageStatusVo {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "消息ID")
    private Long messageId;

    @Schema(description = "是否已读：0-未读 1-已读")
    private Integer isRead;

    @Schema(description = "首次阅读时间（真实阅读）")
    private Date firstReadTime;

    @Schema(description = "最后阅读时间（真实阅读）")
    private Date lastReadTime;
}


