package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.system.model.vo.menu.RouteVo;
import cn.zhangchuangla.system.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequiredArgsConstructor
public class SysMenuController extends BaseController {

    private final SysMenuService sysMenuService;

    /**
     * 获取用户路由
     *
     * @return 用户路由
     */
    @Operation(summary = "菜单路由列表")
    @GetMapping("/routes")
    public AjaxResult getCurrentUserRoutes() {
        List<RouteVo> routeList = sysMenuService.getCurrentUserRoutes();
        return success(routeList);
    }

}
