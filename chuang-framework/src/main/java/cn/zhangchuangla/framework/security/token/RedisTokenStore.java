package cn.zhangchuangla.framework.security.token;

import cn.zhangchuangla.common.core.config.property.SecurityProperties;
import cn.zhangchuangla.common.core.entity.security.OnlineLoginUser;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.AuthorizationException;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisKeyCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Redis中读取令牌操作
 *
 * @author Chuang
 * <p>
 * created on 2025/7/24 20:48
 */
@Component
@RequiredArgsConstructor
public class RedisTokenStore {

    private final RedisKeyCache redisKeyCache;
    private final SecurityProperties securityProperties;


    /**
     * 保存访问令牌到Redis中
     *
     * @param accessTokenSession 访问令牌会话ID
     * @param onlineLoginUser    在线用户信息
     */
    public void setAccessToken(String accessTokenSession, OnlineLoginUser onlineLoginUser) {
        String accessTokenKey = RedisConstants.Auth.USER_ACCESS_TOKEN + accessTokenSession;
        redisKeyCache.setCacheObject(accessTokenKey, onlineLoginUser,
                securityProperties.getSession().getAccessTokenExpireTime());
    }

    /**
     * 保存刷新令牌到Redis中
     *
     * @param refreshTokenSession 刷新令牌会话ID
     * @param accessTokenSession  访问令牌会话ID
     */
    public void setRefreshToken(String refreshTokenSession, String accessTokenSession) {
        String refreshTokenKey = RedisConstants.Auth.USER_REFRESH_TOKEN + refreshTokenSession;
        //保存刷新令牌
        redisKeyCache.setCacheObject(refreshTokenKey, accessTokenSession,
                securityProperties.getSession().getRefreshTokenExpireTime());
    }

    /**
     * 读取访问令牌
     *
     * @param accessTokenSession 访问令牌会话ID
     */
    public OnlineLoginUser readAccessToken(String accessTokenSession) {
        String accessTokenKey = RedisConstants.Auth.USER_ACCESS_TOKEN + accessTokenSession;
        return redisKeyCache.getCacheObject(accessTokenKey);
    }

    /**
     * 读取刷新令牌
     *
     * @param refreshTokenSession 刷新令牌会话ID
     */
    public String readRefreshToken(String refreshTokenSession) {
        String refreshTokenKey = RedisConstants.Auth.USER_REFRESH_TOKEN + refreshTokenSession;
        return redisKeyCache.getCacheObject(refreshTokenKey);
    }

    /**
     * 删除访问令牌
     *
     * @param accessTokenSession 访问令牌会话ID
     */
    public void deleteAccessToken(String accessTokenSession) {
        String accessTokenKey = RedisConstants.Auth.USER_ACCESS_TOKEN + accessTokenSession;
        redisKeyCache.deleteObject(accessTokenKey);
    }

    /**
     * 删除刷新令牌
     *
     * @param refreshTokenSession 刷新令牌会话ID
     */
    public void deleteRefreshToken(String refreshTokenSession) {
        String refreshTokenKey = RedisConstants.Auth.USER_REFRESH_TOKEN + refreshTokenSession;
        redisKeyCache.deleteObject(refreshTokenKey);
    }

    /**
     * 删除刷新令牌和关联的访问令牌
     *
     * @param refreshTokenSession 刷新令牌会话ID
     */
    public void deleteRefreshTokenAndAccessToken(String refreshTokenSession) {
        String refreshTokenKey = RedisConstants.Auth.USER_REFRESH_TOKEN + refreshTokenSession;
        redisKeyCache.deleteObject(refreshTokenKey);
        // 删除关联的访问令牌
        String accessToken = redisKeyCache.getCacheObject(refreshTokenKey);
        if (accessToken != null) {
            String accessTokenKey = RedisConstants.Auth.USER_ACCESS_TOKEN + accessToken;
            redisKeyCache.deleteObject(accessTokenKey);
        }
    }

    /**
     * 验证访问令牌是否有效
     *
     * @param accessToken 访问令牌
     */
    public boolean isValidAccessToken(String accessToken) {
        return redisKeyCache.exists(RedisConstants.Auth.USER_ACCESS_TOKEN + accessToken);
    }

    /**
     * 验证刷新令牌是否有效
     *
     * @param refreshToken 刷新令牌
     */
    public boolean isValidRefreshToken(String refreshToken) {
        return redisKeyCache.exists(RedisConstants.Auth.USER_REFRESH_TOKEN + refreshToken);
    }

    /**
     * 映射刷新令牌到访问令牌
     *
     * @param refreshTokenSession 刷新令牌会话ID
     */
    public void mapRefreshTokenToAccessToken(String refreshTokenSession, String newAccessToken) {
        String refreshKey = RedisConstants.Auth.USER_REFRESH_TOKEN + refreshTokenSession;
        // 1. 验证 refreshKey 存在
        if (!redisKeyCache.exists(refreshKey)) {
            throw new AuthorizationException(ResultCode.REFRESH_TOKEN_INVALID);
        }

        // 2. 取出旧的 accessToken
        String oldAccessToken = redisKeyCache.getCacheObject(refreshKey);
        if (oldAccessToken == null) {
            throw new AuthorizationException(ResultCode.REFRESH_TOKEN_INVALID);
        }

        // 3. 拿到剩余 TTL
        Long ttlSeconds = redisKeyCache.getKeyExpire(refreshKey);
        if (ttlSeconds == null || ttlSeconds <= 0) {
            throw new AuthorizationException(ResultCode.REFRESH_TOKEN_INVALID);
        }

        // 4. 删除旧的双向映射
        String oldAccessKey = RedisConstants.Auth.USER_ACCESS_TOKEN + oldAccessToken;
        if (redisKeyCache.exists(oldAccessKey)) {
            redisKeyCache.deleteObject(oldAccessKey);
        }
        // a) refresh → access
        redisKeyCache.setCacheObject(refreshKey, newAccessToken, ttlSeconds);
    }


}
