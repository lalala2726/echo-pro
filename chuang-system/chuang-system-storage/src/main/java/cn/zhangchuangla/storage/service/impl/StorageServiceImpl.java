package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.FileException;
import cn.zhangchuangla.common.core.exception.ParamException;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.storage.components.SpringContextHolder;
import cn.zhangchuangla.storage.constant.StorageConstants;
import cn.zhangchuangla.storage.core.service.FileOperationService;
import cn.zhangchuangla.storage.core.service.StorageConfigRetrievalService;
import cn.zhangchuangla.storage.model.dto.FileTrashInfoDTO;
import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import cn.zhangchuangla.storage.model.entity.FileRecord;
import cn.zhangchuangla.storage.model.entity.config.LocalFileStorageConfig;
import cn.zhangchuangla.storage.service.StorageManageService;
import cn.zhangchuangla.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * @author Chuang
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final StorageConfigRetrievalService storageConfigRetrievalService;
    private final StorageManageService storageManageService;

    //文件最大大小
    private static final long MAX_FILE_SIZE = 1024 * 1024 * 50;
    //图片最大大小
    private static final long MAX_IMAGE_SIZE = 1024 * 1024 * 5;


    /**
     * 普通文件上传
     *
     * @param file 文件
     * @return 上传结果
     */
    @Override
    public UploadedFileInfo upload(MultipartFile file) {
        //获取文件大小
        long fileSize = file.getSize();
        if (fileSize > MAX_FILE_SIZE) {
            throw new FileException(ResponseCode.FileUploadFailed, "此接口文件大小最大不能超过50M");
        }
        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();
        FileOperationService service = getService(activeStorageType);
        UploadedFileInfo upload = service.upload(file);


        // 保存文件信息
        String username = SecurityUtils.getUsername();
        FileRecord fileRecord = FileRecord.builder()
                .originalName(file.getOriginalFilename())
                .fileName(upload.getFileName())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .originalFileUrl(upload.getFileUrl())
                .originalRelativePath(upload.getFileRelativePath())
                .uploadTime(new Date())
                .uploaderName(username)
                .fileExtension(upload.getFileExtension())
                .storageType(activeStorageType)
                .build();
        storageManageService.saveFileInfo(fileRecord);
        return upload;
    }

    /**
     * 图片上传
     *
     * @param file 文件
     * @return 上传结果
     */
    @Override
    public UploadedFileInfo uploadImage(MultipartFile file) {

        long fileSize = file.getSize();
        if (fileSize > MAX_IMAGE_SIZE) {
            throw new FileException(ResponseCode.FileUploadFailed, "图片大小不能超过5M");
        }
        String contentType = file.getContentType();
        if (contentType != null && !contentType.startsWith("image")) {
            throw new FileException(ResponseCode.FileUploadFailed, "只能上传图片类型的资源");
        }
        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();
        FileOperationService service = getService(activeStorageType);
        UploadedFileInfo uploadedFileInfo = service.uploadImage(file);

        //保存文件信息
        String username = SecurityUtils.getUsername();
        FileRecord fileRecord = FileRecord.builder()
                .originalName(file.getOriginalFilename())
                .fileName(uploadedFileInfo.getFileName())
                .previewImageUrl(uploadedFileInfo.getPreviewImage())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .previewImagePath(uploadedFileInfo.getPreviewImageRelativePath())
                .originalFileUrl(uploadedFileInfo.getFileUrl())
                .originalRelativePath(uploadedFileInfo.getFileRelativePath())
                .uploadTime(new Date())
                .uploaderName(username)
                .fileExtension(uploadedFileInfo.getFileExtension())
                .storageType(activeStorageType)
                .build();
        storageManageService.saveFileInfo(fileRecord);
        return uploadedFileInfo;
    }

    /**
     * 删除文件
     *
     * @param fileId      文件ID
     * @param forceDelete 是否强制删除（不经过回收站）
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = FileException.class)
    public boolean delete(Long fileId, boolean forceDelete) {
        if (fileId == null || fileId <= 0) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "文件ID不能为空");
        }

        // 1. 获取文件信息
        FileRecord fileRecord = storageManageService.getById(fileId);
        if (fileRecord == null) {
            throw new ServiceException(ResponseCode.RESULT_IS_NULL, "文件不存在");
        }

        // 2. 获取当前激活的存储类型
        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();
        if (!activeStorageType.equals(fileRecord.getStorageType())) {
            throw new ServiceException(
                    ResponseCode.OPERATION_ERROR,
                    String.format("存储类型不匹配，无法执行删除操作。当前存储类型: %s，文件ID: %d 的实际存储类型: %s",
                            activeStorageType, fileId, fileRecord.getStorageType())
            );
        }

        // 3. 获取对应的文件操作服务
        FileOperationService service = getService(activeStorageType);

        // 4. 获取存储配置，判断是否真实删除
        LocalFileStorageConfig config = service.getConfig();
        boolean realDelete = config.isRealDelete();

        // 5. 执行删除操作
        if (realDelete) {
            // 配置为真实删除模式，无论是否强制删除，都会物理删除文件
            log.info("执行物理删除文件，文件ID: {}", fileId);
            service.delete(fileRecord.getOriginalRelativePath(), fileRecord.getPreviewImagePath(), true);

            // 标记数据库记录为已删除
            fileRecord.setIsDeleted(StorageConstants.dataVerifyConstants.FILE_DELETED);
            // 清空回收站相关字段
            fileRecord.setIsTrash(0);
            fileRecord.setOriginalTrashPath(null);
            fileRecord.setPreviewTrashPath(null);
        } else {
            // 配置为逻辑删除模式
            if (forceDelete) {
                // 虽然指定强制删除，但配置为逻辑删除，所以只在数据库标记为已删除
                log.info("配置为逻辑删除模式，执行数据库标记删除，文件ID: {}", fileId);
                fileRecord.setIsDeleted(StorageConstants.dataVerifyConstants.FILE_DELETED);
            } else {
                // 移入回收站模式
                log.info("执行移入回收站操作，文件ID: {}", fileId);
                FileTrashInfoDTO trashInfo = service.delete(
                        fileRecord.getOriginalRelativePath(),
                        fileRecord.getPreviewImagePath(),
                        false
                );

                if (trashInfo != null) {
                    // 更新记录为已放入回收站
                    fileRecord.setIsTrash(StorageConstants.dataVerifyConstants.IN_TRASH);
                    fileRecord.setOriginalTrashPath(trashInfo.getOriginalTrashPath());
                    fileRecord.setPreviewTrashPath(trashInfo.getPreviewTrashPath());
                    // 清空原路径信息
                    fileRecord.setOriginalRelativePath(null);
                    fileRecord.setPreviewImagePath(null);
                    fileRecord.setOriginalFileUrl(null);
                    fileRecord.setPreviewImageUrl(null);
                } else {
                    // 如果文件在磁盘上不存在，但记录存在，直接标记为逻辑删除
                    log.warn("文件在物理存储中不存在，但数据库记录存在。文件ID: {}. 将执行逻辑删除。", fileId);
                    fileRecord.setIsDeleted(StorageConstants.dataVerifyConstants.FILE_DELETED);
                }
            }
        }

        // 6. 更新数据库记录
        return storageManageService.updateById(fileRecord);
    }

    /**
     * 批量删除文件
     *
     * @param ids         文件ID列表
     * @param forceDelete 是否强制删除（不经过回收站）
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = FileException.class)
    public boolean delete(List<Long> ids, boolean forceDelete) {
        for (Long id : ids) {
            if (!delete(id, forceDelete)) {
                return false;
            }
        }
        return true;
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
            case StorageConstants.StorageType.LOCAL -> StorageConstants.springBeanName.LOCAL_STORAGE_SERVICE;
            case StorageConstants.StorageType.MINIO -> StorageConstants.springBeanName.MINIO_STORAGE_SERVICE;
            case StorageConstants.StorageType.ALIYUN_OSS -> StorageConstants.springBeanName.ALIYUN_OSS_STORAGE_SERVICE;
            case StorageConstants.StorageType.TENCENT_COS ->
                    StorageConstants.springBeanName.TENCENT_COS_STORAGE_SERVICE;
            default -> throw new FileException("未知存储类型: " + type);
        };
    }
}
