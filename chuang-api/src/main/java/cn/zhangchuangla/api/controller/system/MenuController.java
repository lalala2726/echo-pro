package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.utils.BeanCotyUtils;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.system.core.model.entity.SysMenu;
import cn.zhangchuangla.system.core.model.request.menu.SysMenuAddRequest;
import cn.zhangchuangla.system.core.model.request.menu.SysMenuQueryRequest;
import cn.zhangchuangla.system.core.model.request.menu.SysMenuUpdateRequest;
import cn.zhangchuangla.system.core.model.vo.menu.MenuOption;
import cn.zhangchuangla.system.core.model.vo.menu.RouterVo;
import cn.zhangchuangla.system.core.model.vo.menu.SysMenuListVo;
import cn.zhangchuangla.system.core.model.vo.menu.SysMenuVo;
import cn.zhangchuangla.system.core.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/6 05:03
 */
@Slf4j
@RequestMapping("/system/menu")
@RestController
@RequiredArgsConstructor
public class MenuController extends BaseController {

    private final SysMenuService sysMenuService;


    /**
     * 获取当前用户路由信息
     *
     * @return 路由信息
     */
    @Operation(summary = "获取路由")
    @GetMapping("/route")
    public AjaxResult<List<RouterVo>> getRoute() {
        List<SysMenu> list = sysMenuService.list();
        List<RouterVo> routerVos = sysMenuService.buildRouteVo(list);
        return AjaxResult.success(routerVos);
    }


    /**
     * 获取菜单列表
     *
     * @param request 菜单列表查询参数
     * @return 菜单列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取菜单列表")
    @PreAuthorize("@ss.hasPermission('system:menu:list')")
    public AjaxResult<List<SysMenuListVo>> listMenu(SysMenuQueryRequest request) {
        List<SysMenu> sysMenus = sysMenuService.listMenu(request);
        List<SysMenuListVo> sysMenuListVos = sysMenuService.buildMenuList(sysMenus);
        return success(sysMenuListVos);
    }

    /**
     * 获取菜单详情
     *
     * @param menuId 菜单ID
     * @return 菜单详情
     */
    @GetMapping("/{menuId:\\d+}")
    @Operation(summary = "获取菜单详情")
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    public AjaxResult<SysMenuVo> getMenuById(@PathVariable("menuId") Long menuId) {
        SysMenu sysMenu = sysMenuService.getMenuById(menuId);
        SysMenuVo sysMenuVo = BeanCotyUtils.copyProperties(sysMenu, SysMenuVo.class);
        return AjaxResult.success(sysMenuVo);
    }

    /**
     * 添加菜单
     *
     * @param request 添加菜单请求参数
     * @return 添加结果
     */
    @PostMapping
    @Operation(summary = "添加菜单")
    @PreAuthorize("@ss.hasPermission('system:menu:add')")
    @OperationLog(title = "菜单管理", businessType = BusinessType.INSERT)
    public AjaxResult<Void> addMenu(@RequestBody SysMenuAddRequest request) {
        boolean result = sysMenuService.addMenu(request);
        return toAjax(result);
    }

    /**
     * 修改菜单
     *
     * @param request 修改菜单请求参数
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改菜单")
    @PreAuthorize("@ss.hasPermission('system:menu:update')")
    @OperationLog(title = "菜单管理", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> updateMenu(@RequestBody SysMenuUpdateRequest request) {
        log.info("修改菜单：{}", request);
        boolean result = sysMenuService.updateMenu(request);
        return toAjax(result);
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return 删除结果
     */
    @DeleteMapping("/{id:\\d+}")
    @Operation(summary = "删除菜单")
    @PreAuthorize("@ss.hasPermission('system:menu:delete')")
    @OperationLog(title = "菜单管理", businessType = BusinessType.DELETE)
    public AjaxResult<Void> deleteMenu(@PathVariable("id") Long menuId) {
        boolean result = sysMenuService.deleteMenu(menuId);
        return toAjax(result);
    }

    /**
     * 获取菜单选项
     *
     * @return 菜单选项
     */
    @GetMapping("/options")
    @Operation(summary = "获取菜单选项")
    @PreAuthorize("@ss.hasPermission('system:menu:list')")
    public AjaxResult<List<Option<String>>> getMenuOptions() {
        List<Option<String>> options = sysMenuService.getMenuOptions();
        return success(options);
    }

    /**
     * 获取菜单树
     *
     * @return 菜单树
     */
    @GetMapping("/tree")
    @Operation(summary = "菜单树")
    @PreAuthorize("@ss.hasPermission('system:menu:list')")
    public AjaxResult<List<MenuOption>> menuTree() {
        List<MenuOption> sysMenu = sysMenuService.menuTree();
        return success(sysMenu);
    }

    /**
     * 检查菜单的名字是否已存在
     *
     * @param id   菜单id
     * @param name 菜单名称
     * @return 检查结果
     */
    @Operation(summary = "检查菜单名称是否已经存在")
    @GetMapping("/name-exists")
    public AjaxResult<Boolean> isMenuNameExists(@RequestParam(value = "id", required = false) Long id,
                                                @RequestParam("name") String name) {
        boolean exists = sysMenuService.isMenuNameExists(id, name);
        return success(exists);
    }

    /**
     * 检查菜单路径是否已经存在
     *
     * @param id   菜单id
     * @param path 路由地址
     * @return 存在返回true，不存在返回false
     */
    @GetMapping("/path-exists")
    @Operation(summary = "检查菜单路径是否已经存在")
    public AjaxResult<Boolean> isMenuPathExists(@RequestParam(value = "id", required = false) Long id,
                                                @RequestParam("path") String path) {
        boolean exists = sysMenuService.isMenuPathExists(id, path);
        return success(exists);
    }


}
