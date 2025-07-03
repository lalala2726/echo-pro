package cn.zhangchuangla.storage.core.service.impl;

import cn.zhangchuangla.common.core.exception.FileException;
import cn.zhangchuangla.storage.async.StorageAsyncService;
import cn.zhangchuangla.storage.constant.StorageConstants;
import cn.zhangchuangla.storage.core.service.FileOperationService;
import cn.zhangchuangla.storage.core.service.StorageConfigRetrievalService;
import cn.zhangchuangla.storage.model.dto.FileOperationDto;
import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import cn.zhangchuangla.storage.model.entity.config.MinioStorageConfig;
import com.alibaba.fastjson2.JSON;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/3 06:26
 */
@Service
public class MinioFileOperationServiceImpl implements FileOperationService {

    private final StorageConfigRetrievalService storageConfigRetrievalService;
    private final StorageAsyncService storageAsyncService;
    private MinioStorageConfig minioStorageConfig;

    public MinioFileOperationServiceImpl(StorageConfigRetrievalService storageConfigRetrievalService, StorageAsyncService storageAsyncService) {
        this.storageConfigRetrievalService = storageConfigRetrievalService;
        this.storageAsyncService = storageAsyncService;
    }

    public MinioStorageConfig getConfig() {
        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();
        String json = storageConfigRetrievalService.getCurrentStorageConfigJson();
        if (!StorageConstants.StorageType.MINIO.equals(activeStorageType)) {
            throw new FileException(String.format("当前调用的服务是:%s,而你激活的配置是:%s,调用的服务和激活的配置不符合!请你仔细检查配置!"
                    , StorageConstants.StorageType.LOCAL, activeStorageType));
        }
        if (json == null || json.isBlank()) {
            throw new FileException("本地文件存储配置未找到");
        }
        minioStorageConfig = JSON.parseObject(json, MinioStorageConfig.class);
        return minioStorageConfig;
    }

    @Override
    public UploadedFileInfo upload(MultipartFile file) {
        return null;
    }

    @Override
    public UploadedFileInfo uploadImage(MultipartFile file) {
        return null;
    }

    @Override
    public FileOperationDto delete(FileOperationDto fileOperationDto, boolean forceDelete) {
        return null;
    }

    @Override
    public boolean restore(FileOperationDto fileOperationDto) {
        return false;
    }

    @Override
    public void deleteTrashFile(FileOperationDto fileOperationDto) {

    }
}
