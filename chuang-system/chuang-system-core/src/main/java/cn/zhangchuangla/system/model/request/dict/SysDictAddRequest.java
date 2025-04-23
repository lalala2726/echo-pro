package cn.zhangchuangla.system.model.request.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 字典添加请求类
 */
@Data
@Schema(description = "字典添加请求类")
public class SysDictAddRequest {


    /**
     * 类型编码
     */
    @Schema(description = "类型编码", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "字典编码不能为空")
    private String dictCode;

    /**
     * 类型名称
     */
    @Schema(description = "类型名称", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "system")
    @NotBlank(message = "字典类型不能为空")
    private String name;

    /**
     * 状态(0:正常;1:禁用)
     */
    @Schema(description = "状态(0:正常;1:禁用)", type = "integer", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @Range(min = 0, max = 1, message = "状态只能为0或1")
    private Integer status;

}
