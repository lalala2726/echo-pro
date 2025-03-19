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
@Schema(name = "字典值视图", description = "字典值视图")
public class DictionaryDataListVo extends BaseVO {

    /**
     * 主键
     */
    @Schema(description = "主键")
    private Long id;

    /**
     * 字典编码
     */
    @Schema(description = "字典ID")
    private Long dictionaryId;

    /**
     * 字典项键
     */
    @Schema(description = "字典项键")
    private String dataKey;

    /**
     * 字典项值
     */
    @Schema(description = "字典项值")
    private String dataValue;


    /**
     * 状态 (0: 启用, 1: 禁用)
     */
    @Schema(description = "状态(0: 启用, 1: 禁用)")
    private Integer status;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

}
