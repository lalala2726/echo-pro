package cn.zhangchuangla.system.model.request.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 字典项添加请求类
 */
@Data
@Schema(description = "字典项添加请求类")
public class SysDictItemAddRequest {

    /**
     * 关联字典编码
     */
    @Schema(description = "关联字典编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dictCode;

    /**
     * 字典项值
     */
    @Schema(description = "字典项值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典项不能为空")
    private String value;

    /**
     * 字典项标签
     */
    @Schema(description = "字典项标签", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典项标签不能为空")
    private String label;

    /**
     * 标签类型
     */
    @Schema(description = "标签类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "标签类型不能为空")
    private String tagType;

    /**
     * 状态（1-正常，0-禁用）
     */
    @Schema(description = "状态（1-正常，0-禁用）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "状态不能为空")
    @Max(value = 1, message = "状态值只能是0或1")
    @Min(value = 0, message = "状态值只能是0或1")
    private Integer status;

    /**
     * 排序
     */
    @Schema(description = "排序", requiredMode = Schema.RequiredMode.AUTO)
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sort;

}
