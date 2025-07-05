package cn.zhangchuangla.framework.security.token;


import cn.zhangchuangla.common.core.core.entity.security.AuthenticationToken;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;

/**
 * Token 管理器
 * <p>
 * 用于生成、解析、校验、刷新 Token
 *
 * @author Ray.Hao
 * @since 2.16.0
 */
public interface TokenManager {

    /**
     * 生成认证 Token
     *
     * @param authentication 用户认证信息
     * @return 认证 Token 响应
     */
    AuthenticationToken generateToken(Authentication authentication);

    /**
     * 解析 Token 获取认证信息
     *
     * @param token Token
     * @return 用户认证信息
     */
    Authentication parseToken(String token);

    /**
     * 校验 访问令牌 是否有效
     *
     * @param token JWT Token
     * @return 是否有效
     */
    boolean validateAccessToken(String token);

    /**
     * 校验刷新令牌是否有效
     *
     * @param token 刷新令牌
     * @return 是否有效 true 有效 false 无效
     */
    boolean validateRefreshToken(String token);

    /**
     * 刷新 Token
     *
     * @param refreshToken 刷新令牌
     * @return 认证 Token 响应
     */
    AuthenticationToken refreshToken(String refreshToken);

    /**
     * 令 Token 失效
     *
     * @param token JWT Token
     */
    default void invalidateToken(String token) {
        // 默认实现可以是空的，或者抛出不支持的操作异常
        // throw new UnsupportedOperationException("Not implemented");
    }

    Claims getClaimsFromToken(String token);
}
