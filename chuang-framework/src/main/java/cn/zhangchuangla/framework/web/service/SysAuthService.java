package cn.zhangchuangla.framework.web.service;

import cn.zhangchuangla.common.core.core.security.model.AuthenticationToken;
import cn.zhangchuangla.common.core.core.security.model.RefreshTokenRequest;
import cn.zhangchuangla.framework.model.request.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;


/**
 * @author Chuang
 * <p>
 * created on 2025/2/20 10:46
 */
public interface SysAuthService {

    /**
     * 登录
     *
     * @param loginRequest 登录参数
     * @return 返回token
     */
    AuthenticationToken login(LoginRequest loginRequest, HttpServletRequest request);


    /**
     * 刷新token
     *
     * @param refreshToken 刷新令牌
     * @return 认证 Token 响应
     */
    AuthenticationToken refreshToken(RefreshTokenRequest refreshToken);


    /**
     * 注销登录
     */
    void logout();

}
