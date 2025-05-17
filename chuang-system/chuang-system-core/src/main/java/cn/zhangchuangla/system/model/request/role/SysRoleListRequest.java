package cn.zhangchuangla.system.model.request.role;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色参数,用户查询角色列表
 *
 * @author zhangchuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "角色列表查询请求对象", description = "角色列表查询请求对象")
public class SysRoleListRequest extends BasePageRequest {

    /**
     * 角色名
     */
    @Schema(description = "角色名", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String roleName;

}
