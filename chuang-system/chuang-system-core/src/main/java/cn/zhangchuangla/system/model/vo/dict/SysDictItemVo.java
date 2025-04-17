package cn.zhangchuangla.system.model.vo.dict;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 字典项表
 */
@Data
@Schema(description = "字典项视图对象")
public class SysDictItemVo {

    /**
     * 主键
     */
    @Schema(description = "主键")
    private Long id;

    /**
     * 关联字典编码
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
     * 标签类型
     */
    @Schema(description = "标签类型")
    private String tagType;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;

    /**
     * 创建时间
     */
    @Schema(name = "创建时间")
    private Date createTime;

    /**
     * 删除时间
     */
    @Schema(name = "删除时间")
    private Date updateTime;

    /**
     * 创建人
     */
    @Schema(name = "创建人")
    private String createBy;

    /**
     * 修改人
     */
    @Schema(name = "修改人")
    private String updateBy;

    /**
     * 备注
     */
    @Schema(name = "备注")
    private String remark;

}
