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
 * 腾讯云COS存储加载器
 */
@Component
public class TencentCosStorageLoader implements StorageLoader {

    @Override
    public String getStorageType() {
        return StorageConstants.StorageType.TENCENT_COS;
    }

    @Override
    public void loadConfig(String json, RedisKeyCache redisKeyCache) {
        redisKeyCache.setCacheObject(RedisConstants.StorageConfig.CURRENT_STORAGE_CONFIG, json);
        redisKeyCache.setCacheObject(RedisConstants.StorageConfig.ACTIVE_TYPE, StorageConstants.StorageType.TENCENT_COS);
    }
}
