package cn.zhangchuangla.storage.core.service;

import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Chuang
 * <p>
 * created on 2025/6/27 07:21
 * <p>
 * 存储配置获取服务
 * 用于获取当前激活的存储配置信息
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageConfigRetrievalService {

    private final RedisCache redisCache;

    /**
     * 获取当前激活的存储类型
     *
     * @return 存储类型字符串 (e.g., "MINIO", "ALIYUN_OSS", "LOCAL", "TENCENT_COS")
     */
    public String getActiveStorageType() {
        return redisCache.getCacheObject(RedisConstants.StorageConfig.ACTIVE_TYPE);
    }

    /**
     * 获取当前激活存储的配置JSON字符串
     *
     * @return 配置JSON字符串
     */
    public String getCurrentStorageConfigJson() {
        return redisCache.getCacheObject(RedisConstants.StorageConfig.CURRENT_STORAGE_CONFIG);
    }

}
