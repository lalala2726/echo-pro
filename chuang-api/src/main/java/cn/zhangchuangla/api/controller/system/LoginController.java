package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.model.entity.SysUser;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.model.request.LoginRequest;
import cn.zhangchuangla.infrastructure.web.service.SysLoginService;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.vo.menu.RouterVo;
import cn.zhangchuangla.system.model.vo.user.UserInfoVo;
import cn.zhangchuangla.system.service.SysMenuService;
import cn.zhangchuangla.system.service.SysPermissionsService;
import cn.zhangchuangla.system.service.SysRoleService;
import cn.zhangchuangla.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 13:55
 */
@RestController
@Slf4j
@Tag(name = "登录接口")
@RequestMapping("/login")
public class LoginController extends BaseController {


    private final SysLoginService sysLoginService;
    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final SysPermissionsService sysPermissionsService;
    private final SysMenuService sysMenuService;

    public LoginController(SysLoginService sysLoginService, SysUserService sysUserService, SysRoleService sysRoleService, SysPermissionsService sysPermissionsService, SysMenuService sysMenuService) {
        this.sysLoginService = sysLoginService;
        this.sysUserService = sysUserService;
        this.sysRoleService = sysRoleService;
        this.sysPermissionsService = sysPermissionsService;
        this.sysMenuService = sysMenuService;
    }


    /**
     * 登录
     *
     * @param request 请求参数
     * @return token
     */
    @PostMapping()
    @Operation(summary = "登录")
    public AjaxResult login(@Parameter(name = "登录参数", required = true)
                            @Validated @RequestBody LoginRequest loginRequest,
                            @Parameter(name = "请求对象", required = true) HttpServletRequest request) {
        log.info("登录请求参数：{}", request);
        String token = sysLoginService.login(loginRequest, request);
        HashMap<String, String> result = new HashMap<>();
        result.put(Constants.TOKEN, token);
        return success(result);
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public AjaxResult logout() {
        boolean result = sysLoginService.logout();
        return success(result);
    }


    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getUserInfo")
    @Operation(summary = "获取用户信息")
    public AjaxResult getInfo() {
        AjaxResult ajax = new AjaxResult();
        Long userId = getUserId();
        SysUser sysUser = sysUserService.getUserInfoByUserId(userId);
        Set<String> roles = sysRoleService.getUserRoleSetByUserId(userId);
        Set<String> permissions = sysPermissionsService.getPermissionsByUserId(userId);
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(sysUser, userInfoVo);
        ajax.put("user", userInfoVo);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return AjaxResult.success(ajax);
    }

    /**
     * 获取路由信息
     *
     * @return 返回路由信息
     */
    @GetMapping("/getRouters")
    @Operation(summary = "获取路由信息")
    public AjaxResult getRouters() {
        Long currentUserId = getUserId();
        List<SysMenu> menus = sysMenuService.getMenuUserId(currentUserId);
        List<RouterVo> routerVos = sysMenuService.buildMenu(menus);
        return success(routerVos);
    }


}
