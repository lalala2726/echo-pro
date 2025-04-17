package cn.zhangchuangla.system.model.entity;

import cn.zhangchuangla.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典项表
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_dict_item")
@Data
public class SysDictItem extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联字典编码，与sys_dict表中的dict_code对应
     */
    private String dictCode;

    /**
     * 字典项值
     */
    private String value;

    /**
     * 字典项标签
     */
    private String label;

    /**
     * 标签类型，用于前端样式展示（如success、warning等）
     */
    private String tagType;

    /**
     * 状态（1-正常，0-禁用）
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

}
