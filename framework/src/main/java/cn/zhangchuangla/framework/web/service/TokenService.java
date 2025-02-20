package cn.zhangchuangla.framework.web.service;

import io.jsonwebtoken.Claims;

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
}
