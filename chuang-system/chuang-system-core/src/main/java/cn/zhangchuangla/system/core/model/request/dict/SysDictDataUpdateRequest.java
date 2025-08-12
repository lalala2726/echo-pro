package cn.zhangchuangla.system.core.model.request.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * 字典数据更新请求对象
 *
 * @author Chuang
 */
@Data
@Schema(name = "字典数据更新请求对象", description = "字典数据更新请求对象")
public class SysDictDataUpdateRequest {

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
    @Schema(description = "字典类型", example = "user_status", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictType;

    /**
     * 字典标签
     */
    @NotBlank(message = "字典标签不能为空")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    @Schema(description = "字典标签", example = "启用", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictLabel;

    /**
     * 字典值
     */
    @NotBlank(message = "字典值不能为空")
    @Size(max = 100, message = "字典值长度不能超过100个字符")
    @Schema(description = "字典值", example = "0", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictValue;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1", type = "integer")
    private Integer sort = 0;

    /**
     * 状态：1启用，0禁用
     */
    @Schema(description = "状态：1启用，0禁用", example = "1", type = "integer", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    /**
     * 备注
     */
    @Size(max = 255, message = "备注长度不能超过255个字符")
    @Schema(description = "备注", example = "启用状态", type = "string")
    private String remark;
}
