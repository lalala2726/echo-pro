package cn.zhangchuangla.system.model.request.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 部门添加请求类
 *
 * @author Chuang
 */
@Data
@Schema(name = "部门添加请求类", description = "部门添加请求类")
public class SysDeptAddRequest {

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 50, message = "部门名称不能超过50个字符")
    @Schema(description = "部门名称", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "系统管理")
    private String deptName;

    /**
     * 父部门ID
     */
    @Schema(description = "父部门ID", type = "integer", example = "1")
    private Long parentId;

    /**
     * 部门状态（0正常 1停用）
     */
    @Schema(description = "部门状态", type = "integer", format = "int32", example = "0")
    private Integer status;

    /**
     * 部门负责人
     */
    @Schema(description = "部门负责人", type = "integer", example = "张三")
    @Size(max = 50, message = "部门负责人不能超过50个字符")
    private String manager;

    /**
     * 部门描述
     */
    @Schema(description = "部门描述", type = "string", example = "系统管理")
    private String description;


}
