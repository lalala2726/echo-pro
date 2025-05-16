package cn.zhangchuangla.system.model.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * 角色更新请求参数,用于更新角色时使用
 */
@Data
@Schema(name = "角色表更新参数", description = "角色表更新参数")
public class SysRoleUpdateRequest {

    /**
     * 主键
     */
    @Schema(description = "角色ID", type = "int", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "主键不能小于1")
    private Long roleId;

    /**
     * 角色名
     */
    @NotBlank(message = "角色名不能为空")
    @Schema(description = "角色名", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String roleName;

    /**
     * 角色权限
     */
    @NotBlank(message = "角色权限不能为空")
    @Schema(description = "角色权限", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String roleKey;

    /**
     * 排序
     */
    @Schema(description = "排序", type = "integer", format = "integer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Min(value = 0, message = "排序不能小于0")
    private Integer sort;

    /**
     * 备注
     */
    @Schema(description = "备注", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;


}
