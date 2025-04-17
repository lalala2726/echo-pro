package cn.zhangchuangla.system.model.request.dict;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "字典项列表请求类")
public class SysDictItemListRequest extends BasePageRequest {

    /**
     * 主键
     */
    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Min(value = 1, message = "主键ID不能小于1")
    private Long id;

    /**
     * 关联字典编码，与sys_dict表中的dict_code对应
     */
    @Schema(description = "关联字典编码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 100, message = "字典编码长度不能超过100个字符")
    private String dictCode;

    /**
     * 字典项值
     */
    @Schema(description = "字典项值", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 100, message = "字典项值长度不能超过100个字符")
    private String value;

    /**
     * 字典项标签
     */
    @Schema(description = "字典项标签", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 100, message = "字典项标签长度不能超过100个字符")
    private String label;

    /**
     * 标签类型，用于前端样式展示（如success、warning等）
     */
    @Schema(description = "标签类型", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String tagType;

    /**
     * 状态（1-正常，0-禁用）
     */
    @Schema(description = "状态（1-正常，0-禁用）", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer status;

    /**
     * 排序
     */
    @Schema(description = "排序", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer sort;


}
