package cn.zhangchuangla.system.core.model.vo.role;

import cn.zhangchuangla.system.core.model.vo.menu.SysMenuTreeList;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/12 13:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "角色权限分配对象", description = "角色权限分配对象")
public class SysRolePermissionVo {

    /**
     * 角色ID
     */
    @Schema(description = "角色ID", type = "int", example = "1")
    private Long roleId;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称", type = "string", example = "系统管理员")
    private String roleName;

    /**
     * 角色权限标识
     */
    @Schema(description = "角色权限标识", type = "string", example = "admin")
    private String roleKey;

    /**
     * 菜单树
     */
    @Schema(description = "菜单树", type = "array")
    private List<SysMenuTreeList> sysMenuTree;

    /**
     * 已选中的菜单ID
     */
    @Schema(description = "已选中", type = "array")
    private List<Long> selectedMenuId;

}
