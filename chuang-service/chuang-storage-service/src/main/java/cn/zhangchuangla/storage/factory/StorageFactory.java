package cn.zhangchuangla.storage.factory;

import cn.zhangchuangla.common.constant.StorageTypeConstants;
import cn.zhangchuangla.storage.core.StorageOperation;
import cn.zhangchuangla.storage.service.*;
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
    private final FTPOperationService ftpOperationService;
    private final NASOperationService nasOperationService;

    @Autowired
    public StorageFactory(AliyunOssOperationService aliyunOssOperationService, TencentCOSOperationService tencentCOSOperationService, MinioOperationService minioOperationService, FTPOperationService ftpOperationService, NASOperationService nasOperationService) {
        this.aliyunOssOperationService = aliyunOssOperationService;
        this.tencentCOSOperationService = tencentCOSOperationService;
        this.minioOperationService = minioOperationService;
        this.ftpOperationService = ftpOperationService;
        this.nasOperationService = nasOperationService;
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
            case StorageTypeConstants.FTP -> ftpOperationService;
            case StorageTypeConstants.LOCAL -> nasOperationService;
            default -> null;
        };
    }
}
