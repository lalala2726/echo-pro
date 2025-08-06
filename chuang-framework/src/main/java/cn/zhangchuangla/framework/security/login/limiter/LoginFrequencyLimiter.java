package cn.zhangchuangla.framework.security.login.limiter;

import cn.zhangchuangla.common.core.config.property.SecurityProperties;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.AuthorizationException;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * 登录频率限制器
 * <p>
 * 该组件用于控制用户登录频率，防止频繁登录攻击。
 * 只统计成功登录次，支持按小时和按天限制登录次数。
 * </p>
 *
 * @author Chuang
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoginFrequencyLimiter {

    private final RedisCache redisCache;
    private final SecurityProperties securityProperties;

    /**
     * 检查用户登录频率限制（基于成功登录次数）
     *
     * @param username 用户名
     * @throws AuthorizationException 如果超过频率限制则抛异常
     */
    public void checkFrequencyLimit(String username) {
        Assert.hasText(username, "用户名不能为空");

        SecurityProperties.SessionConfig sessionConfig = securityProperties.getSession();
        if (sessionConfig == null) {
            log.debug("会话配置为空，跳过频率检查");
            return;
        }

        int maxLoginPerHour = sessionConfig.getMaxLoginPerHour();
        int maxLoginPerDay = sessionConfig.getMaxLoginPerDay();

        // 检查小时限制
        if (maxLoginPerHour > 0) {
            checkHourlyLimit(username, maxLoginPerHour);
        }

        // 检查每日限制
        if (maxLoginPerDay > 0) {
            checkDailyLimit(username, maxLoginPerDay);
        }
    }

    /**
     * 记录登录成功
     *
     * @param username 用户名
     */
    public void recordLoginSuccess(String username) {
        Assert.hasText(username, "用户名不能为空");

        // 增加小时成功次数
        incrementCounter(String.format(RedisConstants.Auth.LOGIN_SUCCESS_HOUR_KEY, username), getSecondsToNextHour());

        // 增加每日成功次数
        incrementCounter(String.format(RedisConstants.Auth.LOGIN_SUCCESS_DAY_KEY, username), getSecondsToNextDay());

        log.debug("用户 {} 登录成功，已记录登录频率", username);
    }

    /**
     * 检查小时限制（只基于成功登录次数）
     *
     * @param username        用户名
     * @param maxLoginPerHour 每小时最大登录次数
     */
    private void checkHourlyLimit(String username, int maxLoginPerHour) {
        String successKey = String.format(RedisConstants.Auth.LOGIN_SUCCESS_HOUR_KEY, username);
        int successCount = getCount(successKey);

        if (successCount >= maxLoginPerHour) {
            long remainingSeconds = redisCache.getExpire(successKey, TimeUnit.SECONDS);
            String message = remainingSeconds > 0
                    ? String.format("登录过于频繁，请在 %d 分钟后重试", (remainingSeconds + 59) / 60)
                    : "登录过于频繁，请稍后重试";

            log.warn("用户 {} 小时内登录次数超限 ({}/{})", username, successCount, maxLoginPerHour);
            throw new AuthorizationException(ResultCode.LOGIN_ERROR, message);
        }
    }

    /**
     * 检查每日限制（只基于成功登录次数）
     *
     * @param username       用户名
     * @param maxLoginPerDay 每日最大登录数
     */
    private void checkDailyLimit(String username, int maxLoginPerDay) {
        String successKey = String.format(RedisConstants.Auth.LOGIN_SUCCESS_DAY_KEY, username);
        int successCount = getCount(successKey);

        if (successCount >= maxLoginPerDay) {
            long remainingSeconds = redisCache.getExpire(successKey, TimeUnit.SECONDS);
            String message = remainingSeconds > 0
                    ? String.format("今日登录次数已达上限，请在 %d 小时后重试", (remainingSeconds + 3599) / 3600)
                    : "今日登录次数已达上限，请明天重试";

            log.warn("用户 {} 每日登录次数超限 ({}/{})", username, successCount, maxLoginPerDay);
            throw new AuthorizationException(ResultCode.LOGIN_ERROR, message);
        }
    }

    /**
     * 增加计数器
     *
     * @param key           Redis key
     * @param expireSeconds 过期时间（秒）
     */
    private void incrementCounter(String key, long expireSeconds) {
        Integer count = redisCache.getCacheObject(key);
        if (count == null) {
            count = 0;
        }
        count++;
        // 明确指定时间单位为秒
        redisCache.setCacheObject(key, count, expireSeconds);
    }

    /**
     * 获取计数值
     *
     * @param key Redis key
     * @return 计数值
     */
    private int getCount(String key) {
        Integer count = redisCache.getCacheObject(key);
        return count != null ? count : 0;
    }

    /**
     * 获取到下一个小时的秒数
     *
     * @return 秒数
     */
    private long getSecondsToNextHour() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextHour = now.plusHours(1).withMinute(0).withSecond(0).withNano(0);
        return ChronoUnit.SECONDS.between(now, nextHour);
    }

    /**
     * 获取到下一天的秒数
     *
     * @return 秒数
     */
    private long getSecondsToNextDay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextDay = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        return ChronoUnit.SECONDS.between(now, nextDay);
    }

    /**
     * 获取用户今日登录统计（只统计成功次数）
     *
     * @param username 用户名
     * @return 今日成功登录次数
     */
    public int getTodayLoginCount(String username) {
        Assert.hasText(username, "用户名不能为空");
        String successKey = String.format(RedisConstants.Auth.LOGIN_SUCCESS_DAY_KEY, username);
        return getCount(successKey);
    }

    /**
     * 获取用户本小时登录统计（只统计成功次数）
     *
     * @param username 用户名
     * @return 本小时成功登录次数
     */
    public int getHourlyLoginCount(String username) {
        Assert.hasText(username, "用户名不能为空");
        String successKey = String.format(RedisConstants.Auth.LOGIN_SUCCESS_HOUR_KEY, username);
        return getCount(successKey);
    }
}
