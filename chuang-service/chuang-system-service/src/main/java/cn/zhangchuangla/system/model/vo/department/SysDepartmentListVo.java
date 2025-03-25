package cn.zhangchuangla.system.model.vo.department;

import lombok.Data;

/**
 * 部门表
 */
@Data
public class SysDepartmentListVo {

    /**
     * 部门ID
     */
    private Integer id;

    /**
     * 部门名称
     */
    private String name;

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
