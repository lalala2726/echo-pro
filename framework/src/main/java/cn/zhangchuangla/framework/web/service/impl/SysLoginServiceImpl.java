package cn.zhangchuangla.framework.web.service.impl;

import cn.zhangchuangla.common.config.TokenConfig;
import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.AuthenticationException;
import cn.zhangchuangla.framework.model.entity.LoginUser;
import cn.zhangchuangla.framework.model.request.LoginRequest;
import cn.zhangchuangla.framework.web.service.SysLoginService;
import cn.zhangchuangla.framework.web.service.TokenService;
import cn.zhangchuangla.system.model.entity.SysPermissions;
import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.service.SysPermissionsService;
import cn.zhangchuangla.system.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 登录服务实现类
 * 该类实现了用户登录的逻辑，包括验证用户身份和生成token。
 *
 * @author Chuang
 * created on 2025/2/19 14:10
 */
@Slf4j
@Service
public class SysLoginServiceImpl implements SysLoginService {

    private final TokenConfig tokenConfig;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RedisCache redisCache;
    private final SysRoleService sysRoleService;
    private final SysPermissionsService sysPermissionsService;

    public SysLoginServiceImpl(TokenConfig tokenConfig, AuthenticationManager authenticationManager, TokenService tokenService, RedisCache redisCache, SysRoleService sysRoleService, SysPermissionsService sysPermissionsService) {
        this.tokenConfig = tokenConfig;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.redisCache = redisCache;
        this.sysRoleService = sysRoleService;
        this.sysPermissionsService = sysPermissionsService;
    }

    /**
     * 实现登录逻辑
     *
     * @param request 请求参数
     * @return 令牌
     */
    @Override
    public String login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            throw new AuthenticationException(ResponseCode.LOGIN_ERROR, "账号或密码错误");
        }
        //获取用户信息
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        //设置角色和权限
        List<SysRole> roles = sysRoleService.getRoleListByUserId(loginUser.getSysUser().getUserId());
        loginUser.setRoles(roles); //获取用户角色
        //获取用户权限
        List<SysPermissions> permissions = sysPermissionsService.getPermissionsByUserId(loginUser.getSysUser().getUserId());
        loginUser.setPermissions(permissions);
        String userId = loginUser.getSysUser().getUserId().toString();
        //生成token
        log.info("登录用户信息:{}", loginUser);
        String token = tokenService.createToken(userId);
        redisCache.setCacheObject(RedisKeyConstant.LOGIN_TOKEN_KEY + userId, loginUser, tokenConfig.getExpire(), TimeUnit.MINUTES);
        return token;
    }


}
