package cn.zhangchuangla.common.redis.core;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 通用的 Redis List 操作封装
 * 取消泛型要求，统一使用 String key 和 Object value
 *
 * @author Chuang
 */
@Component
@RequiredArgsConstructor
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public final class RedisListCache {

    private final RedisTemplate redisTemplate;

    /**
     * 从左侧入队
     */
    public Long leftPush(final String key, final Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 从右侧入队
     */
    public Long rightPush(final String key, final Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 从左侧弹出
     */
    @SuppressWarnings("unchecked")
    public <T> T leftPop(final String key) {
        return (T) redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 从右侧弹出
     */
    @SuppressWarnings("unchecked")
    public <T> T rightPop(final String key) {
        return (T) redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 获取指定范围的数据（start~end，包括两端）
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> range(final String key, final long start, final long end) {
        return (List<T>) redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取列表长度
     */
    public Long size(final String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 移除列表中等于 value 的元素，count>0 删除左边第 count 个；count<0 删除右边；0 删除所有
     */
    public Long remove(final String key, final long count, final Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    /**
     * 将列表修剪到指定区间
     */
    public void trim(final String key, final long start, final long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    /**
     * 设置过期时间
     */
    public Boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }
}
