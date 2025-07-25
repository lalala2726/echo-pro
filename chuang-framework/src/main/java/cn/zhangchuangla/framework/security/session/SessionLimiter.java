package cn.zhangchuangla.framework.security.session;

import cn.zhangchuangla.common.core.config.property.SecurityProperties;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.AuthorizationException;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisHashCache;
import cn.zhangchuangla.common.redis.core.RedisKeyCache;
import cn.zhangchuangla.common.redis.core.RedisZSetCache;
import cn.zhangchuangla.framework.security.model.dto.LoginDeviceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录数量限制
 *
 * @author Chuang
 * <p>
 * created on 2025/7/24 22:06
 */
@Component
@RequiredArgsConstructor
public class SessionLimiter {

    private static final String DEVICE_TYPE = "device_type";
    private static final String DEVICE_NAME = "device_name";
    private static final String IP = "ip";
    private static final String LOCATION = "location";
    private static final String USER_ID = "user_id";
    private static final String LOGIN_TIME = "login_time";
    private final RedisKeyCache redisKeyCache;
    private final RedisHashCache redisHashCache;
    private final RedisZSetCache redisZSetCache;
    private final SecurityProperties securityProperties;

    /**
     * 检查登录设备数量限制
     *
     * @param userId     用户ID
     * @param deviceType 设备类型
     */
    public void checkLoginDeviceLimit(Long userId, String deviceType) {
        SecurityProperties.SessionConfig.MaxSessionsPerClient maxSessionsPerClient =
                securityProperties.getSession().getMaxSessionsPerClient();
        long limit = getLimit(deviceType, maxSessionsPerClient);
        if (limit > 0) {
            checkLoginCountLimit(userId, deviceType, limit);
        } else if (limit == 0) {
            throw new AuthorizationException(ResultCode.LOGIN_ERROR, "登录设备数量已达上限");
        }
    }


    /**
     * 添加登录设备
     *
     * @param loginDeviceDTO 登录设备信息
     */
    public void addSession(@Validated LoginDeviceDTO loginDeviceDTO) {
        long now = System.currentTimeMillis();
        long refreshTokenExpireTime = securityProperties.getSession().getRefreshTokenExpireTime();

        Map<String, Object> deviceInfo = Map.of(
                DEVICE_TYPE, loginDeviceDTO.getDeviceType(),
                DEVICE_NAME, loginDeviceDTO.getDeviceName(),
                LOGIN_TIME, now
        );
        String hashKey = RedisConstants.Auth.SESSIONS_KEY + loginDeviceDTO.getRefreshSessionId();
        redisHashCache.hPutAll(hashKey, deviceInfo);
        // 设置会话有效期
        redisKeyCache.expire(hashKey, refreshTokenExpireTime, TimeUnit.SECONDS);

        //2.写入ZSet索引
        String zKey = RedisConstants.Auth.SESSIONS_KEY + loginDeviceDTO.getUserId();
        // 每次登录时更新 ZSet 的过期时间。如果用户在刷新令牌的有效期内未登录，
        // 则该索引将在最长令牌有效期后自动过期。此外，定时任务也会清理过期的索引。
        redisZSetCache.zAdd(zKey, loginDeviceDTO.getRefreshSessionId(), now, refreshTokenExpireTime, TimeUnit.SECONDS);
    }


    /**
     * 检查登录数量限制
     *
     * @param userId     用户ID
     * @param deviceType 设备类型
     * @param limit      限制数量
     */
    public void checkLoginCountLimit(Long userId, String deviceType, final long limit) {

    }


    private long getLimit(String deviceType, SecurityProperties.SessionConfig.MaxSessionsPerClient maxSessionsPerClient) {
        Map<String, Long> limits = Map.of(
                "pc", maxSessionsPerClient.getPc(),
                "mobile", maxSessionsPerClient.getMobile(),
                "web", maxSessionsPerClient.getWeb(),
                "miniProgram", maxSessionsPerClient.getMiniProgram(),
                "unknown", maxSessionsPerClient.getUnknown()
        );
        return limits.getOrDefault(deviceType, maxSessionsPerClient.getUnknown());
    }


}
