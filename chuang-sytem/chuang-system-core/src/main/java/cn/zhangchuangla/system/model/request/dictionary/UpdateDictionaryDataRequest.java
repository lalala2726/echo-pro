package cn.zhangchuangla.system.model.request.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * 字典值表
 */
@Data
@Schema(name = "修改字典项请求类")
public class UpdateDictionaryDataRequest {

    /**
     * 字典名称
     */
    @Schema(description = "字典名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典名称不能为空")
    @Min(value = 1L, message = "字典ID不能小于1")
    private String dictName;

    /**
     * 字典项键
     */
    @Schema(description = "字典项键")
    private String dataKey;

    /**
     * 字典项值
     */
    @Schema(description = "字典项值")
    private String dataValue;

    /**
     * 排序
     */
    @Schema(description = "排序")
    @Min(value = 0, message = "排序不能小于0")
    private Integer sortOrder;

    /**
     * 状态 (0: 启用, 1: 禁用)
     */
    @Schema(description = "状态 (0: 启用, 1: 禁用)")
    private String status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

}
