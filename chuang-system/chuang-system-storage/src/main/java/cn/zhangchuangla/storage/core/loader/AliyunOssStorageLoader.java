package cn.zhangchuangla.storage.core.loader;

import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisKeyCache;
import cn.zhangchuangla.storage.constant.StorageConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Chuang
 * <p>
 * created on 2025/6/27 07:21
 * <p>
 * 阿里云OSS存储加载器
 */
@Slf4j
@Component
public class AliyunOssStorageLoader implements StorageLoader {

    @Override
    public String getStorageType() {
        return StorageConstants.StorageType.ALIYUN_OSS;
    }

    @Override
    public void loadConfig(String json, RedisKeyCache redisKeyCache) {
        redisKeyCache.setCacheObject(RedisConstants.StorageConfig.CURRENT_STORAGE_CONFIG, json);
        redisKeyCache.setCacheObject(RedisConstants.StorageConfig.ACTIVE_TYPE, StorageConstants.StorageType.ALIYUN_OSS);
    }
}
