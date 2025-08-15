package cn.zhangchuangla.framework.security.login;

import cn.zhangchuangla.common.core.entity.security.SysUser;
import cn.zhangchuangla.common.core.enums.BusinessType;
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
import cn.zhangchuangla.framework.model.vo.AuthTokenVo;
import cn.zhangchuangla.framework.security.device.DeviceLimiter;
import cn.zhangchuangla.framework.security.login.limiter.LoginFrequencyLimiter;
import cn.zhangchuangla.framework.security.login.limiter.PasswordRetryLimiter;
import cn.zhangchuangla.framework.security.token.RedisTokenStore;
import cn.zhangchuangla.framework.security.token.TokenService;
import cn.zhangchuangla.system.core.model.entity.SysSecurityLog;
import cn.zhangchuangla.system.core.service.CaptchaService;
import cn.zhangchuangla.system.core.service.SysUserService;
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
    private final CaptchaService captchaService;

    /**
     * 实现登录逻辑
     *
     * <p>流程：
     * 1) 校验登录约束（密码重试、频率限制）
     * 2) 认证
     * 3) 构造设备信息
     * 4) 生成会话并绑定上下文
     * 5) 校验设备限制，必要时回滚令牌
     * 6) 记录登录与安全日志
     * </p>
     *
     * @param request            登录请求参数
     * @param httpServletRequest 原始 HTTP 请求（用于提取 IP、UA 等）
     * @return 授权令牌视图
     * @throws LoginException 认证失败时抛出
     */
    public AuthTokenVo login(LoginRequest request, HttpServletRequest httpServletRequest) {
        String username = request.getUsername().trim();

        // 0) 图形验证码校验（大小写不敏感）
        verifyImageCaptcha(request);

        // 1) 校验登录约束
        validateLoginConstraints(username);

        // 2) 认证
        Authentication authentication = doAuthenticate(username, request.getPassword().trim(), httpServletRequest);

        // 3) 构造设备信息
        LoginDeviceDTO loginDeviceDTO = buildLoginDeviceDTO(request, httpServletRequest);

        // 4) 生成会话并绑定上下文
        LoginSessionDTO session = createSessionAndBindContext(authentication, loginDeviceDTO);
        enrichDeviceDTOWithSession(loginDeviceDTO, session, username);

        // 5) 校验设备限制并在失败时回滚令牌
        enforceDeviceLimitOrRollback(loginDeviceDTO, session);

        // 6) 记录登录与安全日志
        recordLoginSuccessLogs(username, httpServletRequest);

        return BeanCotyUtils.copyProperties(session, AuthTokenVo.class);
    }

    /**
     * 校验登录约束：密码重试限制与登录频率限制。
     *
     * @param username 用户名
     * @throws AuthorizationException 当不满足策略限制时抛出
     */
    private void validateLoginConstraints(String username) {
        // 检查用户是否被锁定（密码重试限制）
        passwordRetryLimiter.allowLogin(username);
        // 检查登录频率限制（基于成功登录次数）
        loginFrequencyLimiter.checkFrequencyLimit(username);
    }

    /**
     * 执行认证流程。如果失败，会记录失败次数与失败日志，并抛出 LoginException。
     *
     * @param username    用户名
     * @param rawPassword 原始密码
     * @param request     HTTP 请求（用于提取 IP、UA）
     * @return 认证对象
     * @throws LoginException 认证失败时抛出
     */
    private Authentication doAuthenticate(String username, String rawPassword, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, rawPassword);
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            // 成功后清理失败记录并记录成功频率
            passwordRetryLimiter.clearRecord(username);
            loginFrequencyLimiter.recordLoginSuccess(username);
            return authentication;
        } catch (Exception e) {
            log.error("用户名:{},登录失败！", username, e);
            passwordRetryLimiter.recordFailure(username);
            String ipAddr = IPUtils.getIpAddress(request);
            String userAgent = UserAgentUtils.getUserAgent(request);
            asyncLogService.recordLoginLog(username, ipAddr, userAgent, false);
            throw e;
        }
    }

    /**
     * 构造登录设备信息。
     *
     * @param loginRequest 登录请求
     * @param request      HTTP 请求（用于提取 IP、UA、位置信息等）
     * @return 登录设备信息 DTO
     */
    private LoginDeviceDTO buildLoginDeviceDTO(LoginRequest loginRequest, HttpServletRequest request) {
        LoginDeviceDTO loginDeviceDTO = new LoginDeviceDTO();
        String browserName = UserAgentUtils.getBrowserName(request);
        loginDeviceDTO.setDeviceName(browserName != null ? browserName : "Unknown Device");
        loginDeviceDTO.setDeviceType(loginRequest.getDeviceType() != null ? loginRequest.getDeviceType().getValue() : DeviceType.UNKNOWN.getValue());
        String ip = IPUtils.getIpAddress(request);
        loginDeviceDTO.setIp(ip);
        loginDeviceDTO.setLocation(IPUtils.getRegion(ip));
        loginDeviceDTO.setUserAgent(UserAgentUtils.getUserAgent(request));
        return loginDeviceDTO;
    }

    /**
     * 生成会话令牌并绑定到 Spring Security 上下文。
     *
     * @param authentication 认证对象
     * @param loginDeviceDTO 登录设备信息
     * @return 登录会话信息
     */
    private LoginSessionDTO createSessionAndBindContext(Authentication authentication, LoginDeviceDTO loginDeviceDTO) {
        LoginSessionDTO session = tokenService.createToken(authentication, loginDeviceDTO);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return session;
    }

    /**
     * 将会话关键字段补充回设备信息对象中，便于后续设备限制检查。
     *
     * @param loginDeviceDTO 设备信息
     * @param session        登录会话信息
     * @param username       用户名
     */
    private void enrichDeviceDTOWithSession(LoginDeviceDTO loginDeviceDTO, LoginSessionDTO session, String username) {
        loginDeviceDTO.setRefreshSessionId(session.getRefreshTokenSessionId());
        loginDeviceDTO.setUsername(username);
        loginDeviceDTO.setUserId(session.getUserId());
    }

    /**
     * 校验设备限制，若不通过则删除已生成的令牌并向上抛出异常。
     *
     * <p>注意：如需在单机、低并发下跳过加锁，可在限流实现中按需调整。</p>
     *
     * @param loginDeviceDTO 设备信息
     * @param session        登录会话信息
     * @throws AuthorizationException 当设备数量超限时抛出
     */
    private void enforceDeviceLimitOrRollback(LoginDeviceDTO loginDeviceDTO, LoginSessionDTO session) {
        try {
            deviceLimiter.checkLimitAndAddSession(loginDeviceDTO);
        } catch (AuthorizationException e) {
            redisTokenStore.deleteRefreshTokenAndAccessToken(session.getRefreshTokenSessionId());
            throw e;
        }
    }

    /**
     * 记录登录成功相关日志（登录日志与安全日志）。
     *
     * @param username 用户名
     * @param request  HTTP 请求（用于提取 IP、UA）
     */
    private void recordLoginSuccessLogs(String username, HttpServletRequest request) {
        String ipAddr = IPUtils.getIpAddress(request);
        String userAgent = UserAgentUtils.getUserAgent(request);
        asyncLogService.recordLoginLog(username, ipAddr, userAgent, true);
        recordLoginSecurityLog(username, ipAddr);
    }

    /**
     * 校验图形验证码，大小写不敏感。
     *
     * @param request 登录请求，需包含 uuid 与 code
     * @throws ParamException 当验证码无效或校验失败时抛出
     */
    private void verifyImageCaptcha(LoginRequest request) {
        String uuid = request.getUuid();
        String codeUpper = request.getCode() == null ? null : request.getCode().toUpperCase();
        boolean ok = captchaService.verifyImageCode(uuid, codeUpper);
        if (!ok) {
            throw new ParamException(ResultCode.PARAM_ERROR, "验证码错误");
        }
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
     * 手动记录登录安全日志
     * <p>
     * 用于记录用户登录成功的安全日志，这是一个重要的安全事件，
     * 需要单独记录以便进行安全审计和异常行为分析。
     * </p>
     *
     * @param username 用户名
     * @param ipAddr   登录IP地址
     */
    private void recordLoginSecurityLog(String username, String ipAddr) {
        try {
            // 获取用户ID
            Long userId = null;
            try {
                SysUser user = sysUserService.getUserInfoByUsername(username);
                if (user != null) {
                    userId = user.getUserId();
                }
            } catch (Exception e) {
                log.warn("获取用户ID失败，用户名: {}, 异常: {}", username, e.getMessage());
            }

            // 构建安全日志对象
            SysSecurityLog securityLog = new SysSecurityLog();
            securityLog.setUserId(userId);
            securityLog.setTitle("用户登录");
            securityLog.setOperationType(BusinessType.LOGIN.name());
            securityLog.setOperationIp(ipAddr);
            securityLog.setOperationRegion(IPUtils.getRegion(ipAddr));
            securityLog.setOperationTime(new Date());

            // 使用异步服务记录安全日志
            asyncLogService.recordSecurityLog(securityLog);
        } catch (Exception e) {
            log.error("记录登录安全日志时发生异常，用户: {}, IP: {}, 异常: {}", username, ipAddr, e.getMessage(), e);
        }
    }

}
