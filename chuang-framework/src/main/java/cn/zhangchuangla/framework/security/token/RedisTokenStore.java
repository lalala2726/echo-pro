package cn.zhangchuangla.framework.security.token;

import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.AuthorizationException;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.framework.model.vo.OnlineLoginUser;
import cn.zhangchuangla.framework.security.property.SecurityProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RedisTokenStore {

    private final RedisCache redisCache;
    private final SecurityProperties securityProperties;


    /**
     * 保存访问令牌到Redis中
     *
     * @param accessTokenId   访问令牌会话ID
     * @param onlineLoginUser 在线用户信息
     */
    protected void setAccessToken(String accessTokenId, OnlineLoginUser onlineLoginUser) {
        String accessTokenKey = RedisConstants.Auth.USER_ACCESS_TOKEN + accessTokenId;
        redisCache.setCacheObject(accessTokenKey, onlineLoginUser,
                securityProperties.getSession().getAccessTokenExpireTime());
    }

    /**
     * 保存刷新令牌到Redis中
     *
     * @param refreshTokenId 刷新令牌会话ID
     * @param accessTokenId  访问令牌会话ID
     */
    protected void setRefreshToken(String refreshTokenId, String accessTokenId) {
        String refreshTokenRedisKey = RedisConstants.Auth.USER_REFRESH_TOKEN + refreshTokenId;
        //保存刷新令牌
        redisCache.setCacheObject(refreshTokenRedisKey, accessTokenId,
                securityProperties.getSession().getRefreshTokenExpireTime());
    }

    /**
     * 读取访问令牌
     *
     * @param accessTokenId 访问令牌会话ID
     */
    public OnlineLoginUser getAccessToken(String accessTokenId) {
        String accessTokenRedisKey = RedisConstants.Auth.USER_ACCESS_TOKEN + accessTokenId;
        return redisCache.getCacheObject(accessTokenRedisKey);
    }

    /**
     * 读取刷新令牌
     *
     * @param refreshTokenId 刷新令牌会话ID
     * @return 访问令牌ID
     */
    public String getRefreshToken(String refreshTokenId) {
        String refreshTokenRedisKey = RedisConstants.Auth.USER_REFRESH_TOKEN + refreshTokenId;
        return redisCache.getCacheObject(refreshTokenRedisKey);
    }

    /**
     * 删除访问令牌
     *
     * @param accessTokenId 访问令牌会话ID
     */
    public void deleteAccessToken(String accessTokenId) {
        String accessTokenRedisKey = RedisConstants.Auth.USER_ACCESS_TOKEN + accessTokenId;
        redisCache.deleteObject(accessTokenRedisKey);
    }

    /**
     * 删除刷新令牌
     *
     * @param refreshTokenId 刷新令牌会话ID
     */
    public void deleteRefreshToken(String refreshTokenId) {
        String refreshTokenRedisKey = RedisConstants.Auth.USER_REFRESH_TOKEN + refreshTokenId;
        redisCache.deleteObject(refreshTokenRedisKey);
    }

    /**
     * 删除刷新令牌和关联的访问令牌
     *
     * @param refreshTokenId 刷新令牌会话ID
     */
    public void deleteRefreshTokenAndAccessToken(String refreshTokenId) {
        String refreshTokenRedisKey = RedisConstants.Auth.USER_REFRESH_TOKEN + refreshTokenId;
        // 删除关联的访问令牌
        String accessToken = redisCache.getCacheObject(refreshTokenRedisKey);
        if (accessToken != null) {
            String accessTokenKey = RedisConstants.Auth.USER_ACCESS_TOKEN + accessToken;
            redisCache.deleteObject(accessTokenKey);
        }
        redisCache.deleteObject(refreshTokenRedisKey);
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
        String accessTokenRedisKey = RedisConstants.Auth.USER_ACCESS_TOKEN + accessTokenId;
        OnlineLoginUser onlineLoginUser = redisCache.getCacheObject(accessTokenRedisKey);
        Assert.notNull(onlineLoginUser, "访问令牌不存在!");
        return onlineLoginUser.getRefreshTokenId();
    }


    /**
     * 映射刷新令牌到访问令牌
     *
     * @param refreshTokenId 刷新令牌会话ID
     */
    public void mapRefreshTokenToAccessToken(String refreshTokenId, String newAccessToken) {
        String refreshKey = RedisConstants.Auth.USER_REFRESH_TOKEN + refreshTokenId;
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


    /**
     * 更新访问令牌时间
     *
     * @param accessTokenId 访问令牌ID
     * @return 是否更新成功
     */
    public boolean updateAccessTime(String accessTokenId) {
        String accessTokenRedisKey = RedisConstants.Auth.USER_ACCESS_TOKEN + accessTokenId;
        OnlineLoginUser onlineLoginUser = redisCache.getCacheObject(accessTokenRedisKey);

        // 检查令牌是否存在
        if (onlineLoginUser == null) {
            log.warn("尝试更新访问时间时，令牌不存在: {}", accessTokenId);
            return false;
        }

        Long expire = redisCache.getExpire(accessTokenRedisKey);
        if (expire <= 0) {
            log.warn("尝试更新访问时间时，令牌已过期: {}", accessTokenId);
            return false;
        }

        onlineLoginUser.setAccessTime(System.currentTimeMillis());
        redisCache.setCacheObject(accessTokenRedisKey, onlineLoginUser, expire);
        return true;
    }
}
