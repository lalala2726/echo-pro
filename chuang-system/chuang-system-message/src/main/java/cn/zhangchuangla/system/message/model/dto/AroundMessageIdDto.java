package cn.zhangchuangla.system.message.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 上一条/下一条消息ID 承载对象
 *
 * @author Chuang
 */
@Data
@Schema(description = "上一条和下一条消息ID")
public class AroundMessageIdDto {
    @Schema(description = "上一条消息ID")
    private Long prevId;

    @Schema(description = "下一条消息ID")
    private Long nextId;
}


