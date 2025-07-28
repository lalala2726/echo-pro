package cn.zhangchuangla.common.redis.core;

import cn.zhangchuangla.common.redis.config.RedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 有序集合（ZSet）操作缓存工具类
 * 提供有序集合数据结构的各种操作方法，包括批量扫描功能
 *
 * @author Chuang
 * <p>
 * created on 2025/7/25 15:28
 */
@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public final class RedisZSetCache {

    public final RedisTemplate redisTemplate;
    private final RedisProperties redisProperties;


    /**
     * 添加有序集合成员
     *
     * @param key   Redis 键
     * @param value 成员
     * @param score 分值
     * @param <T>   值类型
     */
    public <T> void zAdd(final String key, final T value, final double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 添加有序集合成员
     *
     * @param key     Redis 键
     * @param value   集合
     * @param <T>     值类型
     * @param timeout 超时时间 单位秒
     */
    public <T> void zAdd(final String key, final T value, final double score, final long timeout) {
        redisTemplate.opsForZSet().add(key, value, score);
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 添加有序集合成员
     *
     * @param key     Redis 键
     * @param value   集合
     * @param <T>     值类型
     * @param timeout 超时时间
     */
    public <T> void zAdd(final String key, final T value, final double score, final long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForZSet().add(key, value, score);
        redisTemplate.expire(key, timeout, timeUnit);
    }

    /**
     * 设置有序集合有效期(单位秒)
     *
     * @param key     Redis 键
     * @param timeout 有效期
     */
    public void expire(final String key, final long timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取有序集合有效期
     *
     * @param key Redis 键
     */
    public void expire(final String key, final long timeout, final TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }


    /**
     * 批量删除有序集合中的一个或多个成员
     *
     * @param key    Redis 键
     * @param values 要删除的成员
     * @param <T>    值类型
     * @return 删除的成员个数
     */
    public <T> Long zRemove(final String key, final T... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 按 排名（索引）范围获取有序集合成员
     *
     * @param key   Redis 键
     * @param start 开始索引（从 0 开始）
     * @param end   结束索引（-1 表示最后一个）
     * @param <T>   值类型
     * @return 值集合（按分值从小到大排序）
     */
    public <T> Set<T> zRange(final String key, final long start, final long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 按 分值 范围获取有序集合成员
     *
     * @param key Redis 键
     * @param min 最小分值
     * @param max 最大分值
     * @param <T> 值类型
     * @return 值集合（分值在 [min, max]）
     */
    public <T> Set<T> zRangeByScore(final String key, final double min, final double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 统计有序集合中分值在给定区间的成员数量
     *
     * @param key Redis 键
     * @param min 最小分值
     * @param max 最大分值
     * @return 成员数量
     */
    public Long zCount(final String key, final double min, final double max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }

    /**
     * 获取有序集合的总成员数
     *
     * @param key Redis 键
     * @return 成员数量
     */
    public Long zCard(final String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }


    /**
     * 获取有序集合的所有成员（带分值）
     *
     * @param key Redis 键
     * @return 值集合（带分值）
     */
    public Set<ZSetOperations.TypedTuple<String>> getAllWithScore(String key) {
        return redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
    }


    /**
     * 获取某个成员的分值
     *
     * @param key   Redis 键
     * @param value 成员
     * @param <T>   值类型
     * @return 分值；null 表示成员不存在
     */
    public <T> Double zScore(final String key, final T value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 为有序集合中的某个成员分值加上给定增量
     *
     * @param key   Redis 键
     * @param value 成员
     * @param delta 增量（可以为负数，用于减分）
     * @param <T>   值类型
     * @return 操作后的新分值
     */
    public <T> Double zIncrementScore(final String key, final T value, final double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }


    /**
     * 批量键扫描方法 - 使用 Redis SCAN 操作高效获取匹配的 ZSet 键
     * 推荐在生产环境中使用此方法，避免阻塞 Redis 服务器
     *
     * @param pattern Redis 键模式，支持通配符（如：ranking:*）
     * @return 匹配的键列表，如果没有匹配的键则返回空列表
     */
    public List<String> scanKeys(final String pattern) {
        if (!StringUtils.hasText(pattern)) {
            return new ArrayList<>();
        }

        List<String> keys = new ArrayList<>();
        try {
            ScanOptions options = ScanOptions.scanOptions()
                    .match(pattern)
                    // 每次扫描的数量，可根据实际情况调整
                    .count(redisProperties.scanCount)
                    .build();

            try (Cursor<String> cursor = redisTemplate.scan(options)) {
                while (cursor.hasNext()) {
                    keys.add(cursor.next());
                }
            }

            log.debug("Redis ZSet scan completed, pattern: {}, found {} keys", pattern, keys.size());
        } catch (Exception e) {
            log.error("Redis ZSet scan keys failed, pattern: {}, error: {}", pattern, e.getMessage(), e);
            // 发生异常时返回空列表，避免影响业务流程
            return new ArrayList<>();
        }

        return keys;
    }

    /**
     * 批量值获取方法 - 获取匹配模式的 ZSet 键及其所有成员（带分值）
     * 使用 Redis SCAN 操作高效扫描键，然后批量获取每个 ZSet 的所有成员和分值
     *
     * @param pattern Redis 键模式，支持通配符（如：ranking:*）
     * @return Map，键为ZSet键，值为该ZSet的所有成员（带分值），如果没有匹配的键则返回空Map
     */
    public Map<String, Set<ZSetOperations.TypedTuple<Object>>> scanKeysWithValues(final String pattern) {
        if (!StringUtils.hasText(pattern)) {
            return new HashMap<>();
        }

        // 首先扫描获取所有匹配的键
        List<String> keys = scanKeys(pattern);
        if (keys.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Set<ZSetOperations.TypedTuple<Object>>> result = new HashMap<>();
        try {
            // 逐个获取每个 ZSet 的所有成员（带分值）
            for (String key : keys) {
                Set<ZSetOperations.TypedTuple<Object>> zsetMembers =
                        redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
                result.put(key, zsetMembers);
            }
        } catch (Exception e) {
            log.error("Redis ZSet scan keys with values failed, pattern: {}, error: {}", pattern, e.getMessage(), e);
            return new HashMap<>();
        }

        return result;
    }


}
