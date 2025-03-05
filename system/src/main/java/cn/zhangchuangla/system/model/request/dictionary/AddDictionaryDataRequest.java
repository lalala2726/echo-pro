package cn.zhangchuangla.system.model.request.dictionary;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典值表
 */
@Data
@Schema(name = "添加字段项请求类")
public class AddDictionaryDataRequest {


    /**
     * 字典编码
     */
    @Schema(description = "字典ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long dictionaryId;

    /**
     * 字典项键
     */
    @Schema(description = "字典项键", requiredMode = Schema.RequiredMode.REQUIRED)
    private String itemKey;

    /**
     * 字典项值
     */
    @Schema(description = "字典项值", requiredMode = Schema.RequiredMode.REQUIRED)
    private String itemValue;

    /**
     * 排序
     */
    @Schema(description = "排序",defaultValue = "0")
    private Integer sortOrder;

    /**
     * 状态 (0: 启用, 1: 禁用)
     */
    @Schema(description = "状态 (0: 启用, 1: 禁用)",defaultValue = "0")
    private Integer status;

}
