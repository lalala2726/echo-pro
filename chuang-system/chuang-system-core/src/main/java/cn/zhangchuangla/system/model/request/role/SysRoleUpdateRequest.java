package cn.zhangchuangla.system.model.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
    @Schema(description = "角色ID", example = "1", type = "int", format = "int64", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "主键不能小于1")
    private Long roleId;

    /**
     * 角色名
     */
    @NotBlank(message = "角色名不能为空")
    @Schema(description = "角色名", example = "管理员", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String roleName;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "0", type = "integer", format = "integer", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Min(value = 0, message = "排序不能小于0")
    private Integer sort;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "系统默认角色", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;


}
