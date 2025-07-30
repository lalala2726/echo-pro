package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.security.SysUser;
import cn.zhangchuangla.framework.model.request.LoginRequest;
import cn.zhangchuangla.framework.model.request.RefreshTokenRequest;
import cn.zhangchuangla.framework.model.request.RegisterRequest;
import cn.zhangchuangla.framework.model.vo.AuthTokenVo;
import cn.zhangchuangla.framework.security.UserSecurityManager;
import cn.zhangchuangla.framework.security.login.AuthService;
import cn.zhangchuangla.framework.security.token.TokenService;
import cn.zhangchuangla.system.core.model.vo.user.UserInfoVo;
import cn.zhangchuangla.system.core.service.SysRoleService;
import cn.zhangchuangla.system.core.service.SysUserService;
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
import java.util.Set;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 13:55
 */
@RestController
@Slf4j
@Tag(name = "用户认证", description = "提供用户登录、刷新令牌、退出登录等认证相关操作接口")
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController extends BaseController {

    private final AuthService authService;
    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final TokenService tokenService;
    private final UserSecurityManager userSecurityManager;


    /**
     * 注册
     *
     * @param request 注册请求参数
     * @return 返回注册结果，成功返回用户ID
     */
    @PostMapping("/register")
    @Operation(summary = "注册")
    public AjaxResult<Long> register(@Parameter(description = "注册参数", required = true)
                                     @Validated @RequestBody RegisterRequest request) {
        request.setUsername(request.getUsername().trim());
        request.setPassword(request.getPassword().trim());
        Long userId = authService.register(request);
        log.info("用户注册成功，用户ID：{}", userId);
        return success(userId);
    }

    /**
     * 登录
     *
     * @param request 请求参数
     * @return token
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    public AjaxResult<AuthTokenVo> login(
            @Parameter(name = "登录参数", required = true) @Validated @RequestBody LoginRequest loginRequest,
            @Parameter(name = "请求对象", required = true) HttpServletRequest request) {
        AuthTokenVo authTokenVo = authService.login(loginRequest, request);
        return success(authTokenVo);
    }

    /**
     * 刷新token
     *
     * @param request 刷新令牌
     * @return 新的token
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新token")
    public AjaxResult<AuthTokenVo> refreshToken(@RequestBody @Validated RefreshTokenRequest request) {
        AuthTokenVo newAuthTokenVo = tokenService.refreshToken(request.getRefreshToken());
        return success(newAuthTokenVo);
    }


    /**
     * 获取用户信息
     * 获取当前登录用户的详细信息，包括用户基本信息、角色集合、权限集合等
     *
     * @return 用户信息，包括user、roles、permissions等
     */
    @GetMapping("/getUserInfo")
    @Operation(summary = "获取用户信息")
    public AjaxResult<HashMap<String, Object>> getInfo() {
        HashMap<String, Object> ajax = new HashMap<>(4);
        Long userId = getUserId();
        Set<String> roles = sysRoleService.getRoleSetByUserId(userId);
        SysUser sysUser = sysUserService.getUserInfoByUserId(userId);
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(sysUser, userInfoVo);
        ajax.put("user", userInfoVo);
        ajax.put("roles", roles);
        return success(ajax);
    }

    /**
     * 退出登录
     *
     * @return 操作结果
     */
    @DeleteMapping("/logout")
    @Operation(summary = "退出登录")
    public AjaxResult<Void> logout() {
        String token = getToken();
        boolean result = userSecurityManager.logoutByToken(token);
        return toAjax(result);
    }

}
