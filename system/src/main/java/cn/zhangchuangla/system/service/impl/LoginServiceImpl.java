package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.exception.AuthenticationException;
import cn.zhangchuangla.common.utils.JwtUtil;
import cn.zhangchuangla.system.model.entity.LoginUser;
import cn.zhangchuangla.system.model.request.LoginRequest;
import cn.zhangchuangla.system.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 14:10
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;

    private final RedisCache redisCache;

    public LoginServiceImpl(AuthenticationManager authenticationManager, RedisCache redisCache) {
        this.authenticationManager = authenticationManager;
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
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getSysUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId);
        //authenticate存入redis
        redisCache.setCacheObject("login:" + userId, loginUser);
        return jwt;
    }
}
