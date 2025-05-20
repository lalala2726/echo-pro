package cn.zhangchuangla.generator.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 代码生成业务表状态更新请求
 *
 * @author zhangchuang
 */
@Data
@Schema(name = "代码生成业务表状态更新请求", description = "用于更新低代码业务表状态")
public class GenTableStatusUpdateRequest {

    /**
     * 编号
     */
    @Schema(description = "编号", type = "int", format = "int64", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "编号不能为空")
    private Long tableId;

    /**
     * 状态（0正常 1停用）
     */
    @Schema(description = "状态（0正常 1停用）", type = "string", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态不能为空")
    private String status;
}
