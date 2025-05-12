package cn.zhangchuangla.system.model.vo.role;

import cn.zhangchuangla.system.model.vo.menu.SysMenuTreeList;
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
@Schema(description = "角色权限分配对象")
public class SysRolePermVo {

    /**
     * 菜单树
     */
    @Schema(description = "菜单树")
    private List<SysMenuTreeList> sysMenuTreeList;

    /**
     * 已选中的菜单ID
     */
    @Schema(description = "已选中")
    private List<Long> selectedMenuId;

}
