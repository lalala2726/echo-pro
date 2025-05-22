package cn.zhangchuangla.framework.web.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.zhangchuangla.common.core.core.security.model.AuthenticationToken;
import cn.zhangchuangla.common.core.core.security.model.RefreshTokenRequest;
import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.common.core.utils.client.IPUtils;
import cn.zhangchuangla.common.core.utils.client.UserAgentUtils;
import cn.zhangchuangla.framework.manager.AsyncManager;
import cn.zhangchuangla.framework.manager.factory.AsyncFactory;
import cn.zhangchuangla.framework.model.request.LoginRequest;
import cn.zhangchuangla.framework.security.token.TokenManager;
import cn.zhangchuangla.framework.web.service.SysAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class SysAuthServiceImpl implements SysAuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenManager tokenManager;

    /**
     * 实现登录逻辑
     *
     * @param request 请求参数
     * @return 令牌
     */
    @Override
    public AuthenticationToken login(LoginRequest request, HttpServletRequest httpServletRequest) {
        // 1. 创建用于密码认证的令牌（未认证）
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getUsername().trim(), request.getPassword().trim());

        // 2. 执行认证（认证中）
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            log.error("用户名:{},登录失败！", request.getUsername(), e);
            // 使用异步工厂记录登录失败日志
            String ipAddr = IPUtils.getIpAddr(httpServletRequest);
            String userAgent = UserAgentUtils.getUserAgent(httpServletRequest);
            AsyncManager.me().execute(AsyncFactory.recordLoginLog(request.getUsername(), ipAddr, userAgent, false));
            throw e;
        }

        // 3. 认证成功后生成 JWT 令牌，并存入 Security 上下文，供登录日志 AOP 使用（已认证）
        AuthenticationToken authenticationTokenResponse = tokenManager.generateToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 使用异步工厂记录登录成功日志
        String ipAddr = IPUtils.getIpAddr(httpServletRequest);
        String userAgent = UserAgentUtils.getUserAgent(httpServletRequest);
        AsyncManager.me().execute(AsyncFactory.recordLoginLog(request.getUsername(), ipAddr, userAgent, true));

        return authenticationTokenResponse;
    }

    /**
     * 刷新令牌
     *
     * @param request 刷新令牌
     * @return 认证 Token 响应
     */
    @Override
    public AuthenticationToken refreshToken(RefreshTokenRequest request) {
        // 验证刷新令牌
        boolean isValidate = tokenManager.validateRefreshToken(request.getRefreshToken());

        if (!isValidate) {
            throw new ServiceException(ResponseCode.REFRESH_TOKEN_INVALID);
        }
        // 刷新令牌有效，生成新的访问令牌
        return tokenManager.refreshToken(request.getRefreshToken());
    }

    /**
     * 登出
     */
    @Override
    public void logout() {
        String token = SecurityUtils.getTokenFromRequest();
        if (StrUtil.isNotBlank(token)) {
            // 将JWT令牌加入黑名单
            tokenManager.invalidateToken(token);
            // 清除Security上下文
            SecurityContextHolder.clearContext();
        }
    }
}
