package cn.zhangchuangla.storage.loader;

import cn.zhangchuangla.common.core.config.property.AppProperty;
import cn.zhangchuangla.common.core.constant.StorageConstants;
import cn.zhangchuangla.common.core.core.loader.DataLoader;
import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.ProfileException;
import cn.zhangchuangla.common.core.model.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.core.model.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.core.model.entity.file.TencentCOSConfigEntity;
import cn.zhangchuangla.storage.model.entity.StorageConfig;
import cn.zhangchuangla.storage.service.StorageConfigService;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统文件配置加载器：从数据库加载存储配置并缓存，支持 MinIO、OSS、本地文件系统。
 *
 * @author Chuang
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class StorageConfigLoader implements DataLoader {

    private final StorageConfigService storageConfigService;
    private final AppProperty appProperty;
    private final Map<String, String> sysFileConfigCache = new ConcurrentHashMap<>(4);

    @Override
    public String getName() {
        return "存储配置加载器";
    }

    @Override
    public int getOrder() {
        return 10; // 优先级较高，需要早于其他依赖存储配置的加载器执行
    }

    @Override
    public boolean isAsync() {
        return false; // 存储配置必须同步加载，因为其他服务可能依赖它
    }

    /**
     * 执行数据加载
     */
    @Override
    public void load() {
        try {
            log.info("开始从数据库中加载存储配置");
            sysFileConfigCache.clear();

            StorageConfig config = storageConfigService.getMasterConfig();
            if (config == null) {
                //未找到主要配置，自动降级为本地存储
                log.error("没有找到主要存储配置，自动设置为本地存储");
                autoSetLocalStorage();
                return;
            }

            cacheSysFileConfigByStorageType(config);
            log.info("文件上传配置加载成功，类型: {}, 存储Key: {}", config.getStorageType(), config.getStorageKey());
        } catch (Exception e) {
            log.error("加载存储配置失败", e);
            throw new RuntimeException("存储配置加载失败: " + e.getMessage(), e);
        }
    }

    /**
     * 刷新配置缓存
     */
    public String refreshCache() {
        sysFileConfigCache.clear();
        load();
        return sysFileConfigCache.get(StorageConstants.STORAGE_NAME);
    }

    /**
     * 按存储类型缓存配置
     */
    public void cacheSysFileConfigByStorageType(StorageConfig storageConfig) {
        String storageType = storageConfig.getStorageType();
        sysFileConfigCache.put(StorageConstants.CURRENT_DEFAULT_UPLOAD_TYPE, storageType);

        switch (storageType) {
            case StorageConstants.MINIO -> log.info("加载 Minio 配置");
            case StorageConstants.ALIYUN_OSS -> log.info("加载阿里云OSS配置");
            case StorageConstants.TENCENT_COS -> log.info("加载腾讯云COS配置");
            case StorageConstants.LOCAL -> log.info("加载本地文件系统配置");
            default -> throw new ProfileException(ResponseCode.PROFILE_ERROR, "未知的存储类型: " + storageType);
        }

        initCommonConfig(storageConfig);
    }

    /**
     * 自动设置为本地存储
     */
    private void autoSetLocalStorage() {
        try {
            String uploadPath = appProperty.getConfig().getUploadPath();
            if (uploadPath == null || uploadPath.isEmpty()) {
                throw new ProfileException("本地存储路径为空！");
            }
        } catch (Exception e) {
            log.error("未找到本地存储配置，上传将不可用！原因: {}", e.getMessage());
        }
    }

    /**
     * 获取当前默认上传类型
     */
    public String getCurrentDefaultUploadType() {
        return Optional.ofNullable(sysFileConfigCache.get(StorageConstants.CURRENT_DEFAULT_UPLOAD_TYPE))
                .filter(type -> !type.isEmpty())
                .orElseThrow(() -> new ProfileException(ResponseCode.PROFILE_ERROR, "请设置一个文件存储类型"));
    }

    /**
     * 获取 MinIO 配置
     */
    public MinioConfigEntity getMinioConfig() {
        return parseConfig(sysFileConfigCache.get(StorageConstants.MINIO), MinioConfigEntity.class);
    }

    /**
     * 获取腾讯云 COS 配置
     */
    public TencentCOSConfigEntity getTencentCOSConfig() {
        return parseConfig(sysFileConfigCache.get(StorageConstants.TENCENT_COS), TencentCOSConfigEntity.class);
    }

    /**
     * 获取阿里云 OSS 配置
     */
    public AliyunOSSConfigEntity getAliyunOSSConfig() {
        return parseConfig(sysFileConfigCache.get(StorageConstants.ALIYUN_OSS), AliyunOSSConfigEntity.class);
    }


    /**
     * 通用 JSON 配置解析方法
     */
    private <T> T parseConfig(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            log.warn("配置解析失败，类型: {}, 数据为空", clazz.getSimpleName());
            return null;
        }
        return JSON.parseObject(json, clazz);
    }

    /**
     * 初始化通用配置项（动态 Key 存储）
     */
    private void initCommonConfig(StorageConfig config) {
        String storageType = config.getStorageType();
        sysFileConfigCache.put(storageType, config.getStorageValue());
        sysFileConfigCache.put(StorageConstants.STORAGE_KEY, config.getStorageKey());
        sysFileConfigCache.put(StorageConstants.STORAGE_NAME, config.getStorageName());
    }
}
