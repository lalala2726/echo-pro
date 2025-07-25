package cn.zhangchuangla.common.redis.core;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Chuang
 */
@Component
@RequiredArgsConstructor
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public final class RedisHashCache {


    private final RedisTemplate redisTemplate;


    /**
     * 向 Hash 添加单个字段
     *
     * @param key   Redis 键
     * @param field 字段名
     * @param value 字段值
     * @param <HK>  字段类型
     * @param <HV>  值类型
     */
    public <HK, HV> void hPut(final String key, final HK field, final HV value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 向 Hash 批量添加字段
     *
     * @param key  Redis 键
     * @param map  字段–值 对
     * @param <HK> 字段类型
     * @param <HV> 值类型
     */
    public <HK, HV> void hPutAll(final String key, final Map<HK, HV> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 从 Hash 中取单个字段
     *
     * @param key   Redis 键
     * @param field 字段名
     * @param <HK>  字段类型
     * @param <HV>  值类型
     * @return 对应字段的值；不存在返回 null
     */
    @SuppressWarnings("unchecked")
    public <HK, HV> HV hGet(final String key, final HK field) {
        return (HV) redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 取整个 Hash 的所有字段和值
     *
     * @param key  Redis 键
     * @param <HK> 字段类型
     * @param <HV> 值类型
     * @return 一个 Map，key 对应字段，value 对应值
     */
    @SuppressWarnings("unchecked")
    public <HK, HV> Map<HK, HV> hGetAll(final String key) {
        return (Map<HK, HV>) redisTemplate.opsForHash().entries(key);
    }

    /**
     * 从 Hash 中删除一个或多个字段
     *
     * @param key    Redis 键
     * @param fields 要删除的字段名
     * @param <HK>   字段类型
     * @return 删除的字段数量
     */
    public <HK> Long hRemove(final String key, final HK... fields) {
        return redisTemplate.opsForHash().delete(key, (Object[]) fields);
    }

    /**
     * 判断 Hash 中是否存在某个字段
     *
     * @param key   Redis 键
     * @param field 字段名
     * @return true=存在；false=不存在
     */
    public Boolean hExists(final String key, final Object field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * 获取 Hash 中字段的数量
     *
     * @param key Redis 键
     * @return 字段个数
     */
    public Long hSize(final String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 对 Hash 中的数值字段做增量操作
     *
     * @param key   Redis 键
     * @param field 字段名
     * @param delta 增量（负值表示减）
     * @param <HK>  字段类型
     * @return 操作后的新值
     */
    public <HK> Long hIncrement(final String key, final HK field, final long delta) {
        return redisTemplate.opsForHash().increment(key, field, delta);
    }

    /**
     * 给任意 key 设置过期时间（Hash 也能用）
     *
     * @param key     Redis 键
     * @param timeout 时长
     * @param unit    单位
     * @return 设置是否成功
     */
    public Boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 查询 key 的剩余存活秒数
     *
     * @param key Redis 键
     * @return 剩余秒数；-2=不存在，-1=未设置过期
     */
    public Long getExpire(final String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}
