package cn.zhangchuangla.system.message.model.request;

import cn.zhangchuangla.system.message.enums.MessageLevelEnum;
import cn.zhangchuangla.system.message.enums.MessageTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/24 22:47
 */
@Data
@Schema(name = "消息", description = "发送消息请求参数")
public class MessageRequest {

    /**
     * 消息标题
     */
    @Schema(description = "消息标题", type = "string", example = "系统消息")
    @NotBlank(message = "消息标题不能为空")
    private String title;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容", type = "string", example = "消息内容示例")
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 消息类型
     */
    @Schema(description = "消息类型", allowableValues = {"system", "notice", "announcement"}, example = "system")
    @NotNull(message = "消息类型不能为空")
    private MessageTypeEnum type;

    /**
     * 消息级别
     */
    @Schema(description = "消息级别", allowableValues = {"normal", "important", "urgent"}, example = "normal")
    @NotNull(message = "消息级别不能为空")
    private MessageLevelEnum level;

}
