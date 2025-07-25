package cn.zhangchuangla.common.redis.core;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/25 15:28
 */
@Component
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class RedisZSetCache {

    public final RedisTemplate redisTemplate;

    public RedisZSetCache(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


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
}
