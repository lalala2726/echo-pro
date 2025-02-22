package cn.zhangchuangla.framework.web.service;

import cn.zhangchuangla.framework.model.entity.LoginUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/20 10:47
 */
public interface TokenService {
    /**
     * 创建Token
     *
     * @param userId 用户ID
     * @return token字符串
     */
    String createToken(String userId);

    /**
     * 从Token中获取用户ID
     *
     * @param token Token字符串
     * @return 用户ID
     */
    String getUserIdFromToken(String token);

    /**
     * 验证Token是否有效
     *
     * @param token Token字符串
     * @return 是否有效
     */
    boolean validateToken(String token);

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
