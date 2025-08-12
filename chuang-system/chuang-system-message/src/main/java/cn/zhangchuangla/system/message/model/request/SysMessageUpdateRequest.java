package cn.zhangchuangla.system.message.model.request;

import cn.zhangchuangla.system.message.enums.MessageLevelEnum;
import cn.zhangchuangla.system.message.enums.MessageTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 系统消息表修改请求参数
 *
 * @author Chuang
 * created on 2025/5/25
 */
@Data
@Schema(description = "系统消息表修改请求参数")
public class SysMessageUpdateRequest {

    /**
     * 消息ID
     */
    @Schema(description = "消息ID", type = "long", example = "1")
    @NotNull(message = "消息ID不能为空")
    private Long id;

    /**
     * 消息标题
     */
    @Schema(description = "消息标题", type = "string", example = "系统维护通知")
    @NotBlank(message = "消息标题不能为空")
    private String title;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容", type = "string", example = "系统将在今晚进行维护，请提前保存数据。")
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 消息类型
     */
    @Schema(description = "消息类型", type = "string", example = "system", allowableValues = {"system", "notice", "announcement"})
    @NotNull(message = "消息类型不能为空")
    private MessageTypeEnum type;

    /**
     * 消息级别
     */
    @Schema(description = "消息级别", type = "string", example = "important", allowableValues = {"normal", "important", "urgent"})
    @NotNull(message = "消息级别不能为空")
    private MessageLevelEnum level;
}
