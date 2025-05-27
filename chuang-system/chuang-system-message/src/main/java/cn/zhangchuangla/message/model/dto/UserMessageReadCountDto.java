package cn.zhangchuangla.message.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户消息读读取量
 *
 * @author Chuang
 * <p>
 * created on 2025/5/26 16:00
 */
@Data
@AllArgsConstructor
public class UserMessageReadCountDto {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 已读数量
     */
    @Schema(description = "已读数量")
    private long read;

    /**
     * 未读数量
     */
    @Schema(description = "未读数量")
    private long unRead;
}
