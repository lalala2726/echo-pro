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

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 14:10
 */
@Slf4j
@Service
public class SysLoginServiceImpl implements SysLoginService {

    private final TokenConfig tokenConfig;

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    private final RedisCache redisCache;

    public SysLoginServiceImpl(TokenConfig tokenConfig, AuthenticationManager authenticationManager, TokenService tokenService, RedisCache redisCache) {
        this.tokenConfig = tokenConfig;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.redisCache = redisCache;
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
        String userId = loginUser.getSysUser().getId().toString();
        //生成token
        String token = tokenService.createToken(userId);
        //authenticate存入redis
        //fixme 密码等敏感信息不应该存入redis
        //fixme 应该设置Redis的过期时间
        redisCache.setCacheObject(RedisKeyConstant.LOGIN_TOKEN_KEY + userId, loginUser, tokenConfig.getExpire(), TimeUnit.MINUTES);
        return token;
    }
}
