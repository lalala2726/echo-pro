package cn.zhangchuangla.system.core.model.request.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * 字典类型更新请求对象
 *
 * @author Chuang
 */
@Data
@Schema(name = "字典类型更新请求对象", description = "字典类型更新请求对象")
public class SysDictTypeUpdateRequest {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空")
    @Schema(description = "主键ID", example = "1", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 字典类型
     */
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    @Schema(description = "字典类型", example = "user_status", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dictType;

    /**
     * 字典名称
     */
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典名称长度不能超过100个字符")
    @Schema(description = "字典名称", example = "用户状态", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dictName;

    /**
     * 状态：0启用，1禁用
     */
    @NotNull(message = "状态不能为空")
    @Schema(description = "状态：0启用，1禁用", example = "0", type = "integer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer status;

    /**
     * 备注
     */
    @Size(max = 255, message = "备注长度不能超过255个字符")
    @Schema(description = "备注", example = "用户状态字典", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;
}
