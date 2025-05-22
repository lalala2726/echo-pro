package cn.zhangchuangla.storage.core;

import cn.hutool.core.util.StrUtil;
import cn.zhangchuangla.common.core.constant.StorageConstants;
import cn.zhangchuangla.common.core.exception.ProfileException;
import cn.zhangchuangla.common.core.utils.StringUtils;
import cn.zhangchuangla.storage.StorageType;
import cn.zhangchuangla.storage.config.StorageSystemProperties;
import cn.zhangchuangla.storage.exception.StorageException;
import cn.zhangchuangla.storage.loader.StorageConfigLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Chuang
 */
@Slf4j
@Component
public class StorageManager {

    private final ApplicationContext applicationContext;
    private final StorageSystemProperties storageSystemProperties;
    private final StorageConfigLoader storageConfigLoader;

    private StorageService activeStorageService;
    private Object activeSpecificProperties;

    public StorageManager(ApplicationContext applicationContext,
                          StorageSystemProperties storageSystemProperties,
                          StorageConfigLoader storageConfigLoader) {
        this.applicationContext = applicationContext;
        this.storageSystemProperties = storageSystemProperties;
        this.storageConfigLoader = storageConfigLoader;
        determineActiveStorageService();
    }

    private void determineActiveStorageService() {
        String activeTypeString = storageSystemProperties.getActiveType();

        if (StringUtils.isBlank(activeTypeString)) {
            log.info("未在 application.yml 中配置 'storage.active-type'，尝试从数据库加载...");
            try {
                activeTypeString = storageConfigLoader.getCurrentDefaultUploadType();
                if (StringUtils.isBlank(activeTypeString)) {
                    log.warn("数据库中配置的激活存储类型为空，默认使用 LOCAL。");
                    activeTypeString = StorageType.LOCAL.name();
                } else {
                    log.info("从数据库加载到激活的存储类型: {}", activeTypeString);
                }
            } catch (ProfileException e) {
                log.warn("从数据库加载激活存储类型失败 (可能未配置): {}。默认使用 LOCAL。", e.getMessage());
                activeTypeString = StorageType.LOCAL.name();
            }
        } else {
            log.info("从 application.yml 加载到激活的存储类型: {}", activeTypeString);
        }

        StorageType activeStorageType;
        try {
            activeStorageType = StorageType.valueOf(activeTypeString.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("无效的存储类型配置: '{}'. 请检查 'storage.active-type' 或数据库中的配置.", activeTypeString);
            throw new StorageException("无效的存储类型: " + activeTypeString + "，将默认使用 LOCAL", e);
        }

        log.info("最终确定的激活存储类型为: {}", activeStorageType);

        this.activeSpecificProperties = getSpecificPropertiesForType(activeStorageType);
        if (this.activeSpecificProperties == null) {
            log.error("无法获取类型 {} 的具体配置信息，即使类型已确定。请检查 StorageSystemProperties 中的配置块。", activeStorageType);
            throw new StorageException("无法为类型 " + activeStorageType + " 获取存储配置。");
        }

        this.activeStorageService = getServiceBeanForType(activeStorageType);

        if (this.activeStorageService == null) {
            log.error("StorageManager 初始化完成，但没有激活的存储服务配置或可用。存储操作将会失败。");
            throw new StorageException("没有找到可用的存储服务实现，请检查配置和Bean定义以及激活类型设置。");
        }
        log.info("成功激活存储服务: {}", activeStorageType);
    }

    private Object getSpecificPropertiesForType(StorageType type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case LOCAL -> Optional.ofNullable(storageSystemProperties.getLocal())
                    .filter(c -> StrUtil.isNotBlank(c.getRootPathOrBucketName()))
                    .orElseThrow(() -> new StorageException("LOCAL 存储配置不完整或未找到 (local.rootPathOrBucketName 缺失)"));
            case MINIO -> Optional.ofNullable(storageSystemProperties.getMinio())
                    .filter(c -> org.apache.commons.lang3.StringUtils.isNoneBlank(c.getEndpoint(), c.getAccessKey(), c.getSecretKey(), c.getRootPathOrBucketName()))
                    .orElseThrow(() -> new StorageException("MinIO 存储配置不完整或未找到 (minio 部分属性缺失)"));
            case ALIYUN_OSS -> Optional.ofNullable(storageSystemProperties.getAliyunOss())
                    .filter(c -> org.apache.commons.lang3.StringUtils.isNoneBlank(c.getEndpoint(), c.getAccessKeyId(), c.getAccessKeySecret(), c.getRootPathOrBucketName()))
                    .orElseThrow(() -> new StorageException("Aliyun OSS 存储配置不完整或未找到 (aliyunOss 部分属性缺失)"));
            case TENCENT_COS -> Optional.ofNullable(storageSystemProperties.getTencentCos())
                    .filter(c -> org.apache.commons.lang3.StringUtils.isNoneBlank(c.getRegion(), c.getSecretId(), c.getSecretKey(), c.getRootPathOrBucketName()))
                    .orElseThrow(() -> new StorageException("Tencent COS 存储配置不完整或未找到 (tencentCos 部分属性缺失)"));
        };
    }

    public StorageService getActiveStorageService() {
        if (activeStorageService == null) {
            log.error("试图获取激活的存储服务，但没有配置。请检查您的存储配置和激活逻辑。");
            throw new StorageException("没有配置或激活的存储服务。");
        }
        return activeStorageService;
    }

    public Object getActiveStorageSpecificProperties() {
        if (activeSpecificProperties == null) {
            log.error("试图获取激活的存储特定配置，但没有找到。可能初始化失败或未配置激活服务。");
            throw new StorageException("没有激活的存储服务或其特定配置信息。");
        }
        return activeSpecificProperties;
    }

    public StorageService getServiceBeanForType(StorageType type) {
        String beanName = switch (type) {
            case LOCAL -> StorageConstants.LOCAL_STORAGE_SERVICE;
            case MINIO -> StorageConstants.MINIO_STORAGE_SERVICE;
            case ALIYUN_OSS -> StorageConstants.ALIYUN_OSS_STORAGE_SERVICE;
            case TENCENT_COS -> StorageConstants.TENCENT_COS_STORAGE_SERVICE;
        };
        try {
            return applicationContext.getBean(beanName, StorageService.class);
        } catch (NoSuchBeanDefinitionException e) {
            log.error("StorageService bean not found for type: {} (expected bean name: {})", type, beanName);
            throw new StorageException("找不到类型 " + type + " 对应的存储服务Bean: " + beanName, e);
        }
    }
}
