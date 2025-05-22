package cn.zhangchuangla.framework.helper;

import cn.zhangchuangla.common.core.enums.AccessType;
import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.TooManyRequestException;
import cn.zhangchuangla.common.core.utils.ServletUtils;
import cn.zhangchuangla.common.core.utils.client.IPUtils;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * 访问限流助手
 * 提供编程式限流能力，可在任意代码位置进行限流控制
 *
 * @author Chuang
 * <p>
 * created on 2025/4/7 21:30
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccessLimitHelper {

    /**
     * 限流Lua脚本
     */
    private static final String LIMIT_LUA_SCRIPT = """
            local key = KEYS[1]
            local maxCount = tonumber(ARGV[1])
            local expireTime = tonumber(ARGV[2])
            
            local current = tonumber(redis.call('get', key) or "0")
            if current >= maxCount then
                return 0
            end
            
            if current == 0 then
                redis.call('setex', key, expireTime, 1)
            else
                redis.call('incr', key)
            end
            return 1
            """;
    /**
     * Redis Lua脚本对象
     */
    private static final DefaultRedisScript<Long> REDIS_SCRIPT = new DefaultRedisScript<>(LIMIT_LUA_SCRIPT, Long.class);
    /**
     * Redis操作模板
     */
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 基于IP地址的限流检查
     *
     * @param key      限流标识
     * @param maxCount 允许的最大访问次数
     * @param period   时间窗口(秒)
     * @return true-允许访问，false-限制访问
     */
    public boolean checkWithIp(String key, int maxCount, int period) {
        try {
            HttpServletRequest request = ServletUtils.getRequest();
            String ip = IPUtils.getIpAddr(request);
            String redisKey = RedisConstants.ACCESS_LIMIT_IP + key + ":" + ip;
            return executeLimit(redisKey, maxCount, period);
        } catch (Exception e) {
            log.error("基于IP限流异常: {}", e.getMessage(), e);
            // 发生异常时默认放行
            return true;
        }
    }

    /**
     * 基于用户ID的限流检查
     *
     * @param key      限流标识
     * @param userId   用户ID
     * @param maxCount 允许的最大访问次数
     * @param period   时间窗口(秒)
     * @return true-允许访问，false-限制访问
     */
    public boolean checkWithUserId(String key, String userId, int maxCount, int period) {
        try {
            String redisKey = RedisConstants.ACCESS_LIMIT_USER + key + ":" + userId;
            return executeLimit(redisKey, maxCount, period);
        } catch (Exception e) {
            log.error("基于用户ID限流异常: {}", e.getMessage(), e);
            // 发生异常时默认放行
            return true;
        }
    }

    /**
     * 基于自定义标识的限流检查
     *
     * @param key      限流标识
     * @param customId 自定义标识
     * @param maxCount 允许的最大访问次数
     * @param period   时间窗口(秒)
     * @return true-允许访问，false-限制访问
     */
    public boolean checkWithCustomId(String key, String customId, int maxCount, int period) {
        try {
            String redisKey = RedisConstants.ACCESS_LIMIT_CUSTOM + key + ":" + customId;
            return executeLimit(redisKey, maxCount, period);
        } catch (Exception e) {
            log.error("基于自定义标识限流异常: {}", e.getMessage(), e);
            // 发生异常时默认放行
            return true;
        }
    }

    /**
     * 检查限流并在超限时抛出异常
     *
     * @param key       限流标识
     * @param uniqueId  唯一标识(如IP、用户ID等)
     * @param maxCount  最大访问次数
     * @param period    时间窗口(秒)
     * @param limitType 限流类型
     * @param message   超限提示消息
     * @throws TooManyRequestException 当超过访问限制时抛出
     */
    public void checkAndThrow(String key, String uniqueId, int maxCount, int period,
                              AccessType limitType, String message) {
        try {
            // 根据限流类型选择不同前缀
            String prefix;
            switch (limitType) {
                case IP -> prefix = RedisConstants.ACCESS_LIMIT_IP;
                case USER -> prefix = RedisConstants.ACCESS_LIMIT_USER;
                case CUSTOM -> prefix = RedisConstants.ACCESS_LIMIT_CUSTOM;
                default -> prefix = RedisConstants.ACCESS_LIMIT_IP;
            }

            String redisKey = prefix + key + ":" + uniqueId;
            boolean allowed = executeLimit(redisKey, maxCount, period);
            if (!allowed) {
                log.warn("接口访问频率超限 - Key: {}, 唯一标识: {}, 限制: {}次/{}秒, 限流类型: {}",
                        key, uniqueId, maxCount, period, limitType.getDescription());
                throw new TooManyRequestException(ResponseCode.TOO_MANY_REQUESTS, message);
            }
        } catch (TooManyRequestException e) {
            throw e;
        } catch (Exception e) {
            // Redis异常时，为确保系统可用性，记录异常但放行请求
            log.error("访问限流检查异常，请求已放行: {}", e.getMessage(), e);
        }
    }

    /**
     * 基于IP的限流检查，超过限制时抛出异常
     *
     * @param key      限流标识
     * @param ip       IP地址
     * @param maxCount 最大访问次数
     * @param period   时间窗口(秒)
     * @param message  超限提示消息
     * @throws TooManyRequestException 当超过访问限制时抛出
     */
    public void checkIpAndThrow(String key, String ip, int maxCount, int period, String message) {
        checkAndThrow(key, ip, maxCount, period, AccessType.IP, message);
    }

    /**
     * 基于用户ID的限流检查，超过限制时抛出异常
     *
     * @param key      限流标识
     * @param userId   用户ID
     * @param maxCount 最大访问次数
     * @param period   时间窗口(秒)
     * @param message  超限提示消息
     * @throws TooManyRequestException 当超过访问限制时抛出
     */
    public void checkUserAndThrow(String key, String userId, int maxCount, int period, String message) {
        checkAndThrow(key, userId, maxCount, period, AccessType.USER, message);
    }

    /**
     * 执行限流逻辑
     *
     * @param redisKey Redis键
     * @param maxCount 允许的最大访问次数
     * @param period   时间窗口(秒)
     * @return true-允许访问，false-限制访问
     */
    private boolean executeLimit(String redisKey, int maxCount, int period) {
        Long result = stringRedisTemplate.execute(
                REDIS_SCRIPT,
                Collections.singletonList(redisKey),
                String.valueOf(maxCount),
                String.valueOf(period));

        return result != null && result > 0;
    }
}
