package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.request.menu.SysMenuListRequest;
import cn.zhangchuangla.system.model.vo.menu.SysMenuListVo;
import cn.zhangchuangla.system.service.SysMenuService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhangchuang
 * Created on 2025/3/29 21:19
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/menu")
public class SysMenuController extends BaseController {

    private final SysMenuService sysMenuService;

    @Autowired
    public SysMenuController(SysMenuService sysMenuService) {
        this.sysMenuService = sysMenuService;
    }


    /**
     * 菜单列表
     *
     * @param sysMenuListRequest 请求参数
     * @return 返回分页列表
     */
    @RequestMapping("/list")
    @PreAuthorize("@auth.hasPermission('system:menu:list')")
    @Operation(summary = "菜单列表")
    public TableDataResult listMenu(SysMenuListRequest sysMenuListRequest) {
        Page<SysMenu> page = sysMenuService.listMenu(sysMenuListRequest);
        List<SysMenuListVo> sysMenuListVos = copyListProperties(page, SysMenuListVo.class);
        return getTableData(page, sysMenuListVos);
    }

    @GetMapping("/roleMenuTree/{roleId}")
    public AjaxResult roleMenuTree(@PathVariable("roleId") Long roleId) {

        return success();
    }

}
