package cn.zhangchuangla.system.core.model.vo.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 部门列表视图
 *
 * @author Chuang
 */
@Data
@Schema(name = "部门列表视图对象", description = "部门列表视图对象")
public class SysDeptListVo {

    /**
     * 部门ID
     */
    @Schema(description = "部门ID", type = "long", example = "1")
    private Long deptId;

    /**
     * 部门名称
     */
    @Schema(description = "部门名称", type = "string", example = "研发部")
    private String deptName;

    /**
     * 父部门ID
     */
    @Schema(description = "父部门ID", type = "long", example = "0")
    private Long parentId;

    /**
     * 部门状态（0正常 1停用）
     */
    @Schema(description = "部门状态", type = "int", example = "0")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", type = "date", example = "2023-01-01T00:00:00.000+00:00")
    private Date createTime;

    /**
     * 部门负责人
     */
    @Schema(description = "部门负责人", type = "string", example = "张三")
    private String manager;

    /**
     * 部门描述
     */
    @Schema(description = "部门描述", type = "string", example = "负责产品研发")
    private String description;

}
