package cn.zhangchuangla.storage.core.service.impl;

import cn.zhangchuangla.storage.constant.StorageConstants;
import cn.zhangchuangla.storage.core.service.FileOperationService;
import cn.zhangchuangla.storage.model.dto.FileTransferDto;
import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Chuang
 */
@Slf4j
@Service(StorageConstants.ALIYUN_OSS_STORAGE_SERVICE)
public class AliyunOssFileOperationServiceImpl implements FileOperationService {


    @Override
    public UploadedFileInfo upload(MultipartFile file) {
        return null;
    }

    @Override
    public boolean delete(String relativePath, boolean realDelete) {
        return false;
    }

    @Override
    public boolean restore(String originalRelativePath, String trashRelativePath) {
        return false;
    }

    @Override
    public boolean deleteTrash(String relativePath) {
        return false;
    }
}
