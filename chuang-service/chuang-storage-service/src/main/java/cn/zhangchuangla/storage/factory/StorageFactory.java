package cn.zhangchuangla.storage.factory;

import cn.zhangchuangla.common.constant.StorageTypeConstants;
import cn.zhangchuangla.storage.core.StorageOperation;
import cn.zhangchuangla.storage.service.AliyunOssOperationService;
import cn.zhangchuangla.storage.service.MinioOperationService;
import cn.zhangchuangla.storage.service.TencentCOSOperationService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class StorageFactory {

    private final AliyunOssOperationService aliyunOssOperationService;
    private final TencentCOSOperationService tencentCOSOperationService;
    private final MinioOperationService minioOperationService;

    @Autowired
    public StorageFactory(AliyunOssOperationService aliyunOssOperationService, TencentCOSOperationService tencentCOSOperationService, MinioOperationService minioOperationService) {
        this.aliyunOssOperationService = aliyunOssOperationService;
        this.tencentCOSOperationService = tencentCOSOperationService;
        this.minioOperationService = minioOperationService;
    }

    /**
     * 根据存储类型获取存储服务
     *
     * @param storageType 存储类型
     * @return 存储服务
     */
    public StorageOperation getStorageOperation(String storageType) {
        return switch (storageType) {
            case StorageTypeConstants.ALIYUN_OSS -> aliyunOssOperationService;
            case StorageTypeConstants.TENCENT_COS -> tencentCOSOperationService;
            case StorageTypeConstants.MINIO -> minioOperationService;
            default -> null;
        };
    }
}
