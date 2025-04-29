package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.annotation.OperationLog;
import cn.zhangchuangla.system.model.request.menu.AssignedMenuIdsRequest;
import cn.zhangchuangla.system.model.request.menu.MenuForm;
import cn.zhangchuangla.system.model.request.menu.SysMenuListRequest;
import cn.zhangchuangla.system.service.SysMenuService;
import cn.zhangchuangla.system.service.SysRoleMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 菜单管理控制器
 * 提供菜单的增删改查等功能。
 *
 * @author 有来来源
 * @since 2025/3/29 21:19
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
        return success();
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
        return success();
    }

    /**
     * 获取菜单列表
     *
     * @param request 查询参数
     * @return 菜单列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取菜单列表")
    @PreAuthorize("@ss.hasPermission('system:menu:list')")
    public AjaxResult listMenus(SysMenuListRequest request) {
        return success();
    }

    /**
     * 获取菜单路由列表
     *
     * @return 菜单路由列表
     */
    @GetMapping("/options")
    @Operation(summary = "获取菜单下拉列表")
    @PreAuthorize("@ss.hasPermission('system:menu:list')")
    public AjaxResult listMenuOptions(
            @Parameter(description = "是否只查询父级菜单")
            @RequestParam(required = false, defaultValue = "false", value = "onlyParent") boolean onlyParent) {
        return success();
    }

    /**
     * 获取菜单路由列表
     *
     * @return 菜单路由列表
     */
    @Operation(summary = "菜单表单数据")
    @GetMapping("/{id}/form")
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    public AjaxResult getMenuForm(@Parameter(description = "菜单ID") @PathVariable("id") Long id) {
        return success();
    }

    /**
     * 获取当前用户的路由列表
     *
     * @return 菜单路由列表
     */
    @PostMapping
    @Operation(summary = "添加菜单")
    @OperationLog(title = "菜单管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermission('system:menu:add')")
    public AjaxResult addMenu(@RequestBody MenuForm menuForm) {
        return success();
    }

    /**
     * 修改菜单
     *
     * @param menuForm 菜单表单对象
     * @return 菜单列表
     */
    @PutMapping
    @Operation(summary = "修改菜单")
    @OperationLog(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermission('system:menu:edit')")
    public AjaxResult updateMenu(@RequestBody MenuForm menuForm) {
        return success();
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 菜单列表
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜单")
    @OperationLog(title = "菜单管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermission('system:menu:delete')")
    public AjaxResult deleteMenu(@Parameter(description = "菜单ID，多个以英文(,)分割")
                                 @PathVariable("id") Long id) {
        return success();
    }

    /**
     * 修改菜单显示状态
     *
     * @param menuId  菜单ID
     * @param visible 显示状态(1:显示;0:隐藏)
     * @return 菜单列表
     */
    @Operation(summary = "修改菜单显示状态")
    @PatchMapping("/{menuId}")
    @OperationLog(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermission('system:menu:edit')")
    public AjaxResult updateMenuVisible(
            @Parameter(description = "菜单ID")
            @PathVariable Long menuId, @Parameter(description = "显示状态(1:显示;0:隐藏)") Integer visible
    ) {
        return success();
    }


}
