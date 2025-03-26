package cn.zhangchuangla.message.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/26 22:12
 */
@Schema(description = "站内信请求参数")
@Data
public class SiteMessageRequest {

    /**
     * 接受人
     */
    @Schema(description = "接受人", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "接受人列表不能为空")
    @NotNull(message = "接受人列表不能为空")
    private List<Long> userId;

    /**
     * 标题
     */
    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "标题不能为空")
    @Size(max = 50, message = "标题长度不能超过50")
    private String title;

    /**
     * 内容
     */
    @Schema(description = "内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "内容不能为空")
    @Size(max = 500, message = "内容长度不能超过500")
    private String content;

    /**
     * 消息类型
     */
    @Schema(description = "消息类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "消息类型不能为空")
    private String messageType;
}
