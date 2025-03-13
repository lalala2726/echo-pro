package cn.zhangchuangla.system.model.request.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 字典表
 */
@Data
public class UpdateDictionaryRequest {

    /**
     * 主键
     */
    @NotBlank(message = "主键不能为空")
    @Min(value = 1L, message = "主键不能小于1")
    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称")
    private String name;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
