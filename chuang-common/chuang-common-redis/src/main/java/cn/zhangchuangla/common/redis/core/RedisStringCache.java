package cn.zhangchuangla.common.redis.core;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 简单 String 类型操作
 *
 * @author Chuang
 */
@Component
@RequiredArgsConstructor
public class RedisStringCache {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 存储一个字符串
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 存储一个字符串并设置过期时间
     */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取一个字符串
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 递增（或递减）数值（字符串必须能转成 Long）
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 删除一个 key
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 设置过期
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }
}
