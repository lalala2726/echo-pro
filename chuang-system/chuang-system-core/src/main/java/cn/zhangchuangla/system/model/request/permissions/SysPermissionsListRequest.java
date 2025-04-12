package cn.zhangchuangla.system.model.request.permissions;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "权限列表请求类")
public class SysPermissionsListRequest extends BasePageRequest {


    /**
     * 权限名称
     */
    @Schema(description = "权限名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String permissionsName;

    /**
     * 权限标识
     */
    @Schema(description = "权限标识", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String permissionsKey;
}
