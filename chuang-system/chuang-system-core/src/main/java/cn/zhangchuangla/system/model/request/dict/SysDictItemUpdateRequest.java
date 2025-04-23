package cn.zhangchuangla.system.model.request.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 字典项表
 */
@Data
@Schema(description = "字典项更新请求类")
public class SysDictItemUpdateRequest {

    /**
     * 主键
     */
    @Schema(description = "主键", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1L, message = "id不能小于1")
    private Long id;

    /**
     * 关联字典编码，与sys_dict表中的dict_code对应
     */
    @Schema(description = "关联字典编码，与sys_dict表中的dict_code对应", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 100, message = "字典编码长度不能超过100")
    private String dictCode;

    /**
     * 字典项值
     */
    @Schema(description = "字典项值", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 100, message = "字典项值长度不能超过100")
    private String value;

    /**
     * 字典项标签
     */
    @Schema(description = "字典项标签", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 100, message = "字典项标签长度不能超过100")
    private String label;

    /**
     * 标签类型，用于前端样式展示（如success、warning等）
     */
    @Schema(description = "标签类型，用于前端样式展示（如success、warning等）", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 100, message = "标签类型长度不能超过100")
    private String tagType;

    /**
     * 状态（1-正常，0-禁用）
     */
    @Schema(description = "状态（1-正常，0-禁用）", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Range(min = 0, max = 1, message = "状态只能为0或1")
    private Integer status;

    /**
     * 排序
     */
    @Schema(description = "排序", type = "integer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Min(value = 0, message = "排序不能小于0")
    private Integer sort;

}
