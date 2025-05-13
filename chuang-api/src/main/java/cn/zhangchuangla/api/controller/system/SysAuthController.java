package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.security.model.AuthenticationToken;
import cn.zhangchuangla.common.core.security.model.SysUser;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.framework.model.request.LoginRequest;
import cn.zhangchuangla.framework.web.service.SysAuthService;
import cn.zhangchuangla.system.converter.SysUserConverter;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.vo.menu.RouterVo;
import cn.zhangchuangla.system.model.vo.user.UserInfoVo;
import cn.zhangchuangla.system.service.SysMenuService;
import cn.zhangchuangla.system.service.SysRoleService;
import cn.zhangchuangla.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
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
@RequiredArgsConstructor
public class SysAuthController extends BaseController {

    private final SysAuthService sysAuthService;
    private final SysUserService sysUserService;
    private final SysMenuService sysMenuService;
    private final SysRoleService sysRoleService;
    private final SysUserConverter sysUserConverter;

    /**
     * 登录
     *
     * @param request 请求参数
     * @return token
     */
    @PostMapping("/auth/login")
    @Operation(summary = "登录")
    public AjaxResult<AuthenticationToken> login(
            @Parameter(name = "登录参数", required = true) @Validated @RequestBody LoginRequest loginRequest,
            @Parameter(name = "请求对象", required = true) HttpServletRequest request) {
        // 1. 校验验证码
//        String code = redisCache.getCacheObject(RedisConstants.CAPTCHA_CODE + loginRequest.getCaptchaKey());
//        if (!loginRequest.getCaptchaCode().equals(code)) {
//            return error("验证码错误");
//        }
        AuthenticationToken authenticationToken = sysAuthService.login(loginRequest, request);
        return success(authenticationToken);
    }

    /**
     * 刷新token
     *
     * @param request 刷新令牌
     * @return 新的token
     */
    @PostMapping("/auth/refreshToken")
    @Operation(summary = "刷新令牌")
    public AjaxResult<AuthenticationToken> refreshToken(@Parameter(description = "刷新令牌", required = true)
                                                            @ParameterObject @RequestBody AuthenticationToken request) {
        AuthenticationToken newAuthenticationToken = sysAuthService.refreshToken(request.getRefreshToken());
        return success(newAuthenticationToken);
    }

    /**
     * 获取用户路由
     *
     * @return 用户路由
     */
    @Operation(summary = "菜单路由列表")
    @GetMapping("/auth/routes")
    public AjaxResult<List<RouterVo>> getCurrentUserRoutes() {
        List<SysMenu> menuListByUserId = sysMenuService.getMenuListByUserId(1L);
        List<RouterVo> routerVos = sysMenuService.buildMenus(menuListByUserId);
        return success(routerVos);
    }

    /**
     * 获取用户信息
     * 获取当前登录用户的详细信息，包括用户基本信息、角色集合、权限集合等
     *
     * @return 用户信息，包括user、roles、permissions等
     */
    @GetMapping("/auth/getUserInfo")
    @Operation(summary = "获取用户信息")
    public AjaxResult<HashMap<String, Object>> getInfo() {
        HashMap<String, Object> ajax = new HashMap<>(4);
        Long userId = getUserId();
        SysUser sysUser = sysUserService.getUserInfoByUserId(userId);
        Set<String> permissions = sysMenuService.getUserPermissionByUserId(userId);
        Set<String> roles = sysRoleService.getRoleSetByUserId(userId);
        UserInfoVo userInfoVo = sysUserConverter.toUserInfoVo(sysUser);
        ajax.put("user", userInfoVo);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return success(ajax);
    }

    /**
     * 退出登录
     *
     * @return 操作结果
     */
    @DeleteMapping("/auth/logout")
    @Operation(summary = "退出登录")
    public AjaxResult<Void> logout() {
        sysAuthService.logout();
        return success();
    }

}
