package cn.zhangchuangla.system.model.vo.dictionary;

import cn.zhangchuangla.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典值表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "字典项详细视图")
public class DictionaryItemVo extends BaseVO {

    /**
     * 主键
     */
    @Schema(description = "主键")
    private Long id;


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
