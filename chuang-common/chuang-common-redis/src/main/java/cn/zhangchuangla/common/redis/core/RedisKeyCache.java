package cn.zhangchuangla.common.redis.core;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存操作工具类
 *
 * @author Chuang
 */
@Slf4j
@Component
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class RedisKeyCache {


    public final RedisTemplate redisTemplate;

    public RedisKeyCache(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final int timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等，默认时间单位为秒
     *
     * @param key     key
     * @param value   value
     * @param timeout 超时时间（秒）
     */
    public <T> void setCacheObject(final String key, final T value, final long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }


    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间（秒）
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }


    /**
     * 删除单个对象
     *
     * @param key 缓存键值
     * @return true=删除成功；false=删除失败
     */
    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象的集合
     * @return 删除成功的个数
     */
    public long deleteObject(final Collection collection) {
        return redisTemplate.delete(collection);
    }


    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }


    /**
     * 判断缓存中是否有对应的value
     *
     * @param key 键
     * @return true=存在；false=不存在
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 判断给定键是否存在于Redis中
     *
     * @param key 键
     * @return true=存在；false=不存在
     */
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 获取指定key的过期时间
     *
     * @param key key
     * @return 过期时间（秒），返回-1表示永不过期，返回-2表示键不存在
     */
    public Long getKeyExpire(String key) {
        return redisTemplate.getExpire(key);
    }


}
