package cn.zhangchuangla.framework.security.session;

import cn.zhangchuangla.common.core.config.property.SecurityProperties;
import cn.zhangchuangla.common.core.enums.DeviceType;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.AuthorizationException;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisHashCache;
import cn.zhangchuangla.common.redis.core.RedisKeyCache;
import cn.zhangchuangla.common.redis.core.RedisZSetCache;
import cn.zhangchuangla.framework.security.model.dto.LoginDeviceDTO;
import cn.zhangchuangla.framework.security.token.RedisTokenStore;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 登录数量限制
 *
 * @author Chuang
 * <p>
 * created on 2025/7/24 22:06
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SessionLimiter {

    private static final String DEVICE_TYPE = "device_type";
    private static final String DEVICE_NAME = "device_name";
    private static final String LOGIN_TIME = "login_time";

    // 用户级别的锁，确保同一用户的会话操作是线程安全的
    private final ConcurrentHashMap<String, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    private final RedisKeyCache redisKeyCache;
    private final RedisHashCache redisHashCache;
    private final RedisZSetCache redisZSetCache;
    private final SecurityProperties securityProperties;
    private final RedisTokenStore redisTokenStore;

    /**
     * 获取用户对应的锁
     *
     * @param username 用户名
     * @return 用户对应的锁
     */
    private ReentrantLock getUserLock(String username) {
        return userLocks.computeIfAbsent(username, k -> new ReentrantLock());
    }


    /**
     * 检查登录设备数量限制并添加会话（原子操作）
     *
     * @param loginDeviceDTO 登录设备信息
     */
    public void checkLimitAndAddSession(@Validated LoginDeviceDTO loginDeviceDTO) {
        checkLimitAndAddSession(loginDeviceDTO, true);
    }

    /**
     * 检查登录设备数量限制并添加会话（原子操作）
     *
     * @param loginDeviceDTO 登录设备信息
     * @param needLock       是否需要加锁，true-加锁，false-不加锁
     */
    public void checkLimitAndAddSession(@Validated LoginDeviceDTO loginDeviceDTO, boolean needLock) {
        if (needLock) {
            ReentrantLock lock = getUserLock(loginDeviceDTO.getUsername());
            lock.lock();
            try {
                handelCheckLimitAndAddSession(loginDeviceDTO);
            } finally {
                lock.unlock();
            }
        } else {
            handelCheckLimitAndAddSession(loginDeviceDTO);
        }
    }

    /**
     * 执行检查限制和添加会话的核心逻辑
     *
     * @param loginDeviceDTO 登录设备信息
     */
    private void handelCheckLimitAndAddSession(@NotNull LoginDeviceDTO loginDeviceDTO) {
        // 先清理过期的会话
        cleanOldIndex(loginDeviceDTO.getUsername());

        // 检查登录数量限制
        long limit = getLimit(loginDeviceDTO.getDeviceType());
        if (limit == 0) {
            throw new AuthorizationException(ResultCode.LOGIN_ERROR, String.format("暂不支持:%s 设备登录", loginDeviceDTO.getDeviceType()));
        }

        if (limit > 0) {
            // 统计当前设备数量
            long currentCount = countCurrentSessions(loginDeviceDTO.getUsername(), loginDeviceDTO.getDeviceType());
            if (currentCount >= limit) {
                clearEarliestSession(loginDeviceDTO.getUsername());
            }
        }

        // 检查通过后，立即添加会话
        addSessionInternal(loginDeviceDTO);
    }


    /**
     * 清除当前用户设备中最早的会话
     *
     * @param username 用户名
     */
    private void clearEarliestSession(String username) {
        String deviceIndexRedisKey = RedisConstants.Auth.SESSIONS_INDEX_KEY + username;
        Set<ZSetOperations.TypedTuple<String>> allDeviceIndexSet = redisZSetCache.getAllWithScore(deviceIndexRedisKey);

        if (allDeviceIndexSet != null && !allDeviceIndexSet.isEmpty()) {
            Optional<ZSetOperations.TypedTuple<String>> earliestSession = allDeviceIndexSet.stream()
                    .min(Comparator.comparing(
                            ZSetOperations.TypedTuple::getScore,
                            Comparator.nullsLast(Double::compareTo)
                    ));

            // 如果找到了最早的会话，并且其分值不为 null，则从 Redis ZSet 中移除它
            earliestSession.ifPresent(tuple -> {
                String refreshTokenId = tuple.getValue();
                Double score = tuple.getScore(); // 获取得分
                String deviceRedisKey = RedisConstants.Auth.SESSIONS_DEVICE_KEY + refreshTokenId;
                // 额外检查 score 是否为 null，虽然 nullsLast 已经处理了比较，但移除前再次确认更好
                if (refreshTokenId != null && score != null) {
                    // 从索引中拿到刷新令牌的会话ID,并将对应访问令牌和刷新令牌都删除
                    //1.删除刷新令牌和访问令牌
                    redisTokenStore.deleteRefreshTokenAndAccessToken(refreshTokenId);
                    //2.删除设备的数据
                    redisKeyCache.deleteObject(deviceRedisKey);
                    //3.删除索引
                    redisZSetCache.zRemove(deviceIndexRedisKey, refreshTokenId);
                }
            });
        }
    }

    /**
     * 清理旧索引
     *
     * @param username 用户名
     */
    public void cleanOldIndex(@NotEmpty String username) {
        long now = System.currentTimeMillis();
        long refreshTokenExpireTime = securityProperties.getSession().getRefreshTokenExpireTime();
        String deviceIndexRedisKey = RedisConstants.Auth.SESSIONS_INDEX_KEY + username;
        // 删除旧数据
        Set<ZSetOperations.TypedTuple<String>> allDeviceSet = redisZSetCache.getAllWithScore(deviceIndexRedisKey);
        // 转换为毫秒
        long needDeleteValue = now - refreshTokenExpireTime * 1000;
        allDeviceSet.forEach(tuple -> {
            Double score = tuple.getScore();
            if (score != null && score <= needDeleteValue) {
                String refreshTokenId = tuple.getValue();
                String deviceRedisKey = RedisConstants.Auth.SESSIONS_DEVICE_KEY + refreshTokenId;
                //删除设备数据
                redisKeyCache.deleteObject(deviceRedisKey);
                //删除索引
                redisZSetCache.zRemove(deviceIndexRedisKey, refreshTokenId);
            }
        });
    }

    /**
     * 统计当前有效会话数量
     *
     * @param username   用户名
     * @param deviceType 设备类型
     * @return 当前有效会话数量
     */
    private long countCurrentSessions(String username, String deviceType) {
        String deviceIndexRedisKey = RedisConstants.Auth.SESSIONS_INDEX_KEY + username;
        Set<ZSetOperations.TypedTuple<String>> allDeviceSet = redisZSetCache.getAllWithScore(deviceIndexRedisKey);
        AtomicLong count = new AtomicLong();
        // 转换为毫秒
        long effectiveTime = System.currentTimeMillis() - securityProperties.getSession().getRefreshTokenExpireTime() * 1000;
        allDeviceSet.forEach(tuple -> {
            Double score = tuple.getScore();
            if (score != null && score >= effectiveTime) {
                String refreshTokenId = tuple.getValue();
                String deviceRedisKey = RedisConstants.Auth.SESSIONS_DEVICE_KEY + refreshTokenId;
                Map<String, Object> deviceInfo = redisHashCache.hGetAll(deviceRedisKey);
                if (!deviceInfo.isEmpty()) {
                    String loginDeviceType = deviceInfo.get(DEVICE_TYPE).toString();
                    if (deviceType.equals(loginDeviceType)) {
                        count.getAndIncrement();
                    }
                } else {
                    // 如果设备信息为空，则删除索引
                    redisZSetCache.zRemove(deviceIndexRedisKey, refreshTokenId);
                }
            }
        });

        return count.get();
    }

    /**
     * 获取登录数量限制
     *
     * @param deviceType 设备类型
     * @return 登录数量限制
     */
    private long getLimit(String deviceType) {
        SecurityProperties.SessionConfig.MaxSessionsPerClient maxSessionsPerClient = securityProperties.getSession().getMaxSessionsPerClient();
        Map<String, Long> limits = Map.of(
                DeviceType.PC.getValue(), maxSessionsPerClient.getPc(),
                DeviceType.MOBILE.getValue(), maxSessionsPerClient.getMobile(),
                DeviceType.WEB.getValue(), maxSessionsPerClient.getWeb(),
                DeviceType.MINI_PROGRAM.getValue(), maxSessionsPerClient.getMiniProgram(),
                DeviceType.UNKNOWN.getValue(), maxSessionsPerClient.getUnknown()
        );
        return limits.getOrDefault(deviceType, maxSessionsPerClient.getUnknown());
    }

    /**
     * 内部添加会话方法（不加锁，由调用方保证线程安全）
     *
     * @param loginDeviceDTO 登录设备信息
     */
    private void addSessionInternal(@NotNull LoginDeviceDTO loginDeviceDTO) {
        long now = System.currentTimeMillis();
        long refreshTokenExpireTime = securityProperties.getSession().getRefreshTokenExpireTime();

        Map<String, Object> deviceInfo = Map.of(
                DEVICE_TYPE, loginDeviceDTO.getDeviceType(),
                DEVICE_NAME, loginDeviceDTO.getDeviceName(),
                LOGIN_TIME, now
        );
        String deviceRedisKey = RedisConstants.Auth.SESSIONS_DEVICE_KEY + loginDeviceDTO.getRefreshSessionId();
        redisHashCache.hPutAll(deviceRedisKey, deviceInfo);
        // 设置会话有效期
        redisKeyCache.expire(deviceRedisKey, refreshTokenExpireTime, TimeUnit.SECONDS);
        // 写入ZSet索引
        String deviceIndexRedisKey = RedisConstants.Auth.SESSIONS_INDEX_KEY + loginDeviceDTO.getUsername();
        redisZSetCache.zAdd(deviceIndexRedisKey, loginDeviceDTO.getRefreshSessionId(), now, refreshTokenExpireTime, TimeUnit.SECONDS);
    }

}
