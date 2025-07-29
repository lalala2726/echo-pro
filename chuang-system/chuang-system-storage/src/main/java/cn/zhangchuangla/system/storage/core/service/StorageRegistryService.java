package cn.zhangchuangla.system.storage.core.service;

import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.system.storage.config.StorageSystemProperties;
import cn.zhangchuangla.system.storage.constant.StorageConstants;
import cn.zhangchuangla.system.storage.core.loader.StorageLoader;
import cn.zhangchuangla.system.storage.model.entity.StorageConfig;
import cn.zhangchuangla.system.storage.service.StorageConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Chuang
 * <p>
 * created on 2025/6/27 07:21
 */
@Slf4j
@Service
public class StorageRegistryService {

    private final StorageConfigService storageConfigService;
    private final StorageSystemProperties storageSystemProperties;
    private final RedisCache redisCache;
    private final Map<String, StorageLoader> storageLoaders;

    public StorageRegistryService(StorageConfigService storageConfigService,
                                  StorageSystemProperties storageSystemProperties,
                                  RedisCache redisCache,
                                  List<StorageLoader> storageLoaders) {
        this.storageConfigService = storageConfigService;
        this.storageSystemProperties = storageSystemProperties;
        this.redisCache = redisCache;
        this.storageLoaders = storageLoaders.stream()
                .collect(Collectors.toMap(StorageLoader::getStorageType, Function.identity()));
    }


    /**
     * 开始初始化存储服务
     * 优先级：数据库配置 -> 本地配置文件指定配置 -> 本地默认存储
     *
     * @return 如果任何存储服务初始化成功，则为true，否则为false。
     */
    public boolean initializeStorage() {
        // 1. 优先从数据库中加载存储服务
        if (loadFromDatabase()) {
            return true;
        }

        log.warn("未能从数据库加载主存储配置，尝试从本地配置文件加载。");

        // 2. 从本地配置加载存储服务
        if (loadPrimaryFromLocalConfig()) {
            return true;
        }

        log.warn("未能从本地配置文件加载存储配置，降级为本地默认存储。");

        // 3. 如果本地配置加载失败，则降级为本地默认存储
        fallbackToLocalStorage();
        log.info("存储服务已降级为本地默认服务。");
        return true;
    }

    /**
     * 从数据库加载存储服务
     */
    public boolean loadFromDatabase() {
        StorageConfig primaryConfig = storageConfigService.getPrimaryConfig();
        if (primaryConfig == null || primaryConfig.getStorageType() == null) {
            return false;
        }
        boolean loaded = loadConfig(primaryConfig.getStorageType(), primaryConfig.getStorageValue());
        if (loaded) {
            redisCache.setCacheObject(RedisConstants.StorageConfig.CONFIGURATION_FILE_TYPE, RedisConstants.StorageConfig.CONFIG_TYPE_DATABASE);
            log.info("数据库 - {} 加载成功, 存储Key: {}.", primaryConfig.getStorageType(), primaryConfig.getStorageType());
        }
        return loaded;
    }

    /**
     * 从本地配置加载存储服务
     */
    public boolean loadPrimaryFromLocalConfig() {
        String activeType = storageSystemProperties.getActiveType();
        String configJson;
        switch (activeType) {
            case StorageConstants.StorageType.LOCAL:
                configJson = storageSystemProperties.getLocal().toJson();
                break;
            case StorageConstants.StorageType.MINIO:
                configJson = storageSystemProperties.getMinio().toJson();
                break;
            case StorageConstants.StorageType.ALIYUN_OSS:
                configJson = storageSystemProperties.getAliyunOss().toJson();
                break;
            case StorageConstants.StorageType.TENCENT_COS:
                configJson = storageSystemProperties.getTencentCos().toJson();
                break;
            case StorageConstants.StorageType.AMAZON_S3:
                configJson = storageSystemProperties.getAmazonS3().toJson();
                break;
            default:
                log.error("未找到对应的存储服务配置: {}", activeType);
                return false;
        }
        boolean loaded = loadConfig(activeType, configJson);
        if (loaded) {
            log.info("本地配置 - {} 加载成功.", activeType);
        }
        return loaded;
    }

    /**
     * 降级为本地存储服务
     */
    public void fallbackToLocalStorage() {
        loadConfig(StorageConstants.StorageType.LOCAL, storageSystemProperties.getLocal().toJson());
        log.info("降级为本地存储服务");
    }

    /**
     * 加载存储配置
     *
     * @param storageType 存储类型
     * @param json        存储配置JSON
     * @return 是否加载成功
     */
    private boolean loadConfig(String storageType, String json) {
        StorageLoader loader = storageLoaders.get(storageType);
        if (loader == null) {
            log.error("未找到对应的存储服务加载器: {}", storageType);
            return false;
        }
        loader.loadConfig(json, redisCache);
        log.info("存储服务 [{}] 配置已加载到Redis.", storageType);
        return true;
    }
}
