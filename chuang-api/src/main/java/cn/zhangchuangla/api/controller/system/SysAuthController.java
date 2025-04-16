package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.core.security.model.AuthenticationToken;
import cn.zhangchuangla.common.core.security.model.SysUser;
import cn.zhangchuangla.common.core.security.model.SysUserDetails;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.infrastructure.model.request.LoginRequest;
import cn.zhangchuangla.infrastructure.web.service.SysAuthService;
import cn.zhangchuangla.infrastructure.web.service.TokenService;
import cn.zhangchuangla.system.model.vo.menu.RouteVo;
import cn.zhangchuangla.system.model.vo.user.UserInfoVo;
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
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SysAuthController extends BaseController {


    private final SysAuthService sysAuthService;
    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final SysPermissionsService sysPermissionsService;
    private final TokenService tokenService;
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
     * 获取用户信息
     *
     * @return 用户信息
     */
    //fixme 移动到用户中心
    @GetMapping("/getUserInfo")
    @Operation(summary = "获取用户信息")
    public AjaxResult getInfo() {
        HashMap<String, Object> ajax = new HashMap<>(4);
        Long userId = getUserId();
        SysUser sysUser = sysUserService.getUserInfoByUserId(userId);
        Set<String> roles = sysRoleService.getUserRoleSetByUserId(userId);
        Set<String> permissions = sysPermissionsService.getPermissionsByUserId(userId);
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(sysUser, userInfoVo);
        ajax.put("user", userInfoVo);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return success(ajax);
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
     * @param request 请求对象
     * @return 操作结果
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public AjaxResult logout(HttpServletRequest request) {
        SysUserDetails sysUserDetails = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(sysUserDetails)) {
            String sessionId = sysUserDetails.getSessionId();
            Long userId = sysUserDetails.getUserId();
            //删除用户缓存记录
            redisCache.deleteObject(RedisKeyConstant.LOGIN_TOKEN_KEY + sessionId);
            //删除用户权限缓存
            redisCache.deleteObject(RedisKeyConstant.PASSWORD_ERROR_COUNT + userId);
        }
        return success("退出登录成功！");
    }


}
