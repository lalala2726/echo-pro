package cn.zhangchuangla.storage.service;

import cn.zhangchuangla.common.core.exception.FileException;
import cn.zhangchuangla.storage.components.SpringContextHolder;
import cn.zhangchuangla.storage.constant.StorageConstants;
import cn.zhangchuangla.storage.core.service.FileOperationService;
import cn.zhangchuangla.storage.core.service.StorageConfigRetrievalService;
import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageConfigRetrievalService storageConfigRetrievalService;

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 上传结果
     */
    public UploadedFileInfo upload(MultipartFile file) {
        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();
        FileOperationService service = getService(activeStorageType);
        UploadedFileInfo upload = service.upload(file);
        //todo 存储文件信息
        return upload;
    }


    /**
     * 根据存储类型获取对应的存储服务
     *
     * @param type 存储类型
     * @return 存储服务
     */
    public FileOperationService getService(String type) {
        String beanName = getBeanNameByType(type);
        return SpringContextHolder.getBean(beanName, FileOperationService.class);
    }

    /**
     * 根据存储类型获取对应的存储服务名称
     *
     * @param type 存储类型
     * @return 存储服务名称
     */
    private String getBeanNameByType(String type) {
        return switch (type) {
            case StorageConstants.LOCAL -> StorageConstants.LOCAL_STORAGE_SERVICE;
            case StorageConstants.MINIO -> StorageConstants.MINIO_STORAGE_SERVICE;
            case StorageConstants.ALIYUN_OSS -> StorageConstants.ALIYUN_OSS_STORAGE_SERVICE;
            case StorageConstants.TENCENT_COS -> StorageConstants.TENCENT_COS_STORAGE_SERVICE;
            default -> throw new FileException("未知存储类型: " + type);
        };
    }

}
