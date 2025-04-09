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
    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "主键不能为空")
    @Min(value = 1L, message = "主键不能小于1")
    private Long id;

    /**
     * 角色名
     */
    @NotBlank(message = "角色名不能为空")
    @Schema(description = "角色名")
    private String name;


}
