package cn.zhangchuangla.system.model.vo.department;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 部门列表视图
 */
@Data
public class SysDeptListVo {

    /**
     * 部门ID
     */
    @Schema(description = "部门ID")
    private Integer id;

    /**
     * 部门名称
     */
    @Schema(description = "部门名称")
    private String name;

    /**
     * 父部门ID
     */
    @Schema(description = "父部门ID")
    private Integer parentId;

    /**
     * 部门负责人
     */
    @Schema(description = "部门负责人")
    private Integer managerId;

    /**
     * 部门描述
     */
    @Schema(description = "部门描述")
    private String description;

}
