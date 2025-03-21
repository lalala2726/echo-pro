package cn.zhangchuangla.common.core.redis;

import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.entity.file.LocalFileConfigEntity;
import cn.zhangchuangla.common.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.exception.ProfileException;
import cn.zhangchuangla.common.utils.ProfileUtils;
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
    private final AppConfig appConfig;

    // 本地缓存
    private final Map<String, Object> configCache = new HashMap<>();

    /**
     * 从Redis中加载配置
     */
    @PostConstruct
    public void loadAllConfigs() {
        log.info("正在从Redis加载所有配置...");
        try {
            loadFileUploadConfigs();
            // 其他配置...
        } catch (ProfileException e) {
            log.error("配置文件异常", e);
            // 在初始化时尝试加载本地配置
            try {
                loadLocalFileConfig();
            } catch (ProfileException pe) {
                log.error("本地配置加载失败", pe);
                // 初始化时不抛出异常，但记录错误
            }
        } catch (Exception e) {
            log.warn("无法从Redis中加载配置信息", e);
        }
        log.info("所有配置加载完成");
    }

    /**
     * 加载文件上传配置
     * <p>
     * 优先从Redis中加载文件上传配置信息，如果无法从redis中加载或者Redis中没有配置,则从AppConfig中获取本地文件上传访问路径，并将本地文件上传访问路径保存到Redis中。
     * </p>
     * @throws ProfileException 当配置验证失败时抛出
     */
    private void loadFileUploadConfigs() throws ProfileException {
        log.info("加载文件上传配置信息...");
        String defaultType = redisCache.getCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_DEFAULT);
        if (defaultType == null) {
            loadLocalFileConfig();
            return;
        }
        
        switch (defaultType) {
            case Constants.LOCAL_FILE_UPLOAD:
                LocalFileConfigEntity localFileConfig = redisCache.getCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_LOCAL);
                ProfileUtils.checkCachePropertiesLocalFileUpload(localFileConfig);
                configCache.put(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_LOCAL, localFileConfig);
                configCache.put(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_DEFAULT, Constants.LOCAL_FILE_UPLOAD);
                log.info("从Redis中加载本地文件上传配置成功~");
                break;
            case Constants.MINIO_FILE_UPLOAD:
                MinioConfigEntity minioConfig = redisCache.getCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_MINIO);
                ProfileUtils.checkCachePropertiesMinioConfig(minioConfig);
                configCache.put(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_MINIO, minioConfig);
                configCache.put(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_DEFAULT, Constants.MINIO_FILE_UPLOAD);
                log.info("从Redis中加载Minio上传配置成功~");
                break;
            case Constants.ALIYUN_OSS_FILE_UPLOAD:
                AliyunOSSConfigEntity aliyunOSSConfig = redisCache.getCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_ALIYUN);
                ProfileUtils.checkCachePropertiesAliyunOssFileUpload(aliyunOSSConfig);
                configCache.put(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_ALIYUN, aliyunOSSConfig);
                configCache.put(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_DEFAULT, Constants.ALIYUN_OSS_FILE_UPLOAD);
                log.info("从Redis中加载AliyunOSS上传配置成功~");
                break;
            default:
                log.error("无法识别的默认文件上传类型: {}", defaultType);
                throw new ProfileException("无法识别的默认文件上传类型: " + defaultType);
        }
        
        log.info("文件上传配置信息加载成功");
    }

    /**
     * 从本地配置文件加载本地文件上传配置,并保存到Redis中
     * @throws ProfileException 当本地配置验证失败时抛出
     */
    private void loadLocalFileConfig() throws ProfileException {
        log.info("从本地配置文件加载本地文件上传配置");
        String uploadPath = appConfig.getUploadPath();
        ProfileUtils.checkLocalPropertiesLoadFileUpload(appConfig);
        
        configCache.put(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_DEFAULT, Constants.LOCAL_FILE_UPLOAD);
        configCache.put(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_LOCAL, new LocalFileConfigEntity(uploadPath));
        
        redisCache.setCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_DEFAULT, Constants.LOCAL_FILE_UPLOAD);
        redisCache.setCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_LOCAL, new LocalFileConfigEntity(uploadPath));
        
        log.info("本地文件上传配置加载成功！");
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
     * 刷新所有配置 (用于初始化后的主动刷新，会向上抛出异常)
     * @throws ProfileException 当配置验证失败时抛出
     */
    public void refreshAllConfigs() throws ProfileException {
        log.info("刷新所有配置...");
        refreshFileUploadConfigs();
        // 其他配置刷新...
        log.info("所有配置刷新完成");
    }
    
    /**
     * 专门用于刷新配置的方法，不做任何异常处理，让异常直接向上抛出
     * @throws ProfileException 当配置验证失败时抛出
     */
    private void refreshFileUploadConfigs() throws ProfileException {
        log.info("刷新文件上传配置...");
        String defaultType = redisCache.getCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_DEFAULT);
        if (defaultType == null) {
            throw new ProfileException("未找到默认文件上传配置");
        }
        
        switch (defaultType) {
            case Constants.LOCAL_FILE_UPLOAD:
                LocalFileConfigEntity localFileConfig = redisCache.getCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_LOCAL);
                ProfileUtils.checkCachePropertiesLocalFileUpload(localFileConfig);
                configCache.put(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_LOCAL, localFileConfig);
                configCache.put(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_DEFAULT, Constants.LOCAL_FILE_UPLOAD);
                log.info("刷新本地文件上传配置成功");
                break;
            case Constants.MINIO_FILE_UPLOAD:
                MinioConfigEntity minioConfig = redisCache.getCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_MINIO);
                ProfileUtils.checkCachePropertiesMinioConfig(minioConfig);
                configCache.put(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_MINIO, minioConfig);
                configCache.put(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_DEFAULT, Constants.MINIO_FILE_UPLOAD);
                log.info("刷新Minio上传配置成功");
                break;
            case Constants.ALIYUN_OSS_FILE_UPLOAD:
                AliyunOSSConfigEntity aliyunOSSConfig = redisCache.getCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_ALIYUN);
                ProfileUtils.checkCachePropertiesAliyunOssFileUpload(aliyunOSSConfig);
                configCache.put(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_ALIYUN, aliyunOSSConfig);
                configCache.put(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT_DEFAULT, Constants.ALIYUN_OSS_FILE_UPLOAD);
                log.info("刷新AliyunOSS上传配置成功");
                break;
            default:
                log.error("无法识别的默认文件上传类型: {}", defaultType);
                throw new ProfileException("无法识别的默认文件上传类型: " + defaultType);
        }
    }
}
