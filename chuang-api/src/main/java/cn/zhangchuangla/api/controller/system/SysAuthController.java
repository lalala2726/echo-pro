package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.security.AuthenticationToken;
import cn.zhangchuangla.common.core.entity.security.RefreshTokenRequest;
import cn.zhangchuangla.common.core.entity.security.SysUser;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.framework.model.request.LoginRequest;
import cn.zhangchuangla.framework.web.service.SysAuthService;
import cn.zhangchuangla.system.model.vo.user.UserInfoVo;
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
public class SysAuthController extends BaseController {

    private final SysAuthService sysAuthService;
    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;

    /**
     * 登录
     *
     * @param request 请求参数
     * @return token
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    public AjaxResult<AuthenticationToken> login(
            @Parameter(name = "登录参数", required = true) @Validated @RequestBody LoginRequest loginRequest,
            @Parameter(name = "请求对象", required = true) HttpServletRequest request) {
        AuthenticationToken authenticationToken = sysAuthService.login(loginRequest, request);
        return success(authenticationToken);
    }

    /**
     * 刷新token
     *
     * @param request 刷新令牌
     * @return 新的token
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新token")
    public AjaxResult<AuthenticationToken> refreshToken(@RequestBody @Validated RefreshTokenRequest request) {
        AuthenticationToken newAuthenticationToken = sysAuthService.refreshToken(request);
        return success(newAuthenticationToken);
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
        sysAuthService.logout();
        return success();
    }

}
