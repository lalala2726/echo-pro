package cn.zhangchuangla.framework.web.service.impl;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.AccountException;
import cn.zhangchuangla.common.exception.ParamException;
import cn.zhangchuangla.common.utils.RegularUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.framework.model.entity.LoginUser;
import cn.zhangchuangla.framework.model.request.LoginRequest;
import cn.zhangchuangla.framework.web.service.SysLoginService;
import cn.zhangchuangla.framework.web.service.TokenService;
import cn.zhangchuangla.system.model.entity.SysPermissions;
import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.service.SysPermissionsService;
import cn.zhangchuangla.system.service.SysRoleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
    private final SysRoleService sysRoleService;
    private final SysPermissionsService sysPermissionsService;

    public SysLoginServiceImpl(AuthenticationManager authenticationManager, TokenService tokenService, SysRoleService sysRoleService, SysPermissionsService sysPermissionsService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.sysRoleService = sysRoleService;
        this.sysPermissionsService = sysPermissionsService;
    }


    /**
     * 实现登录逻辑
     *
     * @param requestParams 请求参数
     * @return 令牌
     */
    @Override
    public String login(LoginRequest requestParams, HttpServletRequest httpServletRequest) {

        loginParamsCheck(requestParams);
        //fixme 登录前校验
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestParams.getUsername(), requestParams.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //todo 密码错误次数校验
        if (Objects.isNull(authenticate)) {
            throw new AccountException(ResponseCode.LOGIN_ERROR, "账号或密码错误");
        }
        //获取用户信息
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        loginUser.setUserId(loginUser.getSysUser().getUserId());
        //设置角色和权限
        List<SysRole> roles = sysRoleService.getRoleListByUserId(loginUser.getSysUser().getUserId());
        loginUser.setRoles(roles);
        //获取用户权限
        List<SysPermissions> permissions = sysPermissionsService.getPermissionsByUserId(loginUser.getSysUser().getUserId());
        loginUser.setPermissions(permissions);
        //生成token
        log.info("登录用户信息:{}", loginUser);
        return tokenService.createToken(loginUser, httpServletRequest);
    }

    /**
     * 登录参数校验
     *
     * @param requestParams 参数
     */
    private void loginParamsCheck(LoginRequest requestParams) {
        if (StringUtils.isEmpty(requestParams.getUsername())) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "用户名不能为空");
        }
        if (StringUtils.isEmpty(requestParams.getPassword())) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "密码不能为空");
        }
        if (!RegularUtils.isUsernameValid(requestParams.getUsername())) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "用户名格式错误");
        }
        if (!RegularUtils.isPasswordValid(requestParams.getPassword())) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "密码格式错误");
        }
    }


}
