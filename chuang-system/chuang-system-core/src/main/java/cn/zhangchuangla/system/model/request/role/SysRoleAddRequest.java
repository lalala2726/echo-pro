package cn.zhangchuangla.system.model.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * 角色新增请求参数
 *
 * @author Chuang
 */
@Data
@Schema(name = "添加角色请求对象", description = "添加角色请求对象")
public class SysRoleAddRequest {

    /**
     * 角色名
     */
    @Schema(description = "角色名", example = "管理员", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色名不能为空")
    @Size(min = 2, max = 50, message = "角色名不能超过50个字符")
    private String roleName;


    /**
     * 角色权限字符串
     */
    @Schema(description = "角色权限字符串", example = "admin", type = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色权限字符串不能为空")
    @Size(min = 2, max = 50, message = "角色权限字符串长度必须介于 2 和 50 之间")
    private String roleKey;

    /**
     * 角色排序
     */
    @Schema(description = "角色排序", example = "1", type = "integer")
    private Integer sort;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "系统默认管理员角色", type = "string")
    private String remark;


}
