package cn.zhangchuangla.system.model.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * 角色新增请求参数
 */
@Data
@Schema(name = "添加角色", description = "角色新增参数")
public class SysRoleAddRequest {

    /**
     * 角色名
     */
    @Schema(description = "角色名", type = "string", example = "用户", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色名不能为空")
    @Size(min = 2, max = 50, message = "角色名不能超过50个字符")
    private String roleName;


    /**
     * 角色权限字符串
     */
    @Schema(description = "角色权限字符串", type = "string", example = "user", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色权限字符串不能为空")
    @Size(min = 2, max = 50, message = "角色权限字符串长度必须介于 2 和 50 之间")
    private String roleKey;

    /**
     * 角色排序
     */
    @Schema(description = "角色排序", type = "integer", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer sort;

    /**
     * 备注
     */
    @Schema(description = "备注", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;


}
