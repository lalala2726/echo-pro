package cn.zhangchuangla.system.model.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 角色权限更新请求
 *
 * @author Chuang
 * <p>
 * created on 2025/5/12 20:07
 */
@Data
@Tag(name = "角色权限更新请求", description = "角色权限更新请求")
public class SysUpdateRolePermissionRequest {

    /**
     * 角色ID
     */
    @Schema(description = "角色ID", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    /**
     * 分配的菜单ID
     */
    @Schema(description = "分配的菜单ID", type = "array", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分配的菜单ID不能为空")
    private List<Long> selectedMenuId;
}
