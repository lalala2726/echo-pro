package cn.zhangchuangla.system.model.entity;

import cn.zhangchuangla.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典表
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_dict")
@Data
public class SysDict extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 类型编码
     */
    private String dictCode;

    /**
     * 类型名称
     */
    private String name;

    /**
     * 状态(0:正常;1:禁用)
     */
    private Integer status;


    /**
     * 是否删除(1-删除，0-未删除)
     */
    private Integer isDeleted;
}
