package cn.zhangchuangla.system.model.request.dictionary;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典值表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "字典值查询参数", description = "字典值查询参数请求类")
public class DictionaryDataRequest extends BasePageRequest {

    /**
     * 主键
     */
    @Schema(description = "主键")
    private Long id;

    /**
     * 字典编码
     */
    @Schema(description = "字典编码")
    private Long dictionaryId;

    /**
     * 字典项键
     */
    @Schema(description = "字典项键")
    private String itemKey;

    /**
     * 字典项值
     */
    @Schema(description = "字典项值")
    private String itemValue;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;

    /**
     * 状态 (0: 启用, 1: 禁用)
     */
    @Schema(description = "状态 (0: 启用, 1: 禁用)")
    private Integer status;

}
