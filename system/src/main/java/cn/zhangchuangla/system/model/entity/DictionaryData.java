package cn.zhangchuangla.system.model.entity;

import cn.zhangchuangla.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典值表
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "dictionary_data")
@Data
@Schema(name = "字典值表", description = "字典值表")
public class DictionaryData extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
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
    private String dataKey;

    /**
     * 字典项值
     */
    @Schema(description = "字典项值")
    private String dataValue;

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

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

}
