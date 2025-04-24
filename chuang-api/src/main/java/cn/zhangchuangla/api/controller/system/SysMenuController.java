package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.annotation.OperationLog;
import cn.zhangchuangla.system.model.request.menu.AssignedMenuIdsRequest;
import cn.zhangchuangla.system.model.vo.permission.MenuListVo;
import cn.zhangchuangla.system.model.vo.permission.MenuTreeVo;
import cn.zhangchuangla.system.service.SysMenuService;
import cn.zhangchuangla.system.service.SysRoleMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 * 提供菜单的增删改查等功能
 *
 * @author zhangchuang
 * Created on 2025/3/29 21:19
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class SysMenuController extends BaseController {

    private final SysMenuService sysMenuService;
    private final SysRoleMenuService sysRoleMenuService;

    /**
     * 根据角色ID获取菜单树形结构
     *
     * @return 菜单树形结构
     */
    @GetMapping("/getMenuTreeByRoleId/{roleId}")
    @Operation(summary = "根据角色ID获取菜单树形结构")
    @PreAuthorize("@ss.hasPermission('system:menu:list')")
    public AjaxResult getMenuTreeByRoleId(@PathVariable("roleId") Long roleId) {
        List<MenuListVo> menuTree = sysMenuService.listPermission();
        List<Long> selectedMenuId = sysRoleMenuService.getSelectedMenuIdByRoleId(roleId);
        MenuTreeVo menuTreeVo = new MenuTreeVo();
        menuTreeVo.setMenuListVo(menuTree);
        menuTreeVo.setSelectedMenuId(selectedMenuId);
        return AjaxResult.success(menuTreeVo);
    }

    /**
     * 更改角色菜单的权限
     *
     * @return 菜单列表
     */
    @PutMapping("/updateRoleMenus")
    @Operation(summary = "保存角色菜单")
    @OperationLog(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermission('system:menu:update')")
    public AjaxResult updateRoleMenus(@RequestBody AssignedMenuIdsRequest assignedMenuIdsRequest) {
        boolean result = sysRoleMenuService.updateRoleMenus(assignedMenuIdsRequest);
        return success(result);
    }
}
