package cn.zhangchuangla.common.core.redis;


import cn.zhangchuangla.common.constant.RedisKeyConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存操作工具类
 */
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
     * 缓存基本的对象，Integer、String、实体类等，默认时间单位为秒
     *
     * @param key     key
     * @param value   value
     * @param timeout 超时时间（秒）
     */
    public <T> void setCacheObject(final String key, final T value, final Long timeout) {
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
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象个数
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据列表
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
        for (T t : dataSet) {
            setOperation.add(t);
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据集合
     */
    public <T> Set<T> getCacheSet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key     缓存键值
     * @param dataMap 缓存的数据
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
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

    /**
     * 获取指定key的过期时间，指定时间单位
     *
     * @param key  键
     * @param unit 时间单位
     * @return 过期时间，返回-1表示永不过期，返回-2表示键不存在
     */
    public Long getKeyExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 递增操作
     *
     * @param key   键
     * @param delta 递增因子（大于0）
     * @return 递增后的值
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减操作
     *
     * @param key   键
     * @param delta 递减因子（大于0）
     * @return 递减后的值
     */
    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * 向Set中添加元素
     *
     * @param key    键
     * @param values 值（可以是多个）
     * @return 添加成功的个数
     */
    public <T> Long setCacheSetValues(String key, T... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 删除Hash中的值
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return true=成功；false=失败
     */
    public Boolean deleteCacheMapValue(String key, String hKey) {
        return redisTemplate.opsForHash().delete(key, hKey) > 0;
    }

    /**
     * 获取List中指定范围的元素
     *
     * @param key   键
     * @param start 开始位置
     * @param end   结束位置，-1表示所有值
     * @return 指定范围的元素列表
     */
    public <T> List<T> getCacheListRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 向List中添加单个元素
     *
     * @param key   键
     * @param value 值
     * @return List长度
     */
    public <T> Long setCacheListValue(String key, T value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 移除List中的最后一个元素，并返回该元素
     *
     * @param key 键
     * @return 移除的元素
     */
    public <T> T rightPopList(String key) {
        return (T) redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 移除List中的第一个元素，并返回该元素
     *
     * @param key 键
     * @return 移除的元素
     */
    public <T> T leftPopList(String key) {
        return (T) redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 检查Set中是否包含指定元素
     *
     * @param key   键
     * @param value 值
     * @return true=包含；false=不包含
     */
    public <T> Boolean isMemberOfSet(String key, T value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取Hash中的所有键
     *
     * @param key Redis键
     * @return Hash键集合
     */
    public Set<Object> getCacheMapKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取Hash中的所有值
     *
     * @param key Redis键
     * @return Hash值集合
     */
    public <T> List<T> getCacheMapValues(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    /**
     * 获取Hash的大小
     *
     * @param key Redis键
     * @return Hash大小
     */
    public Long getCacheMapSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 设置Hash中的值，仅当字段不存在时
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     * @return true=设置成功；false=设置失败（字段已存在）
     */
    public <T> Boolean setCacheMapValueIfAbsent(String key, String hKey, T value) {
        return redisTemplate.opsForHash().putIfAbsent(key, hKey, value);
    }
}
