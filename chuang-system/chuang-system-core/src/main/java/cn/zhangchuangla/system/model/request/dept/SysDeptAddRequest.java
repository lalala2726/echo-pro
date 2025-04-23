package cn.zhangchuangla.system.model.request.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 部门表
 */
@Data
@Schema(name = "部门添加请求类")
public class SysDeptAddRequest {


    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 50, message = "部门名称不能超过50个字符")
    @Schema(description = "部门名称", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 父部门ID
     */
    @Schema(description = "父部门ID", type = "integer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 50, message = "父部门ID不能超过50个字符")
    private Integer parentId;

    /**
     * 部门负责人
     */
    @Schema(description = "部门负责人", type = "integer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 50, message = "部门负责人不能超过50个字符")
    private Integer managerId;

    /**
     * 部门描述
     */
    @Schema(description = "部门描述", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;


}
