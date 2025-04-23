package cn.zhangchuangla.system.model.request.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 字典更新请求类
 */
@Data
@Schema(description = "字典更新请求类")
public class SysDictUpdateRequest {

    /**
     * 主键
     */
    @Schema(description = "主键", type = "string", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "主键ID不能小于1")
    private Long id;

    /**
     * 类型编码
     */
    @Schema(description = "类型编码", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 100, message = "类型编码长度不能超过100")
    private String dictCode;

    /**
     * 类型名称
     */
    @Schema(description = "类型名称", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 100, message = "类型名称长度不能超过100")
    private String name;

    /**
     * 状态(0:正常;1:禁用)
     */
    @Schema(description = "状态(0:正常;1:禁用)", type = "integer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Range(min = 0, max = 1, message = "状态只能为0或1")
    private Integer status;

}
