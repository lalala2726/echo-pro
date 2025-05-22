package cn.zhangchuangla.system.model.entity;

import cn.zhangchuangla.common.core.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门表
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_dept")
@Data
public class SysDept extends BaseEntity {

    /**
     * 部门ID
     */
    @TableId(type = IdType.AUTO)
    private Long deptId;

    /**
     * 状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门负责人
     */
    private String manager;

    /**
     * 部门描述
     */
    private String description;


}
