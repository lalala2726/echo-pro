package cn.zhangchuangla.system.model.vo.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 字典项表
 */
@Data
public class SysDictItemListVo {

    /**
     * 主键
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 关联字典编码，与sys_dict表中的dict_code对应
     */
    @Schema(description = "关联字典编码")
    private String dictCode;

    /**
     * 字典项值
     */
    @Schema(description = "字典项值")
    private String value;

    /**
     * 字典项标签
     */
    @Schema(description = "字典项标签")
    private String label;

    /**
     * 标签类型，用于前端样式展示（如success、warning等）
     */
    @Schema(description = "标签类型")
    private String tagType;

    /**
     * 状态（1-正常，0-禁用）
     */
    @Schema(description = "状态（1-正常，0-禁用）")
    private Integer status;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;


}
