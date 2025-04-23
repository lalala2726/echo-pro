package cn.zhangchuangla.system.model.request.role;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色参数,用户查询角色列表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "角色查询参数", description = "角色表查询参数")
public class SysRoleQueryRequest extends BasePageRequest {

    /**
     * 角色名
     */
    @Schema(description = "角色名", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String roleName;

}
