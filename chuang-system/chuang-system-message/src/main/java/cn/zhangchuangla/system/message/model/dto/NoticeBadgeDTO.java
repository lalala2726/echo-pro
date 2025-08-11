package cn.zhangchuangla.system.message.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户消息徽标数量通知数据传输对象。
 *
 * @author Chuang
 */
@Schema(description = "用户消息徽标数量通知")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeBadgeDTO {

    /**
     * 未读数量
     */
    @Schema(description = "未读数量")
    private long unread;

    /**
     * 已读数量
     */
    @Schema(description = "已读数量")
    private long read;
}


