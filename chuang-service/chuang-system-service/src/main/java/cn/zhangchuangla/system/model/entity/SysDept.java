package cn.zhangchuangla.system.model.entity;

import cn.zhangchuangla.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门表
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_dept")
@Data
public class SysDept extends BaseEntity {
    /**
     * 部门ID
     */
    @TableId(type = IdType.AUTO)
    private Integer deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 父部门ID
     */
    private Integer parentId;

    /**
     * 部门负责人
     */
    private Integer managerId;

    /**
     * 部门描述
     */
    private String description;


}
