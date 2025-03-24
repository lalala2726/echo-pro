package cn.zhangchuangla.infrastructure.web.service;

import cn.zhangchuangla.common.core.model.entity.LoginUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;


/**
 * @author Chuang
 * <p>
 * created on 2025/2/20 10:47
 */
public interface TokenService {

    String createToken(LoginUser loginUser, HttpServletRequest request);

    /**
     * 从Token中获取用户ID
     *
     * @param token Token字符串
     * @return 用户ID
     */
    String getUserIdFromToken(String token);

    /**
     * 验证Token
     *
     * @param loginUser 登录用户
     */
    void validateToken(LoginUser loginUser);

    /**
     * 刷新Token
     *
     * @param token 原Token字符串
     * @return 新的Token字符串
     */
    String refreshToken(String token);

    /**
     * 解析Token
     *
     * @param token Token字符串
     * @return Claims对象
     */
    Claims parseToken(String token);


    /**
     * 获取登录用户信息
     *
     * @param request 请求
     * @return 返回登录用户信息
     */
    LoginUser getLoginUser(HttpServletRequest request);

    /**
     * 获取Token
     *
     * @param request 请求
     * @return 返回token
     */
    String getToken(HttpServletRequest request);
}
