package cn.zhangchuangla.framework.web.service;

import cn.zhangchuangla.common.core.entity.security.AuthTokenVo;
import cn.zhangchuangla.common.core.entity.security.SysUser;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.AuthorizationException;
import cn.zhangchuangla.common.core.exception.ParamException;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.client.IPUtils;
import cn.zhangchuangla.common.core.utils.client.UserAgentUtils;
import cn.zhangchuangla.framework.model.request.LoginRequest;
import cn.zhangchuangla.framework.model.request.RegisterRequest;
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
    private final AsyncService asyncService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SysUserService sysUserService;

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
     * 注册用户
     *
     * @param request 请求参数
     * @return 用户ID
     */
    public Long register(RegisterRequest request) {
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
        // 0-正常 1-停用
        user.setStatus(0);
        sysUserService.save(user);
        return user.getUserId();
    }
}
