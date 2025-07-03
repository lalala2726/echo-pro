package cn.zhangchuangla.storage.core.loader;

import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.storage.constant.StorageConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 亚马逊S3存储加载器
 * <p>
 * 负责将亚马逊S3存储配置加载到Redis中，并设置当前活动的存储类型。
 * 兼容标准S3协议和其他S3兼容的存储服务。
 * </p>
 *
 * @author Chuang
 * @since 2025/7/4
 */
@Slf4j
@Component
public class AmazonS3StorageLoader implements StorageLoader {

    /**
     * 获取存储类型标识
     *
     * @return 亚马逊S3存储类型常量
     */
    @Override
    public String getStorageType() {
        return StorageConstants.StorageType.AMAZON_S3;
    }

    /**
     * 加载亚马逊S3存储配置到Redis
     * <p>
     * 将配置JSON和存储类型保存到Redis缓存中，供其他组件使用。
     * 这样设计的好处是支持运行时动态切换存储配置。
     * </p>
     *
     * @param json       亚马逊S3存储配置的JSON字符串
     * @param redisCache Redis缓存实例
     */
    @Override
    public void loadConfig(String json, RedisCache redisCache) {
        // 将配置JSON保存到Redis
        redisCache.setCacheObject(RedisConstants.StorageConfig.CURRENT_STORAGE_CONFIG, json);

        // 设置当前活动的存储类型为亚马逊S3
        redisCache.setCacheObject(RedisConstants.StorageConfig.ACTIVE_TYPE, StorageConstants.StorageType.AMAZON_S3);

        log.info("亚马逊S3存储配置已成功加载到Redis缓存");
    }
}
