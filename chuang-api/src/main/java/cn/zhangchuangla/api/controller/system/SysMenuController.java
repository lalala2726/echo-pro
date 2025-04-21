package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.system.converter.SysMenuConverter;
import cn.zhangchuangla.system.model.vo.permission.PermissionListVo;
import cn.zhangchuangla.system.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final SysMenuConverter sysMenuConverter;


    /**
     * 获取权限列表
     *
     * @return 权限列表
     */
    @GetMapping("/permission/list")
    @Operation(summary = "获取系统权限列表")
    @PreAuthorize("@ss.hasPermission('system:permission:list')")
    public AjaxResult listPermission() {
        List<PermissionListVo> sysMenus = sysMenuService.listPermission();
        return success(sysMenus);
    }
}
