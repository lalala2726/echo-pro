package cn.zhangchuangla.system.model.vo.department;

import cn.zhangchuangla.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门视图
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysDeptVo extends BaseVO {

    /**
     * 部门ID
     */
    @Schema(name = "部门ID")
    private Integer id;

    /**
     * 部门名称
     */
    @Schema(name = "部门名称")
    private String name;

    /**
     * 父部门ID
     */
    @Schema(name = "父部门ID")
    private Integer parentId;

    /**
     * 部门负责人
     */
    @Schema(name = "部门负责人")
    private Integer managerId;

    /**
     * 部门描述
     */
    @Schema
    private String description;

}
