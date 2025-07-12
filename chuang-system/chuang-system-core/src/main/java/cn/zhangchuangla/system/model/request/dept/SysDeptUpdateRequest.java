package cn.zhangchuangla.system.model.request.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 部门修改请求类
 *
 * @author Chuang
 */
@Data
@Schema(name = "部门修改请求类", description = "部门修改请求类")
public class SysDeptUpdateRequest {

    /**
     * 部门ID
     */
    @Schema(name = "部门ID", example = "1", type = "integer", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "部门ID不能为空")
    private Long deptId;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    @Schema(description = "部门名称", example = "研发部", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String deptName;

    /**
     * 父部门ID
     */
    @Schema(description = "父部门ID", example = "0", type = "integer")
    private Long parentId;

    /**
     * 部门状态（0正常 1停用）
     */
    @Schema(description = "部门状态", example = "0", type = "integer", format = "int32")
    private Integer status;

    /**
     * 部门负责人
     */
    @Schema(description = "部门负责人", example = "张三", type = "string")
    private String manager;

    /**
     * 部门描述
     */
    @Schema(description = "部门描述", example = "负责产品研发的部门", type = "string")
    private String description;


}
