package cn.zhangchuangla.system.core.model.entity;

import cn.zhangchuangla.common.core.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据实体类
 *
 * @author Chuang
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_dict_data")
public class SysDictData extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 字典类型（关联字典类型表dict_type）
     */
    private String dictType;

    /**
     * 字典标签（中文显示）
     */
    private String dictLabel;

    /**
     * 字典值（业务使用的值）
     */
    private String dictValue;

    /**
     * 排序（越小越前）
     */
    private Integer sort;

    /**
     * 状态（1=启用，0=禁用）
     */
    private Integer status;
}
