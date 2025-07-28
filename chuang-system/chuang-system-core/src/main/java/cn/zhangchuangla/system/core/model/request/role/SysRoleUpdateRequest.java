package cn.zhangchuangla.system.core.model.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 角色更新请求参数,用于更新角色时使用
 *
 * @author Chuang
 */
@Data
@Schema(name = "角色更新请求对象", description = "角色更新请求对象")
public class SysRoleUpdateRequest {

    /**
     * 主键
     */
    @Schema(description = "角色ID", example = "1", type = "int64", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "角色ID不能为空")
    private Long id;

    /**
     * 角色名
     */
    @Schema(description = "角色名", example = "管理员", type = "string")
    private String roleName;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "1", type = "integer", format = "integer")
    private Integer status;

    /**
     * 角色权限标识
     */
    @Schema(description = "角色权限标识", example = "admin", type = "string")
    private String roleKey;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "0", type = "integer", format = "integer")
    private Integer sort;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "系统默认角色", type = "string")
    private String remark;


}
