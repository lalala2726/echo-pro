package cn.zhangchuangla.storage.core.service.impl;

import cn.zhangchuangla.storage.constant.StorageConstants;
import cn.zhangchuangla.storage.core.service.FileOperationService;
import cn.zhangchuangla.storage.model.dto.FileTrashInfoDTO;
import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import cn.zhangchuangla.storage.model.entity.FileRecord;
import cn.zhangchuangla.storage.model.entity.config.LocalFileStorageConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Chuang
 */
@Slf4j
@Service(StorageConstants.springBeanName.ALIYUN_OSS_STORAGE_SERVICE)
public class AliyunOssFileOperationServiceImpl implements FileOperationService {


    @Override
    public LocalFileStorageConfig getConfig() {
        return null;
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
    public FileTrashInfoDTO delete(String originalRelativePath, String previewRelativePath, boolean forceDelete) {
        return null;
    }

    @Override
    public boolean restore(FileRecord fileRecord) {
        return false;
    }


    @Override
    public boolean deleteTrashFile(String relativePath) {
        return false;
    }
}
