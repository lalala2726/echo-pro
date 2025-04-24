package cn.zhangchuangla.system.model.vo.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 部门列表视图
 */
@Data
public class SysDeptListVo {

    /**
     * 部门ID
     */
    @Schema(description = "部门ID")
    private Long deptId;

    /**
     * 部门名称
     */
    @Schema(description = "部门名称")
    private String deptName;

    /**
     * 父部门ID
     */
    @Schema(description = "父部门ID")
    private Long parentId;

    /**
     * 部门状态（0正常 1停用）
     */
    @Schema(description = "部门状态")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 部门负责人
     */
    @Schema(description = "部门负责人")
    private Long managerId;

    /**
     * 部门描述
     */
    @Schema(description = "部门描述")
    private String description;

}
