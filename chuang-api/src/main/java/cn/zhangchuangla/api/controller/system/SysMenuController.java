package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.system.model.vo.permission.MenuListVo;
import cn.zhangchuangla.system.model.vo.permission.MenuTreeVo;
import cn.zhangchuangla.system.service.SysMenuService;
import cn.zhangchuangla.system.service.SysRoleMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public AjaxResult getMenuTreeByRoleId(@PathVariable("roleId") Long roleId) {
        List<MenuListVo> menuTree = sysMenuService.listPermission();
        List<Long> selectedMenuId = sysRoleMenuService.getSelectedMenuIdByRoleId(roleId);
        MenuTreeVo menuTreeVo = new MenuTreeVo();
        menuTreeVo.setMenuListVo(menuTree);
        menuTreeVo.setSelectedMenuId(selectedMenuId);
        return AjaxResult.success(menuTreeVo);
    }
}
