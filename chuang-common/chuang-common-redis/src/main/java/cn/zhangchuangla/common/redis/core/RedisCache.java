package cn.zhangchuangla.common.redis.core;


import cn.zhangchuangla.common.redis.config.RedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存操作工具类
 *
 * @author Chuang
 */
@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public final class RedisCache {


    public final RedisTemplate redisTemplate;
    private final RedisProperties redisProperties;


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
     * 注意：此方法使用 KEYS 命令，在生产环境中应谨慎使用，建议使用 scanKeys 方法
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 批量键扫描方法 - 使用 Redis SCAN 操作高效获取匹配的键
     * 推荐在生产环境中使用此方法替代 keys() 方法，避免阻塞 Redis 服务器
     */
    public List<String> scanKeys(final String pattern) {
        if (!StringUtils.hasText(pattern)) {
            return new ArrayList<>();
        }

        List<String> keys = new ArrayList<>();
        try {
            ScanOptions options = ScanOptions.scanOptions()
                    .match(pattern)
                    .count(redisProperties.scanCount)
                    .build();

            try (Cursor<String> cursor = redisTemplate.scan(options)) {
                while (cursor.hasNext()) {
                    keys.add(cursor.next());
                }
            }
        } catch (Exception e) {
            log.error("Redis scan keys failed, pattern: {}, error: {}", pattern, e.getMessage(), e);
            return new ArrayList<>();
        }

        return keys;
    }

    /**
     * 批量值获取方法 - 获取匹配模式的键及其对应的值
     * 使用 Redis SCAN 操作高效扫描键，然后批量获取对应的值
     *
     * @param pattern Redis 键模式，支持通配符（如：auth:session:index:*）
     * @return 键值对Map，键为Redis键，值为对应的Redis值，如果没有匹配的键则返回空Map
     */
    public Map<String, Object> scanKeysWithValues(final String pattern) {
        if (!StringUtils.hasText(pattern)) {
            return new HashMap<>();
        }

        // 首先扫描获取所有匹配的键
        List<String> keys = scanKeys(pattern);
        if (keys.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Object> result = new HashMap<>();
        try {
            // 批量获取值以提高性能
            List<Object> values = redisTemplate.opsForValue().multiGet(keys);

            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                Object value = (values != null && i < values.size()) ? values.get(i) : null;
                result.put(key, value);
            }
        } catch (Exception e) {
            log.error("Redis scan keys with values failed, pattern: {}, error: {}", pattern, e.getMessage(), e);
            return new HashMap<>();
        }

        return result;
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
     * @param key ��
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

    /**
     * 获取指定key的过期时间
     *
     * @param key      key
     * @param timeUnit 时间单位
     * @return 过期时间，返回-1���示永不过期，返回-2表示键不存在
     */
    public Long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 获取指定key的过期时间（默认秒）
     *
     * @param key key
     * @return 过期时间（秒），返回-1表示永不过期，返回-2表示键不存在
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }


}
