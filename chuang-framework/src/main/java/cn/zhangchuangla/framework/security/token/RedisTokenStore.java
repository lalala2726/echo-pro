package cn.zhangchuangla.framework.security.token;

import cn.zhangchuangla.common.core.config.property.SecurityProperties;
import cn.zhangchuangla.common.core.entity.security.OnlineLoginUser;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.AuthorizationException;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
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

    private final RedisCache redisCache;
    private final SecurityProperties securityProperties;


    /**
     * 保存访问令牌到Redis中
     *
     * @param accessTokenSession 访问令牌会话ID
     * @param onlineLoginUser    在线用户信息
     */
    public void setAccessToken(String accessTokenSession, OnlineLoginUser onlineLoginUser) {
        String accessTokenKey = RedisConstants.Auth.USER_ACCESS_TOKEN + accessTokenSession;
        redisCache.setCacheObject(accessTokenKey, onlineLoginUser,
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
        redisCache.setCacheObject(refreshTokenKey, accessTokenSession,
                securityProperties.getSession().getRefreshTokenExpireTime());
    }

    public OnlineLoginUser readAccessToken(String accessTokenSession) {
        String accessTokenKey = RedisConstants.Auth.USER_ACCESS_TOKEN + accessTokenSession;
        return redisCache.getCacheObject(accessTokenKey);
    }

    public String readRefreshToken(String refreshTokenSession) {
        String refreshTokenKey = RedisConstants.Auth.USER_REFRESH_TOKEN + refreshTokenSession;
        return redisCache.getCacheObject(refreshTokenKey);
    }

    public void deleteAccessToken(String accessTokenSession) {
        String accessTokenKey = RedisConstants.Auth.USER_ACCESS_TOKEN + accessTokenSession;
        redisCache.deleteObject(accessTokenKey);
    }

    public void deleteRefreshToken(String refreshTokenSession) {
        String refreshTokenKey = RedisConstants.Auth.USER_REFRESH_TOKEN + refreshTokenSession;
        redisCache.deleteObject(refreshTokenKey);
    }

    public boolean isValidAccessToken(String accessToken) {
        return redisCache.exists(RedisConstants.Auth.USER_ACCESS_TOKEN + accessToken);
    }

    public boolean isValidRefreshToken(String refreshToken) {
        return redisCache.exists(RedisConstants.Auth.USER_REFRESH_TOKEN + refreshToken);
    }

    public void mapRefreshTokenToAccessToken(String refreshTokenSession, String accessToken) {
        String refreshTokenKey = RedisConstants.Auth.USER_REFRESH_TOKEN + refreshTokenSession;
        boolean exists = redisCache.exists(refreshTokenKey);
        if (!exists) {
            throw new AuthorizationException(ResultCode.REFRESH_TOKEN_INVALID);
        }
        Long keyExpire = redisCache.getKeyExpire(refreshTokenKey);
        redisCache.setCacheObject(refreshTokenKey, accessToken, keyExpire);
    }


}
