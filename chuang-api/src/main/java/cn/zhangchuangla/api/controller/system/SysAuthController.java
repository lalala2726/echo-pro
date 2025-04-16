package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.core.security.model.AuthenticationToken;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.model.request.LoginRequest;
import cn.zhangchuangla.infrastructure.web.service.SysAuthService;
import cn.zhangchuangla.system.model.vo.menu.RouteVo;
import cn.zhangchuangla.system.service.SysMenuService;
import cn.zhangchuangla.system.service.SysPermissionsService;
import cn.zhangchuangla.system.service.SysRoleService;
import cn.zhangchuangla.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 13:55
 */
@RestController
@Slf4j
@Tag(name = "登录接口")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SysAuthController extends BaseController {


    private final SysAuthService sysAuthService;
    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final SysPermissionsService sysPermissionsService;
    private final SysMenuService sysMenuService;
    private final RedisCache redisCache;


    /**
     * 登录
     *
     * @param request 请求参数
     * @return token
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    public AjaxResult login(@Parameter(name = "登录参数", required = true)
                            @Validated @RequestBody LoginRequest loginRequest,
                            @Parameter(name = "请求对象", required = true) HttpServletRequest request) {
        log.info("登录请求参数：{}", request);
        AuthenticationToken authenticationToken = sysAuthService.login(loginRequest, request);
        return success(authenticationToken);
    }

    /**
     * 刷新token
     *
     * @param refreshToken 刷新令牌
     * @return 新的token
     */
    @PostMapping
    @Operation(summary = "刷新令牌")
    public AjaxResult refreshToken(@Parameter(name = "刷新令牌", required = true)
                                   @RequestParam String refreshToken) {
        AuthenticationToken newAuthenticationToken = sysAuthService.refreshToken(refreshToken);
        return success(newAuthenticationToken);
    }


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

    /**
     * 退出登录
     *
     * @return 操作结果
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public AjaxResult logout() {
        sysAuthService.logout();
        return success();
    }


}
