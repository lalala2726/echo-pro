package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.core.constant.Constants;
import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.FileException;
import cn.zhangchuangla.common.core.exception.ParamException;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.storage.components.SpringContextHolder;
import cn.zhangchuangla.storage.constant.StorageConstants;
import cn.zhangchuangla.storage.core.service.FileOperationService;
import cn.zhangchuangla.storage.core.service.StorageConfigRetrievalService;
import cn.zhangchuangla.storage.model.dto.FileOperationDto;
import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import cn.zhangchuangla.storage.model.entity.FileRecord;
import cn.zhangchuangla.storage.model.entity.config.LocalFileStorageConfig;
import cn.zhangchuangla.storage.model.request.file.FileRecordQueryRequest;
import cn.zhangchuangla.storage.service.StorageManageService;
import cn.zhangchuangla.storage.service.StorageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Chuang
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    //文件最大大小
    private static final long MAX_FILE_SIZE = 1024 * 1024 * 50;
    //图片最大大小
    private static final long MAX_IMAGE_SIZE = 1024 * 1024 * 5;

    private final StorageConfigRetrievalService storageConfigRetrievalService;
    private final StorageManageService storageManageService;

    /**
     * 列出文件列表
     *
     * @param request 查询参数
     * @return 文件列表
     */
    @Override
    public Page<FileRecord> listFileManage(FileRecordQueryRequest request) {
        return storageManageService.listFileManage(request);
    }

    /**
     * 列出回收站文件列表
     *
     * @param request 删除参数
     * @return 回收站文件列表
     */
    @Override
    public Page<FileRecord> listFileTrashManage(FileRecordQueryRequest request) {
        return storageManageService.listFileTrashManage(request);
    }

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
            throw new ServiceException(ResponseCode.FileUploadFailed, "此接口文件大小最大不能超过50M");
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
            throw new ServiceException(ResponseCode.FileUploadFailed, "图片大小不能超过5M");
        }
        String contentType = file.getContentType();
        if (contentType != null && !contentType.startsWith("image")) {
            throw new ServiceException(ResponseCode.FileUploadFailed, "只能上传图片类型的资源");
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
    @Transactional(rollbackFor = {FileException.class})
    public boolean deleteFileById(Long fileId, boolean forceDelete) {
        if (fileId == null || fileId <= 0) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "文件ID不能为空");
        }

        // 1. 获取文件信息
        FileRecord fileRecord = storageManageService.getById(fileId);

        if (fileRecord == null) {
            throw new ServiceException(ResponseCode.RESULT_IS_NULL, "文件不存在");
        }
        if (StorageConstants.dataVerifyConstants.IN_TRASH.equals(fileRecord.getIsTrash())) {
            throw new ServiceException(ResponseCode.RESULT_IS_NULL, "文件已经在回收站中!无法再次删除!");
        }

        // 2. 获取当前激活的存储类型
        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();
        // 校验文件存储类型一致性
        validateFileStorageConsistency(activeStorageType, fileRecord);

        // 3. 获取对应的文件操作服务
        FileOperationService service = getService(activeStorageType);

        // 4. 获取存储配置，判断是否真实删除
        LocalFileStorageConfig config = service.getConfig();
        boolean realDelete = config.isRealDelete();

        // 创建文件操作DTO
        FileOperationDto fileOperationDto = FileOperationDto.builder()
                .previewRelativePath(fileRecord.getPreviewImagePath())
                .originalRelativePath(fileRecord.getOriginalRelativePath())
                .build();
        // 5. 执行删除操作
        if (realDelete) {
            // 配置为真实删除模式，无论是否强制删除，都会物理删除文件
            log.info("执行物理删除文件，文件ID: {}", fileId);
            service.delete(fileOperationDto, true);

            // 标记数据库记录为已删除
            fileRecord.setIsDeleted(StorageConstants.dataVerifyConstants.FILE_DELETED);
            // 清空回收站相关字段
            fileRecord.setIsTrash(StorageConstants.dataVerifyConstants.NOT_IN_TRASH);
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

                FileOperationDto trashInfo = service.delete(fileOperationDto, false
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
    public boolean deleteFileById(List<Long> ids, boolean forceDelete) {
        //fixme 这边涉及批量的操作的时候,需要统一从数据库中获取数据,然后进行批量操作
        for (Long id : ids) {
            if (!deleteFileById(id, forceDelete)) {
                throw new ServiceException(String.format("文件ID: %d 删除失败", id));
            }
        }
        return true;
    }

    /**
     * 从回收站还原文件
     *
     * @param fileIds 文件ID集合
     * @return 是否还原成功
     */
    @Override
    public boolean restoreFileFromRecycleBin(List<Long> fileIds) {
        if (CollectionUtils.isEmpty(fileIds)) {
            throw new ParamException(ResponseCode.PARAM_NOT_NULL, "文件ID不能为空");
        }
        LambdaQueryWrapper<FileRecord> in = new LambdaQueryWrapper<FileRecord>().in(FileRecord::getId, fileIds);
        List<FileRecord> fileRecords = storageManageService.list(in);
        if (CollectionUtils.isEmpty(fileRecords)) {
            throw new ServiceException(ResponseCode.DATA_NOT_FOUND, "文件不存在");
        }
        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();

        // 校验文件存储类型一致性和判断是否在回收站中
        fileRecords.forEach(fileRecord -> {
            validateFileStorageConsistency(activeStorageType, fileRecord);
            if (!StorageConstants.dataVerifyConstants.IN_TRASH.equals(fileRecord.getIsTrash())) {
                throw new ServiceException(ResponseCode.FILE_OPERATION_FAILED, "文件未处于回收站中或已被删除，无法恢复");
            }
        });

        FileOperationService service = getService(activeStorageType);
        //进行文件操作
        fileRecords.forEach(fileRecord -> {
            FileOperationDto fileOperationDto = new FileOperationDto();
            BeanUtils.copyProperties(fileRecord, fileOperationDto);
            boolean restore = service.restore(fileOperationDto);
            //恢复成功后将数据库中文件状态改为正常
            if (restore) {
                fileRecord.setIsTrash(StorageConstants.dataVerifyConstants.NOT_IN_TRASH);
                fileRecord.setIsDeleted(Constants.LogicDelete.NOT_DELETED);
                // 清空回收站路径信息，恢复原始路径信息
                fileRecord.setOriginalTrashPath(null);
                fileRecord.setPreviewTrashPath(null);
                fileRecord.setUpdateTime(new Date());
                storageManageService.updateById(fileRecord);
            }
        });

        return true;
    }

    /**
     * 根据文件ID恢复文件
     *
     * @param fileId 文件ID
     * @return 是否成功
     */
    @Override
    public boolean restoreFileFromRecycleBin(Long fileId) {
        List<Long> list = Collections.singletonList(fileId);
        return restoreFileFromRecycleBin(list);
    }


    /**
     * 批量删除回收站中的文件
     *
     * @param fileIds 要删除的文件ID列表
     * @return 操作是否成功
     */
    @Override
    public boolean deleteTrashFileById(List<Long> fileIds) {
        if (CollectionUtils.isEmpty(fileIds)) {
            throw new ServiceException("文件ID不能为空");
        }

        // 查询文件记录
        LambdaQueryWrapper<FileRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FileRecord::getId, fileIds);
        List<FileRecord> recordList = storageManageService.list(queryWrapper);

        if (CollectionUtils.isEmpty(recordList)) {
            throw new ServiceException(ResponseCode.RESULT_IS_NULL, "未找到指定的文件记录");
        }

        // 校验文件存储类型一致性，并检查文件是否确实在回收站中
        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();
        recordList.forEach(fileRecord -> {
            validateFileStorageConsistency(activeStorageType, fileRecord);

            // 确保文件处于回收站状态
            if (!StorageConstants.dataVerifyConstants.IN_TRASH.equals(fileRecord.getIsTrash())) {
                throw new ServiceException(
                        ResponseCode.OPERATION_ERROR,
                        String.format("文件 %s 不在回收站中，无法执行删除操作", fileRecord.getOriginalName())
                );
            }
        });

        // 获取当前存储服务
        FileOperationService service = getService(activeStorageType);
        // 对每个文件执行物理删除并更新数据库状态
        recordList.forEach(fileRecord -> {
            // 构造文件操作DTO，包含回收站路径信息
            FileOperationDto fileOperationDto = FileOperationDto.builder()
                    .originalTrashPath(fileRecord.getOriginalTrashPath())
                    .previewTrashPath(fileRecord.getPreviewTrashPath())
                    .build();

            // 调用底层服务执行物理删除
            service.deleteTrashFile(fileOperationDto);
        });
        //删除数据库记录
        storageManageService.removeBatchByIds(recordList);

        // 批量更新文件状态
        return storageManageService.updateBatchById(recordList);
    }


    /**
     * 根据存储类型获取对应的存储服务
     *
     * @param type 存储类型
     * @return 存储服务
     */
    private FileOperationService getService(String type) {
        String beanName = getBeanNameByType(type);
        return SpringContextHolder.getBean(beanName, FileOperationService.class);
    }

    /**
     * 校验文件的存储和当前系统使用的存储是否一致
     *
     * @param activeStorageType 当前系统使用的存储类型
     * @param fileRecord        文件记录
     */
    private void validateFileStorageConsistency(String activeStorageType, FileRecord fileRecord) {
        if (!activeStorageType.equals(fileRecord.getStorageType())) {
            throw new ServiceException(
                    ResponseCode.OPERATION_ERROR,
                    String.format("存储类型不匹配，无法执行删除操作。当前存储类型: %s，当前文件ID为 %d 的实际存储类型: %s",
                            activeStorageType, fileRecord.getId(), fileRecord.getStorageType())
            );
        }
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
            default -> throw new ServiceException("未知存储类型: " + type);
        };
    }
}
