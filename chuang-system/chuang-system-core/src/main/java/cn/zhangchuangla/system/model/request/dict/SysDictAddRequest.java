package cn.zhangchuangla.system.model.request.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 字典添加请求类
 */
@Data
@Schema(description = "字典添加请求类")
public class SysDictAddRequest {


    /**
     * 类型编码
     */
    @Schema(description = "类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "字典编码不能为空")
    private String dictCode;

    /**
     * 类型名称
     */
    @Schema(description = "类型名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "system")
    @NotBlank(message = "字典类型不能为空")
    private String name;

    /**
     * 状态(0:正常;1:禁用)
     */
    @Schema(description = "状态(0:正常;1:禁用)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @Max(value = 1, message = "状态只能为0或1")
    @Min(value = 0, message = "状态只能为0或1")
    @NotBlank(message = "状态不能为空")
    private Integer status;

}
