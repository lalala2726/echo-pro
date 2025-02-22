package cn.zhangchuangla.framework.web.service.impl;

import cn.zhangchuangla.common.config.TokenConfig;
import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.exception.AuthenticationException;
import cn.zhangchuangla.framework.model.entity.LoginUser;
import cn.zhangchuangla.framework.model.request.LoginRequest;
import cn.zhangchuangla.framework.web.service.SysLoginService;
import cn.zhangchuangla.framework.web.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import cn.zhangchuangla.system.mapper.SysRoleMapper;
import cn.zhangchuangla.system.mapper.SysPermissionsMapper;
import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.model.entity.SysPermissions;

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
    private final SysRoleMapper sysRoleMapper;
    private final SysPermissionsMapper sysPermissionsMapper;

    public SysLoginServiceImpl(TokenConfig tokenConfig, AuthenticationManager authenticationManager, TokenService tokenService, RedisCache redisCache, SysRoleMapper sysRoleMapper, SysPermissionsMapper sysPermissionsMapper) {
        this.tokenConfig = tokenConfig;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.redisCache = redisCache;
        this.sysRoleMapper = sysRoleMapper;
        this.sysPermissionsMapper = sysPermissionsMapper;
    }

    /**
     * 实现登录逻辑
     *
     * @param request 请求参数
     * @return 令牌
     */
    @Override
    public String login(LoginRequest request) {
        log.info("执行登录业务逻辑：{}", request);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            throw new AuthenticationException("用户名或密码错误");
        }
        //获取用户信息
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        //todo 获得用户权限等信息
        //设置角色和权限
        loginUser.setRoles(getUserRoles(loginUser.getSysUser().getUserId())); //获取用户角色
        loginUser.setPermissions(getUserPermissions(loginUser.getSysUser().getUserId())); //获取用户权限
        String userId = loginUser.getSysUser().getUserId().toString();
        //生成token
        String token = tokenService.createToken(userId);
        //authenticate存入redis
        //fixme 密码等敏感信息不应该存入redis
        //fixme 应该设置Redis的过期时间
        redisCache.setCacheObject(RedisKeyConstant.LOGIN_TOKEN_KEY + userId, loginUser, tokenConfig.getExpire(), TimeUnit.MINUTES);
        return token;
    }

    //示例方法：获取用户角色
    private List<String> getUserRoles(Long userId) {
        return sysRoleMapper.selectRolesByUserId(userId).stream()
                .map(SysRole::getRoleName) // 假设 SysRole 有 getName() 方法
                .collect(Collectors.toList());
    }

    //示例方法：获取用户权限
    private List<String> getUserPermissions(Long userId) {
        return sysPermissionsMapper.selectPermissionsByUserId(userId).stream()
                .map(SysPermissions::getPermissionName) // 假设 SysPermissions 有 getName() 方法
                .collect(Collectors.toList());
    }
}
