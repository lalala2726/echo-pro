package cn.zhangchuangla.storage.factory;

import cn.zhangchuangla.common.constant.StorageConstants;
import cn.zhangchuangla.storage.core.StorageOperation;
import cn.zhangchuangla.storage.service.AliyunOssOperationService;
import cn.zhangchuangla.storage.service.LocalOperationService;
import cn.zhangchuangla.storage.service.MinioOperationService;
import cn.zhangchuangla.storage.service.TencentCOSOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 存储服务工厂类
 * 用于获取不同类型的存储服务
 *
 * @author Chuang
 * <p>
 * created on 2025/4/2
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class StorageFactory {

    private final AliyunOssOperationService aliyunOssOperationService;
    private final TencentCOSOperationService tencentCOSOperationService;
    private final MinioOperationService minioOperationService;
    private final LocalOperationService localOperationService;


    /**
     * 根据存储类型获取存储服务
     *
     * @param storageType 存储类型
     * @return 存储服务
     */
    public StorageOperation getStorageOperation(String storageType) {
        StorageOperation operation = switch (storageType) {
            case StorageConstants.ALIYUN_OSS -> aliyunOssOperationService;
            case StorageConstants.TENCENT_COS -> tencentCOSOperationService;
            case StorageConstants.MINIO -> minioOperationService;
            case StorageConstants.LOCAL -> localOperationService;
            default -> null;
        };

        if (operation == null) {
            log.warn("未找到类型为 [{}] 的存储服务，将使用本地存储作为默认服务", storageType);
            return localOperationService; // 默认使用本地存储
        }
        return operation;
    }
}
