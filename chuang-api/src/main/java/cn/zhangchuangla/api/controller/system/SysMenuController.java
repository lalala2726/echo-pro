package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.common.core.result.TableDataResult;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.request.menu.SysMenuAddRequest;
import cn.zhangchuangla.system.model.request.menu.SysMenuQueryRequest;
import cn.zhangchuangla.system.model.request.menu.SysMenuUpdateRequest;
import cn.zhangchuangla.system.model.vo.menu.SysMenuListVo;
import cn.zhangchuangla.system.service.SysMenuService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/6 05:03
 */
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class SysMenuController extends BaseController {

    private final SysMenuService sysMenuService;


    /**
     * 获取菜单列表
     *
     * @param request 菜单列表查询参数
     * @return 菜单列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取菜单列表")
    @PreAuthorize("@ss.hasPermission('system:menu:list')")
    public AjaxResult<TableDataResult> listMenu(SysMenuQueryRequest request) {
        Page<SysMenu> sysMenuPage = sysMenuService.listMenu(request);
        List<SysMenuListVo> sysMenuListVos = copyListProperties(sysMenuPage, SysMenuListVo.class);
        return getTableData(sysMenuPage, sysMenuListVos);
    }

    @GetMapping("/option")
    @Operation(summary = "获取菜单选项")
    @PreAuthorize("@ss.hasPermission('system:menu:list')")
    public AjaxResult<List<Option<String>>> getMenuOptions() {
        List<Option<String>> options = sysMenuService.getMenuOptions();
        return success(options);
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

    public AjaxResult<Void> addMenu(SysMenuAddRequest request) {
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
    public AjaxResult<Void> updateMenu(SysMenuUpdateRequest request) {
        boolean result = sysMenuService.updateMenu(request);
        return toAjax(result);
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return 删除结果
     */
    @DeleteMapping
    @Operation(summary = "删除菜单")
    @PreAuthorize("@ss.hasPermission('system:menu:delete')")
    public AjaxResult<Void> deleteMenu(Long menuId) {
        boolean result = sysMenuService.deleteMenu(menuId);
        return toAjax(result);
    }

    /**
     * 获取菜单详情
     *
     * @param menuId 菜单ID
     * @return 菜单详情
     */
    @GetMapping("/{menuId}")
    @Operation(summary = "获取菜单详情")
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    public AjaxResult<SysMenu> getMenuById(@PathVariable("menuId") Long menuId) {
        SysMenu sysMenu = sysMenuService.getMenuById(menuId);
        return AjaxResult.success(sysMenu);
    }


}
