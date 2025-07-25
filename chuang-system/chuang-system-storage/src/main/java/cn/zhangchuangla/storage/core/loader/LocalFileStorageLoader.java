package cn.zhangchuangla.storage.core.loader;

import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisKeyCache;
import cn.zhangchuangla.storage.constant.StorageConstants;
import org.springframework.stereotype.Component;

/**
 * @author Chuang
 * <p>
 * created on 2025/6/27 07:21
 * <p>
 * 本地文件存储加载器
 */
@Component
public class LocalFileStorageLoader implements StorageLoader {

    @Override
    public String getStorageType() {
        return StorageConstants.StorageType.LOCAL;
    }

    @Override
    public void loadConfig(String json, RedisKeyCache redisKeyCache) {
        redisKeyCache.setCacheObject(RedisConstants.StorageConfig.CURRENT_STORAGE_CONFIG, json);
        redisKeyCache.setCacheObject(RedisConstants.StorageConfig.ACTIVE_TYPE, StorageConstants.StorageType.LOCAL);
    }
}
