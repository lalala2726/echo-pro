package cn.zhangchuangla.common.core.redis;

import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.entity.file.LocalFileConfigEntity;
import cn.zhangchuangla.common.entity.file.MinioConfigEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigCacheService {

    private final RedisCache redisCache;

    // 本地缓存
    private final Map<String, Object> configCache = new HashMap<>();

    @PostConstruct
    public void loadAllConfigs() {
        log.info("正在从Redis加载所有配置...");
        loadFileUploadConfigs();
        // 其他配置...
        log.info("所有配置加载完成");
    }

    private void loadFileUploadConfigs() {
        // 加载默认文件上传类型
        String defaultType = redisCache.getCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_DEFAULT);
        if (defaultType != null) {
            configCache.put(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_DEFAULT, defaultType);
        }

        // 加载Minio配置
        MinioConfigEntity minioConfig = redisCache.getCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_MINIO);
        if (minioConfig != null) {
            configCache.put(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_MINIO, minioConfig);
        }

        // 加载阿里云OSS配置
        AliyunOSSConfigEntity ossConfig = redisCache.getCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_ALIYUN);
        if (ossConfig != null) {
            configCache.put(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_ALIYUN, ossConfig);
        }

        // 加载本地文件配置
        LocalFileConfigEntity localConfig = redisCache.getCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_LOCAL);
        if (localConfig != null) {
            configCache.put(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_LOCAL, localConfig);
        }
    }

    /**
     * 获取通用配置
     */
    @SuppressWarnings("unchecked")
    public <T> T getConfig(String key) {
        return (T) configCache.get(key);
    }

    /**
     * 获取默认文件上传类型
     */
    public String getDefaultFileUploadType() {
        return getConfig(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_DEFAULT);
    }

    /**
     * 获取MinIO配置
     */
    public MinioConfigEntity getMinioConfig() {
        return getConfig(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_MINIO);
    }

    /**
     * 获取阿里云OSS配置
     */
    public AliyunOSSConfigEntity getAliyunOSSConfig() {
        return getConfig(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_ALIYUN);
    }

    /**
     * 获取本地文件配置
     */
    public LocalFileConfigEntity getLocalFileConfig() {
        return getConfig(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_LOCAL);
    }

    /**
     * 更新配置并刷新本地缓存
     */
    public <T> void updateConfig(String key, T value) {
        // 更新Redis
        redisCache.setCacheObject(key, value);
        // 更新本地缓存
        configCache.put(key, value);
        log.info("配置已更新: {}", key);
    }

    /**
     * 刷新特定配置
     */
    public <T> T refreshConfig(String key) {
        T value = redisCache.getCacheObject(key);
        if (value != null) {
            configCache.put(key, value);
            log.info("配置已刷新: {}", key);
        }
        return value;
    }

    /**
     * 刷新所有配置
     */
    public void refreshAllConfigs() {
        log.info("刷新所有配置...");
        loadAllConfigs();
    }
}
