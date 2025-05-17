package cn.zhangchuangla.system.model.request.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Schema(name = "部门ID", type = "integer", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "部门ID不能小于1")
    private Long deptId;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 50, message = "部门名称不能超过50个字符")
    @Schema(description = "部门名称", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String deptName;

    /**
     * 父部门ID
     */
    @Schema(description = "父部门ID", type = "integer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long parentId;

    /**
     * 部门状态（0正常 1停用）
     */
    @Schema(description = "部门状态", type = "integer", format = "int32", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long status;

    /**
     * 部门负责人
     */
    @Schema(description = "部门负责人", type = "integer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String manager;

    /**
     * 部门描述
     */
    @Schema(description = "部门描述", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;


}
