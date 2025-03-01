package cn.zhangchuangla.framework.web.service.impl;

import cn.zhangchuangla.common.constant.SystemConstant;
import cn.zhangchuangla.common.core.model.entity.LoginUser;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.AccountException;
import cn.zhangchuangla.common.exception.ParamException;
import cn.zhangchuangla.common.utils.RegularUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.framework.model.request.LoginRequest;
import cn.zhangchuangla.framework.security.context.AuthenticationContextHolder;
import cn.zhangchuangla.framework.web.service.SysLoginService;
import cn.zhangchuangla.framework.web.service.SysPasswordService;
import cn.zhangchuangla.framework.web.service.TokenService;
import cn.zhangchuangla.system.service.SysLoginLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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

    public SysLoginServiceImpl(AuthenticationManager authenticationManager, TokenService tokenService, SysPasswordService sysPasswordService, SysLoginLogService sysLoginLogService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.sysPasswordService = sysPasswordService;
        this.sysLoginLogService = sysLoginLogService;
    }


    /**
     * 实现登录逻辑
     *
     * @param requestParams 请求参数
     * @return 令牌
     */
    @Override
    public String login(LoginRequest requestParams, HttpServletRequest httpServletRequest) {
        //fixme 临时关闭, 校验登录参数

//        loginParamsCheck(requestParams);

        Authentication authenticate = null;
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(requestParams.getUsername(), requestParams.getPassword());
            authenticate = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            log.warn("用户名:{},密码错误!", requestParams.getUsername());
            sysPasswordService.PasswordErrorCount(requestParams.getUsername());
            //记录登录失败日志
            sysLoginLogService.recordLoginLog(requestParams.getUsername(), httpServletRequest, SystemConstant.LOGIN_FAIL);
            throw new AccountException(ResponseCode.PASSWORD_FORMAT_ERROR, "用户名或密码错误");
        } finally {
            AuthenticationContextHolder.clearContext();
        }
        // 获取用户信息
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        Long userId = loginUser.getSysUser().getUserId();
        loginUser.setUserId(userId);
        log.info("登录用户信息: {}", loginUser);
        //记录登录成功日志
        sysLoginLogService.recordLoginLog(requestParams.getUsername(), httpServletRequest, SystemConstant.LOGIN_SUCCESS);
        return tokenService.createToken(loginUser, httpServletRequest);
    }

    /**
     * 登录参数校验
     *
     * @param requestParams 参数
     */
    private void loginParamsCheck(LoginRequest requestParams) {
        if (StringUtils.isEmpty(requestParams.getUsername())) {
            log.warn("用户名不能为空");
            throw new ParamException(ResponseCode.PARAM_ERROR, "用户名不能为空");
        }
        if (StringUtils.isEmpty(requestParams.getPassword())) {
            log.warn("密码不能为空");
            throw new ParamException(ResponseCode.PARAM_ERROR, "密码不能为空");
        }
        if (!RegularUtils.isUsernameValid(requestParams.getUsername())) {
            log.warn("用户名格式错误: {}", requestParams.getUsername());
            throw new ParamException(ResponseCode.PARAM_ERROR, "用户名格式错误");
        }
        if (!RegularUtils.isPasswordValid(requestParams.getPassword())) {
            log.warn("密码格式错误");
            throw new ParamException(ResponseCode.PARAM_ERROR, "密码格式错误");
        }
    }
}



