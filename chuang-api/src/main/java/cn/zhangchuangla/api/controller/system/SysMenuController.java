package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.model.entity.Option;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.system.model.request.menu.SysMenuAddRequest;
import cn.zhangchuangla.system.model.request.menu.SysMenuUpdateRequest;
import cn.zhangchuangla.system.model.request.menu.SysMenuUpdateRolePermRequest;
import cn.zhangchuangla.system.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    /**
     * 更改角色菜单的权限
     *
     * @return 菜单列表
     */
    @PutMapping("/updateRoleMenus")
    @Operation(summary = "保存角色菜单")
    @OperationLog(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermission('system:menu:update')")
    public AjaxResult<Void> updateRoleMenus(SysMenuUpdateRolePermRequest request) {
        boolean result = sysMenuService.updateRoleMenus(request);
        return toAjax(result);
    }


    /**
     * 获取菜单路由列表
     *
     * @return 菜单路由列表
     */
    @GetMapping("/options")
    @Operation(summary = "获取菜单下拉列表")
    @PreAuthorize("@ss.hasPermission('system:menu:list')")
    public AjaxResult<List<Option<String>>> listMenuOptions(
            @Parameter(description = "是否只查询父级菜单")
            @RequestParam(required = false, defaultValue = "false", value = "onlyParent") boolean onlyParent) {
        List<Option<String>> options = sysMenuService.getMenuOptions(onlyParent);
        return success(options);
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
    public AjaxResult<Void> addMenu(SysMenuAddRequest request) {
        boolean result = sysMenuService.addMenu(request);
        return toAjax(result);
    }

    /**
     * 修改菜单
     *
     * @return 菜单列表
     */
    @PutMapping
    @Operation(summary = "修改菜单")
    @OperationLog(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermission('system:menu:edit')")
    public AjaxResult<Void> updateMenu(SysMenuUpdateRequest request) {
        boolean result = sysMenuService.updateMenu(request);
        return toAjax(result);
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜单")
    @OperationLog(title = "菜单管理", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermission('system:menu:delete')")
    public AjaxResult<Void> deleteMenu(@Parameter(description = "菜单ID，多个以英文(,)分割")
                                       @PathVariable("id") Long id) {
        boolean result = sysMenuService.deleteMenuById(id);
        return toAjax(result);
    }


}
