package cn.zhangchuangla.system.model.request.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * 字典类型添加请求对象
 *
 * @author Chuang
 */
@Data
@Schema(name = "字典类型添加请求对象", description = "字典类型添加请求对象")
public class SysDictTypeAddRequest {

    /**
     * 字典类型
     */
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型长度不能超过100个字符")
    @Schema(description = "字典类型", example = "user_status", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictType;

    /**
     * 字典名称
     */
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典名称长度不能超过100个字符")
    @Schema(description = "字典名称", example = "用户状态", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictName;

    /**
     * 状态：0启用，1禁用
     */
    @Schema(description = "状态：0启用，1禁用", example = "0", type = "integer", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    /**
     * 备注
     */
    @Size(max = 255, message = "备注长度不能超过255个字符")
    @Schema(description = "备注", example = "用户状态字典", type = "string")
    private String remark;
}
