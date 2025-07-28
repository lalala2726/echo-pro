package cn.zhangchuangla.framework.security.login;

import cn.zhangchuangla.common.core.config.property.SecurityProperties;
import cn.zhangchuangla.common.core.entity.security.AuthTokenVo;
import cn.zhangchuangla.common.core.entity.security.SysUser;
import cn.zhangchuangla.common.core.enums.DeviceType;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.AuthorizationException;
import cn.zhangchuangla.common.core.exception.LoginException;
import cn.zhangchuangla.common.core.exception.ParamException;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.BeanCotyUtils;
import cn.zhangchuangla.common.core.utils.client.IPUtils;
import cn.zhangchuangla.common.core.utils.client.UserAgentUtils;
import cn.zhangchuangla.framework.async.AsyncLogService;
import cn.zhangchuangla.framework.model.dto.LoginDeviceDTO;
import cn.zhangchuangla.framework.model.dto.LoginSessionDTO;
import cn.zhangchuangla.framework.model.request.LoginRequest;
import cn.zhangchuangla.framework.model.request.RegisterRequest;
import cn.zhangchuangla.framework.security.device.DeviceLimiter;
import cn.zhangchuangla.framework.security.login.limiter.LoginFrequencyLimiter;
import cn.zhangchuangla.framework.security.login.limiter.PasswordRetryLimiter;
import cn.zhangchuangla.framework.security.token.RedisTokenStore;
import cn.zhangchuangla.framework.security.token.TokenService;
import cn.zhangchuangla.system.service.SysUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;

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
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AsyncLogService asyncLogService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SysUserService sysUserService;
    private final DeviceLimiter deviceLimiter;
    private final RedisTokenStore redisTokenStore;
    private final PasswordRetryLimiter passwordRetryLimiter;
    private final LoginFrequencyLimiter loginFrequencyLimiter;
    private final SecurityProperties securityProperties;

    /**
     * 实现登录逻辑
     *
     * @param request 请求参数
     * @return 令牌
     */
    public AuthTokenVo login(LoginRequest request, HttpServletRequest httpServletRequest) {
        String username = request.getUsername().trim();

        // 1. 检查用户是否被锁定（密码重试限制）
        passwordRetryLimiter.allowLogin(username);

        // 2. 检查登录频率限制（基于成功登录次数）
        loginFrequencyLimiter.checkFrequencyLimit(username);

        // 3. 创建用于密码认证的令牌（未认证）
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username, request.getPassword().trim());

        // 4. 执行认证（认证中）
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            log.error("用户名:{},登录失败！", username, e);

            // 记录密码重试失败次数
            passwordRetryLimiter.recordFailure(username);
            // 注意：这里不记录登录频率失败，因为登录频率限制只统计成功登录

            // 使用异步服务记录登录失败日志
            String ipAddr = IPUtils.getIpAddress(httpServletRequest);
            String userAgent = UserAgentUtils.getUserAgent(httpServletRequest);
            asyncLogService.recordLoginLog(username, ipAddr, userAgent, false);
            throw new LoginException("账号或密码错误!");
        }

        // 5. 认证成功后，清除密码重试记录并记录成功登录频率
        passwordRetryLimiter.clearRecord(username);
        loginFrequencyLimiter.recordLoginSuccess(username);

        // 6. 生成 JWT 令牌（但还未添加到会话管理中）
        LoginSessionDTO authSessionInfo = tokenService.createToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 7. 构建登录设备信息
        LoginDeviceDTO deviceInfo = LoginDeviceDTO.builder()
                .deviceType(request.getDeviceInfo() != null ? request.getDeviceInfo().getDeviceType().getValue() : DeviceType.UNKNOWN.getValue())
                .deviceName(request.getDeviceInfo() != null ? request.getDeviceInfo().getDeviceName() : "Unknown Device")
                .userId(authSessionInfo.getUserId())
                .refreshSessionId(authSessionInfo.getRefreshTokenSessionId())
                .username(authSessionInfo.getUsername())
                .ip(IPUtils.getIpAddress(httpServletRequest))
                .location(IPUtils.getRegion(IPUtils.getIpAddress(httpServletRequest)))
                .build();

        // 如果为了性能考虑，可以在 checkLimitAndAddSession 的第二个参数传入 false，
        // 表示在检查设备数量限制时跳过加锁。适用于单机部署且用户并发不高的场景。
        // 注意：跳过加锁可能会导致会话数量限制不准确，需根据实际业务场景权衡。
        try {
            deviceLimiter.checkLimitAndAddSession(deviceInfo);
        } catch (AuthorizationException e) {
            // 如果设备限制检查失败，需要清理已生成的token
            log.warn("设备限制检查失败，用户: {}, 设备类型: {}, 错误: {}",
                    username,
                    request.getDeviceInfo() != null ? request.getDeviceInfo().getDeviceType() : DeviceType.UNKNOWN,
                    e.getMessage());
            redisTokenStore.deleteRefreshTokenAndAccessToken(authSessionInfo.getRefreshTokenSessionId());
            throw e;
        }

        // 使用异步服务记录登录成功日志
        String ipAddr = IPUtils.getIpAddress(httpServletRequest);
        String userAgent = UserAgentUtils.getUserAgent(httpServletRequest);
        asyncLogService.recordLoginLog(username, ipAddr, userAgent, true);

        AuthTokenVo authTokenVo = BeanCotyUtils.copyProperties(authSessionInfo, AuthTokenVo.class);
        setAuthTokenInfo(authTokenVo);
        return authTokenVo;
    }

    /**
     * 注册用户
     *
     * @param request 请求参数
     * @return 用户ID
     */
    public Long register(@RequestBody RegisterRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new ParamException(ResultCode.PARAM_ERROR);
        }
        if (sysUserService.isUsernameExist(request.getUsername())) {
            throw new ServiceException(String.format("用户名%s已存在", request.getUsername()));
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(encodedPassword);
        user.setCreateTime(new Date());
        sysUserService.save(user);
        return user.getUserId();
    }


    /**
     * 设置认证令牌信息
     *
     * @param authTokenVo 认证令牌信息
     */
    public void setAuthTokenInfo(AuthTokenVo authTokenVo) {
        long refreshTokenExpireTime = securityProperties.getSession().getRefreshTokenExpireTime();
        long accessTokenExpireTime = securityProperties.getSession().getAccessTokenExpireTime();
        authTokenVo.setAccessTokenExpires(refreshTokenExpireTime);
        authTokenVo.setRefreshTokenExpires(accessTokenExpireTime);
        String tokenPrefix = securityProperties.getSession().getTokenPrefix();
        if (!tokenPrefix.isBlank()) {
            authTokenVo.setAccessToken(tokenPrefix + " " + authTokenVo.getAccessToken());
        }
    }
}
