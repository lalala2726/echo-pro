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
import cn.zhangchuangla.storage.model.request.file.FileRecordQueryRequest;
import cn.zhangchuangla.storage.service.StorageManageService;
import cn.zhangchuangla.storage.service.StorageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
     * @param fileIds     文件ID集合
     * @param forceDelete 是否强制删除（不经过回收站）
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = {FileException.class})
    public boolean deleteFileById(List<Long> fileIds, boolean forceDelete) {
        // 1. 参数校验
        if (CollectionUtils.isEmpty(fileIds)) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "文件ID不能为空");
        }

        // 2. 获取文件记录并校验
        List<FileRecord> fileRecords = getAndValidateFileRecords(fileIds);

        // 3. 获取存储服务和配置
        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();
        FileOperationService service = getService(activeStorageType);

        // 4. 批量处理文件删除操作
        processFileDeletion(fileRecords, service, forceDelete);

        // 5. 批量更新数据库
        return storageManageService.updateBatchById(fileRecords);
    }

    /**
     * 删除文件
     *
     * @param fileId      文件UD
     * @param forceDelete 是否强制删除(不经过回收站)
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = {FileException.class})
    public boolean deleteFileById(Long fileId, boolean forceDelete) {
        List<Long> list = Collections.singletonList(fileId);
        return deleteFileById(list, forceDelete);
    }


    /**
     * 获取并校验文件记录
     */
    private List<FileRecord> getAndValidateFileRecords(List<Long> fileIds) {
        LambdaQueryWrapper<FileRecord> queryWrapper = new LambdaQueryWrapper<FileRecord>()
                .in(FileRecord::getId, fileIds);
        List<FileRecord> fileRecords = storageManageService.list(queryWrapper);

        if (CollectionUtils.isEmpty(fileRecords)) {
            throw new ServiceException(ResponseCode.DATA_NOT_FOUND, "文件不存在");
        }

        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();

        // 批量校验文件状态
        fileRecords.forEach(fileRecord -> {
            validateFileStorageConsistency(activeStorageType, fileRecord);
            if (StorageConstants.dataVerifyConstants.IN_TRASH.equals(fileRecord.getIsTrash())) {
                throw new ServiceException(ResponseCode.OPERATION_ERROR,
                        String.format("文件编号: %s 已经在回收站中!无法再次删除!", fileRecord.getId()));
            }
        });

        return fileRecords;
    }

    /**
     * 批量处理文件删除操作
     */
    private void processFileDeletion(List<FileRecord> fileRecords, FileOperationService service, boolean forceDelete) {
        fileRecords.forEach(fileRecord -> {
            // 构建文件操作DTO
            FileOperationDto fileOperationDto = FileOperationDto.builder()
                    .previewRelativePath(fileRecord.getPreviewImagePath())
                    .originalRelativePath(fileRecord.getOriginalRelativePath())
                    .build();
            if (forceDelete) {
                //这边是强制删除
                service.delete(fileOperationDto, true);
                //标记为已删除
                fileRecord.setIsDeleted(Constants.LogicDelete.DELETED);
            } else {
                FileOperationDto fileOperationDtoResult = service.delete(fileOperationDto, false);
                fileRecord.setOriginalTrashPath(fileOperationDtoResult.getOriginalTrashPath());
                fileRecord.setIsTrash(StorageConstants.dataVerifyConstants.IN_TRASH);
                if (StringUtils.isNotBlank(fileOperationDtoResult.getPreviewTrashPath())) {
                    fileRecord.setPreviewTrashPath(fileOperationDtoResult.getPreviewTrashPath());
                }
            }
        });
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
