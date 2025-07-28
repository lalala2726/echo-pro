package cn.zhangchuangla.storage.core.loader;

import cn.zhangchuangla.common.redis.core.RedisCache;

/**
 * @author Chuang
 * <p>
 * created on 2025/6/27 07:21
 * <p>
 * 存储服务加载器接口
 */
public interface StorageLoader {

    /**
     * 获取存储类型
     *
     * @return 存储类型
     */
    String getStorageType();

    /**
     * 加载存储配置,将存储配置保存到Redis中
     *
     * @param json          存储配置JSON
     * @param redisCache Redis缓存
     */
    void loadConfig(String json, RedisCache redisCache);
}
