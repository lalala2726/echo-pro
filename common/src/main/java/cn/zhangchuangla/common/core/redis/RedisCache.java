package cn.zhangchuangla.common.core.redis;


import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.core.model.entity.LoginUser;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisKeyCommands;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@SuppressWarnings(value = {"unchecked", "rawtypes"})
@Component
public class RedisCache {


    public final RedisTemplate redisTemplate;

    public RedisCache(RedisTemplate redisTemplate) {
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
    public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key     key
     * @param value   value
     * @param timeout 超时时间
     */
    public <T> void setCacheObject(final String key, final T value, final Long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout);
    }


    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
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
     * 根据前缀从Redis中获取字段（Keys）
     *
     * @param keyPrefix Redis 的Key前缀（如 "user:"）
     * @param limit     限制返回的字段数量
     * @param <T>       返回值的泛型
     * @return 返回符合条件的字段和值
     */
    @Deprecated
    public <T> Map<String, T> fetchFieldsByPrefix(final String keyPrefix, final int limit) {
        log.info("Fetching fields from Redis with prefix: {} and limit: {}", keyPrefix, limit);
        Map<String, T> result = new HashMap<>();

        // 使用 Redis scan 避免全量 keys 查询
        ScanOptions options = ScanOptions.scanOptions().match(keyPrefix + "*").count(limit).build();

        // 使用 RedisTemplate 执行 scan 操作
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            RedisKeyCommands keyCommands = connection.keyCommands();
            try (Cursor<byte[]> cursor = keyCommands.scan(options)) {
                while (cursor.hasNext()) {
                    byte[] keyBytes = cursor.next();
                    String key = new String(keyBytes);
                    Object value = redisTemplate.opsForValue().get(key);

                    if (value != null) {
                        result.put(key, (T) value);
                    }
                }
            } catch (Exception e) {
                log.error("Error while fetching fields from Redis", e);
            }
            return null;
        });
        return result;
    }

    /**
     * 根据前缀统计 Redis 中的键数量
     *
     * @param keyPrefix Redis 键前缀（如 "user:"）
     * @return 匹配的键数量
     */
    @Deprecated
    public long countKeysByPrefix(final String keyPrefix) {
        ScanOptions options = ScanOptions.scanOptions().match(keyPrefix + "*").count(1000).build();

        // 使用 RedisTemplate 执行 scan 操作并统计键数量
        Long count = (Long) redisTemplate.execute((RedisCallback<Long>) connection -> {
            RedisKeyCommands keyCommands = connection.keyCommands();
            AtomicInteger counter = new AtomicInteger();

            try (Cursor<byte[]> cursor = keyCommands.scan(options)) {
                while (cursor.hasNext()) {
                    cursor.next();
                    counter.incrementAndGet();
                }
            } catch (Exception e) {
                log.error("Error while counting keys from Redis", e);
            }
            return (long) counter.get();
        });
        if (count == null) {
            throw new RuntimeException("无法正常执行本次操作!");
        }
        return count;
    }

    /**
     * 获取Redis中指定前缀的最早登录信息
     *
     * @param keyPrefix Redis 的Key前缀（如 "user:"）
     * @return 返回符合条件的字段和值
     */
    @Deprecated()
    @SuppressWarnings("unchecked")
    public String fetchAndDeleteEarliestLoginByPrefix(final String keyPrefix) {
        log.info("开始扫描最早登录的前缀信息:{}", keyPrefix);
        HashMap<String, LoginUser> loginUserHashMap = new HashMap<>();
        // 使用 Redis scan 避免全量 keys 查询
        ScanOptions options = ScanOptions.scanOptions().match(keyPrefix + "*").count(1000).build();

        // 使用 RedisTemplate 执行 scan 操作
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            RedisKeyCommands keyCommands = connection.keyCommands();
            try (Cursor<byte[]> cursor = keyCommands.scan(options)) {
                while (cursor.hasNext()) {
                    byte[] keyBytes = cursor.next();
                    String key = new String(keyBytes);
                    Object object = redisTemplate.opsForValue().get(key);
                    if (object != null) {
                        LoginUser loginUser = convertToLoginUser(object);
                        if (loginUser != null) {
                            loginUserHashMap.put(key, loginUser);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("从Redis中获取字段出错", e);
            }
            return null;
        });
        // 找到最早的登录用户
        String earliestKey = loginUserHashMap.entrySet().stream()
                .min(Comparator.comparingLong(entry -> entry.getValue().getLoginTime()))
                .map(Map.Entry::getKey)
                .orElse(null);

        if (earliestKey != null) {
            loginUserHashMap.remove(earliestKey);
        }
        return earliestKey;
    }

    /**
     * 将 Redis 值转换为 LoginUser 对象
     *
     * @param value Redis 值
     * @return LoginUser
     */
    private LoginUser convertToLoginUser(Object value) {
        if (value instanceof LoginUser) {
            return (LoginUser) value;
        } else if (value instanceof JSONObject) {
            return ((JSONObject) value).toJavaObject(LoginUser.class);
        } else if (value instanceof String) {
            return JSON.parseObject((String) value, LoginUser.class);
        }
        return null;
    }


    /**
     * 删除单个对象
     */
    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public long deleteObject(final Collection collection) {
        return redisTemplate.delete(collection);
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext()) {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 删除Hash中的数据
     *
     * @param key
     * @param hkey
     */
    public void delCacheMapValue(final String key, final String hkey) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(key, hkey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
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
        return redisTemplate.hasKey(RedisKeyConstant.LOGIN_TOKEN_KEY + key);
    }

    /**
     * 获取指定key的过期时间
     *
     * @param key key
     */
    public Long getKeyExpire(String key) {
        return redisTemplate.getExpire(key);
    }

}
