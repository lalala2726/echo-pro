package cn.zhangchuangla.system.model.request.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/24 14:06
 */
@Schema(description = "更新角色菜单请求参数")
@Data
public class AssignedMenuIdsRequest {

    /**
     * 角色ID
     */
    @Schema(description = "角色ID", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "角色ID不能小于1")
    private Long roleId;

    /**
     * 菜单ID
     */
    @Schema(description = "菜单ID", type = "integer", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> menuIds;
}
