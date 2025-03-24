package cn.zhangchuangla.system.model.request.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * 字典值表
 */
@Data
@Schema(name = "添加字段项请求类")
public class AddDictionaryDataRequest {


    /**
     * 字典编码
     */
    @Schema(description = "字典ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典ID不能为空")
    @Min(value = 1L, message = "字典ID不能小于1")
    private Long dictionaryId;

    /**
     * 字典项键
     */
    @NotBlank(message = "字典项键不能为空")
    @Schema(description = "字典项键", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dataKey;

    /**
     * 字典项值
     */
    @NotBlank(message = "字典项值不能为空")
    @Schema(description = "字典项值", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dataValue;

    /**
     * 排序
     */
    @Min(value = 0, message = "排序不能小于0")
    @Schema(description = "排序", defaultValue = "0")
    @Min(value = 0, message = "排序不能小于0")
    private Integer sortOrder;

    /**
     * 状态 (0: 启用, 1: 禁用)
     */
    @Schema(description = "状态 (0: 启用, 1: 禁用)", defaultValue = "0")
    private String status;

}
