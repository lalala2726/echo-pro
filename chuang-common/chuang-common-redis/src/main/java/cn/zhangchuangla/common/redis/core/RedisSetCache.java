package cn.zhangchuangla.common.redis.core;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 通用的 Redis Set（无序集合）操作封装
 * 取消泛型要求，统一使用 String key 和 Object value
 *
 * @author Chuang
 */
@Component
@RequiredArgsConstructor
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public final class RedisSetCache {

    private final RedisTemplate redisTemplate;

    /**
     * 添加一个成员到集合
     */
    public Long add(final String key, final Object value) {
        return redisTemplate.opsForSet().add(key, value);
    }

    /**
     * 批量添加成员
     */
    public Long addAll(final String key, final Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 判断成员是否存在
     */
    public Boolean isMember(final String key, final Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取所有成员
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> members(final String key) {
        return (Set<T>) redisTemplate.opsForSet().members(key);
    }

    /**
     * 从集合中移除成员
     */
    public Long remove(final String key, final Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 获取集合大小
     */
    public Long size(final String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 随机弹出并返回一个成员
     */
    @SuppressWarnings("unchecked")
    public <T> T pop(final String key) {
        return (T) redisTemplate.opsForSet().pop(key);
    }

    /**
     * 设置过期时间
     */
    public Boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }
}
