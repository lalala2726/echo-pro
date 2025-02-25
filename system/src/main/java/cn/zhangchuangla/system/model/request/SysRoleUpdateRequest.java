package cn.zhangchuangla.system.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private Long id;

    /**
     * 角色名
     */
    @Schema(description = "角色名")
    private String name;


}
