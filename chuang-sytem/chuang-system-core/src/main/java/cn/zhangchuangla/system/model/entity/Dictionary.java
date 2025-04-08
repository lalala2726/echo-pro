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
@TableName(value = "sys_dictionary")
@Data
public class Dictionary extends BaseEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 字典名称
     */
    private String name;

    /**
     * 字典状态
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;
}
