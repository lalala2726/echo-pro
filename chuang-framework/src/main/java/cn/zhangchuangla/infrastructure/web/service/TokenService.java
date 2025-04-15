package cn.zhangchuangla.infrastructure.web.service;

import cn.zhangchuangla.common.core.security.model.SysUserDetails;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/20 10:47
 */
public interface TokenService {

    String createToken(SysUserDetails sysUserDetails, HttpServletRequest request);

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
     * @param sysUserDetails 登录用户
     */
    void validateToken(SysUserDetails sysUserDetails);

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
    SysUserDetails getLoginUser(HttpServletRequest request);

    /**
     * 获取Token
     *
     * @param request 请求
     * @return 返回token
     */
    String getToken(HttpServletRequest request);

    /**
     * 检查Token状态
     *
     * @param token Token字符串
     * @return Token状态
     */
    TokenStatus checkTokenStatus(String token);

    /**
     * Token校验结果枚举
     */
    enum TokenStatus {
        /**
         * 有效的Token
         */
        VALID,

        /**
         * 过期的Token（能解析但Redis中无数据）
         */
        EXPIRED,

        /**
         * 非法Token（无法解析）
         */
        INVALID,

        /**
         * 未知错误
         */
        ERROR
    }
}
