package cn.zhangchuangla.system.message.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/26 21:09
 */
@Schema(name = "用户发送消息请求对象", description = "用于普通用户发送消息的请求对象")
@Data
public class UserSendMessageRequest {

    /**
     * 标题
     */
    @Schema(description = "消息标题", type = "string", example = "开发进度")
    @NotNull(message = "标题不能为空")
    private String title;

    /**
     * 内容
     */
    @Schema(description = "消息内容", type = "string", example = "消息内容示例")
    @NotNull(message = "内容不能为空")
    private String content;

    /**
     * 接收者ID列表
     */
    @Schema(description = "接收者ID列表", type = "array<int64>", example = "[1,2,3]")
    @NotNull(message = "接收者ID列表不能为空")
    private List<Long> receiveId;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息", type = "string", example = "用户名或密码错误")
    private String errorMessage;

}
