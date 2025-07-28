package cn.zhangchuangla.common.redis.core;

import cn.zhangchuangla.common.redis.config.RedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 通用的 Redis Set（无序集合）操作封装
 * 取消泛型要求，统一使用 String key 和 Object value
 * 提供 Set 数据结构的各种操作方法，包括批量扫描功能
 *
 * @author Chuang
 */
@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public final class RedisSetCache {

    private final RedisTemplate redisTemplate;
    private final RedisProperties redisProperties;

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

    /**
     * 批量键扫描方法 - 使用 Redis SCAN 操作高效获取匹配的 Set 键
     * 推荐在生产环境中使用此方法，避免阻塞 Redis 服务器
     *
     * @param pattern Redis 键模式，支持通配符（如：auth:session:index:*）
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
                    .count(redisProperties.scanCount)
                    .build();

            try (Cursor<String> cursor = redisTemplate.scan(options)) {
                while (cursor.hasNext()) {
                    keys.add(cursor.next());
                }
            }


        } catch (Exception e) {
            log.error("Redis Set scan keys failed, pattern: {}, error: {}", pattern, e.getMessage(), e);
            // 发生异常时返回空列表，避免影响业务流程
            return new ArrayList<>();
        }

        return keys;
    }

    /**
     * 批量值获取方法 - 获取匹配模式的 Set 键及其所有成员
     * 使用 Redis SCAN 操作高效扫描键，然后批量获取每个 Set 的所有成员
     *
     * @param pattern Redis 键模式，支持通配符（如：auth:permissions:*）
     * @return Map，键为Set键，值为该Set的所有成员，如果没有匹配的键则返回空Map
     */
    public Map<String, Set<Object>> scanKeysWithValues(final String pattern) {
        if (!StringUtils.hasText(pattern)) {
            return new HashMap<>();
        }

        // 首先扫描获取所有匹配的键
        List<String> keys = scanKeys(pattern);
        if (keys.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Set<Object>> result = new HashMap<>();
        try {
            // 逐个获取每个 Set 的所有成员
            for (String key : keys) {
                Set<Object> setMembers = redisTemplate.opsForSet().members(key);
                result.put(key, setMembers);
            }
        } catch (Exception e) {
            log.error("Redis Set scan keys with values failed, pattern: {}, error: {}", pattern, e.getMessage(), e);
            return new HashMap<>();
        }

        return result;
    }


}
