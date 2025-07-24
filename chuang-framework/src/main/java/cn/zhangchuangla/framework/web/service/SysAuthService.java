package cn.zhangchuangla.framework.web.service;

import cn.zhangchuangla.common.core.entity.security.AuthTokenVo;
import cn.zhangchuangla.common.core.entity.security.RefreshTokenRequest;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.AuthorizationException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.common.core.utils.client.IPUtils;
import cn.zhangchuangla.common.core.utils.client.UserAgentUtils;
import cn.zhangchuangla.framework.model.request.LoginRequest;
import cn.zhangchuangla.framework.security.token.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
@RequiredArgsConstructor
public class SysAuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AsyncService asyncService;

    /**
     * 实现登录逻辑
     *
     * @param request 请求参数
     * @return 令牌
     */
    public AuthTokenVo login(LoginRequest request, HttpServletRequest httpServletRequest) {
        // 1. 创建用于密码认证的令牌（未认证）
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getUsername().trim(), request.getPassword().trim());

        // 2. 执行认证（认证中）
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            log.error("用户名:{},登录失败！", request.getUsername(), e);
            // 使用异步服务记录登录失败日志
            String ipAddr = IPUtils.getIpAddress(httpServletRequest);
            String userAgent = UserAgentUtils.getUserAgent(httpServletRequest);
            asyncService.recordLoginLog(request.getUsername(), ipAddr, userAgent, false);
            throw new AuthorizationException(ResultCode.LOGIN_ERROR, "账号或密码错误!");
        }

        // 3. 认证成功后生成 JWT 令牌，并存入 Security 上下文，供登录日志 AOP 使用（已认证）
        AuthTokenVo authTokenVoResponse = tokenService.createToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 使用异步服务记录登录成功日志
        String ipAddr = IPUtils.getIpAddress(httpServletRequest);
        String userAgent = UserAgentUtils.getUserAgent(httpServletRequest);
        asyncService.recordLoginLog(request.getUsername(), ipAddr, userAgent, true);

        return authTokenVoResponse;
    }

    /**
     * 刷新令牌
     *
     * @param request 刷新令牌
     * @return 认证 Token 响应
     */
    public AuthTokenVo refreshToken(RefreshTokenRequest request) {
        // 验证刷新令牌
        boolean isValidate = tokenService.validateRefreshToken(request.getRefreshToken());

        if (!isValidate) {
            throw new AuthorizationException(ResultCode.REFRESH_TOKEN_INVALID);
        }
        // 刷新令牌有效，生成新的访问令牌
        return tokenService.refreshToken(request.getRefreshToken());
    }

    /**
     * 登出
     */
    public void logout() {
        String token = SecurityUtils.getTokenFromRequest();
        if (StringUtils.isNotBlank(token)) {
            // 将JWT令牌加入黑名单
            tokenService.invalidateToken(token);
            // 清除Security上下文
            SecurityContextHolder.clearContext();
        }
    }
}
