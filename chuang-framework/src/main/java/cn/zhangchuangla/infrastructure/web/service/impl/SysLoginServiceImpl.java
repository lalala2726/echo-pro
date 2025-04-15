package cn.zhangchuangla.infrastructure.web.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.core.security.model.SysUserDetails;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.AccountException;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.infrastructure.model.request.LoginRequest;
import cn.zhangchuangla.infrastructure.security.context.AuthenticationContextHolder;
import cn.zhangchuangla.infrastructure.web.service.SysLoginService;
import cn.zhangchuangla.infrastructure.web.service.SysPasswordService;
import cn.zhangchuangla.infrastructure.web.service.TokenService;
import cn.zhangchuangla.system.service.SysLoginLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;


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

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final SysPasswordService sysPasswordService;
    private final SysLoginLogService sysLoginLogService;
    private final RedisCache redisCache;

    @Autowired
    public SysLoginServiceImpl(AuthenticationManager authenticationManager, TokenService tokenService, SysPasswordService sysPasswordService, SysLoginLogService sysLoginLogService, RedisCache redisCache) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.sysPasswordService = sysPasswordService;
        this.sysLoginLogService = sysLoginLogService;
        this.redisCache = redisCache;
    }


    /**
     * 实现登录逻辑
     *
     * @param requestParams 请求参数
     * @return 令牌
     */
    @Override
    public String login(LoginRequest requestParams, HttpServletRequest httpServletRequest) {
        Authentication authenticate = null;
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(requestParams.getUsername(), requestParams.getPassword());
            authenticate = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            log.error("登录失败:", e);
            log.info("用户名[{}],登录失败", requestParams.getUsername());
            sysPasswordService.PasswordErrorCount(requestParams.getUsername());
            sysLoginLogService.recordLoginLog(requestParams.getUsername(), httpServletRequest, Constants.LOGIN_FAIL);
            throw new AccountException(ResponseCode.LOGIN_ERROR, e.getMessage());
        } catch (Exception e) {
            log.warn("服务器发生异常:", e);
        } finally {
            AuthenticationContextHolder.clearContext();
        }
        // 获取用户信息
        SysUserDetails sysUserDetails = null;
        if (authenticate != null) {
            sysUserDetails = (SysUserDetails) authenticate.getPrincipal();
        }
        Long userId;
        if (sysUserDetails != null) {
            userId = sysUserDetails.getSysUser().getUserId();
            sysUserDetails.setUserId(userId);
        }
        log.info("登录用户信息: {}", sysUserDetails);
        //记录登录成功日志
        sysLoginLogService.recordLoginLog(requestParams.getUsername(), httpServletRequest, Constants.LOGIN_SUCCESS);
        return tokenService.createToken(sysUserDetails, httpServletRequest);
    }

    /**
     * 登出
     *
     * @return 登出结果
     */
    @Override
    public boolean logout() {
        String sessionId = SecurityUtils.getLoginUser().getSessionId();
        return redisCache.deleteObject(sessionId);
    }
}



