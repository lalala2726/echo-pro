package cn.zhangchuangla.framework.security.token;

import cn.zhangchuangla.common.core.config.property.SecurityProperties;
import cn.zhangchuangla.common.core.entity.security.OnlineLoginUser;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.AuthorizationException;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

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
    protected void setAccessToken(String accessTokenSession, OnlineLoginUser onlineLoginUser) {
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
    protected void setRefreshToken(String refreshTokenSession, String accessTokenSession) {
        String refreshTokenKey = RedisConstants.Auth.USER_REFRESH_TOKEN + refreshTokenSession;
        //保存刷新令牌
        redisCache.setCacheObject(refreshTokenKey, accessTokenSession,
                securityProperties.getSession().getRefreshTokenExpireTime());
    }

    /**
     * 读取访问令牌
     *
     * @param accessTokenSession 访问令牌会话ID
     */
    public OnlineLoginUser getAccessToken(String accessTokenSession) {
        String accessTokenKey = RedisConstants.Auth.USER_ACCESS_TOKEN + accessTokenSession;
        return redisCache.getCacheObject(accessTokenKey);
    }

    /**
     * 读取刷新令牌
     *
     * @param refreshTokenSession 刷新令牌会话ID
     * @return 访问令牌ID
     */
    public String getRefreshToken(String refreshTokenSession) {
        String refreshTokenKey = RedisConstants.Auth.USER_REFRESH_TOKEN + refreshTokenSession;
        return redisCache.getCacheObject(refreshTokenKey);
    }

    /**
     * 删除访问令牌
     *
     * @param accessTokenSession 访问令牌会话ID
     */
    public void deleteAccessToken(String accessTokenSession) {
        String accessTokenKey = RedisConstants.Auth.USER_ACCESS_TOKEN + accessTokenSession;
        redisCache.deleteObject(accessTokenKey);
    }

    /**
     * 删除刷新令牌
     *
     * @param refreshTokenSession 刷新令牌会话ID
     */
    public void deleteRefreshToken(String refreshTokenSession) {
        String refreshTokenKey = RedisConstants.Auth.USER_REFRESH_TOKEN + refreshTokenSession;
        redisCache.deleteObject(refreshTokenKey);
    }

    /**
     * 删除刷新令牌和关联的访问令牌
     *
     * @param refreshTokenSession 刷新令牌会话ID
     */
    public void deleteRefreshTokenAndAccessToken(String refreshTokenSession) {
        String refreshTokenKey = RedisConstants.Auth.USER_REFRESH_TOKEN + refreshTokenSession;
        redisCache.deleteObject(refreshTokenKey);
        // 删除关联的访问令牌
        String accessToken = redisCache.getCacheObject(refreshTokenKey);
        if (accessToken != null) {
            String accessTokenKey = RedisConstants.Auth.USER_ACCESS_TOKEN + accessToken;
            redisCache.deleteObject(accessTokenKey);
        }
    }

    /**
     * 验证访问令牌是否有效
     *
     * @param accessToken 访问令牌
     */
    public boolean isValidAccessToken(String accessToken) {
        return redisCache.exists(RedisConstants.Auth.USER_ACCESS_TOKEN + accessToken);
    }

    /**
     * 验证刷新令牌是否有效
     *
     * @param refreshToken 刷新令牌
     */
    public boolean isValidRefreshToken(String refreshToken) {
        return redisCache.exists(RedisConstants.Auth.USER_REFRESH_TOKEN + refreshToken);
    }


    /**
     * 获取刷新令牌对应的访问令牌ID
     *
     * @param accessTokenId 访问令牌ID
     * @return 刷新令牌ID
     */
    public String getRefreshTokenIdByAccessTokenId(String accessTokenId) {
        Assert.hasText(accessTokenId, "访问令牌ID不能为空!");
        String refreshTokenRedisKey = RedisConstants.Auth.USER_REFRESH_TOKEN + "*";
        Map<String, Object> stringObjectMap = redisCache.scanKeysWithValues(refreshTokenRedisKey);

        for (Map.Entry<String, Object> entry : stringObjectMap.entrySet()) {
            if (entry.getValue() instanceof String value && accessTokenId.equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }


    /**
     * 映射刷新令牌到访问令牌
     *
     * @param refreshTokenSession 刷新令牌会话ID
     */
    public void mapRefreshTokenToAccessToken(String refreshTokenSession, String newAccessToken) {
        String refreshKey = RedisConstants.Auth.USER_REFRESH_TOKEN + refreshTokenSession;
        // 1. 验证 refreshKey 存在
        if (!redisCache.exists(refreshKey)) {
            throw new AuthorizationException(ResultCode.REFRESH_TOKEN_INVALID);
        }

        // 2. 取出旧的 accessToken
        String oldAccessToken = redisCache.getCacheObject(refreshKey);
        if (oldAccessToken == null) {
            throw new AuthorizationException(ResultCode.REFRESH_TOKEN_INVALID);
        }

        // 3. 拿到剩余 TTL
        long ttlSeconds = redisCache.getKeyExpire(refreshKey);
        if (ttlSeconds <= 0) {
            throw new AuthorizationException(ResultCode.REFRESH_TOKEN_INVALID);
        }

        // 4. 删除旧的双向映射
        String oldAccessKey = RedisConstants.Auth.USER_ACCESS_TOKEN + oldAccessToken;
        if (redisCache.exists(oldAccessKey)) {
            redisCache.deleteObject(oldAccessKey);
        }
        // a) refresh → access
        redisCache.setCacheObject(refreshKey, newAccessToken, ttlSeconds);
    }


}
