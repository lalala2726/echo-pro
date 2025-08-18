package cn.zhangchuangla.system.storage.service.impl;

import cn.zhangchuangla.common.core.constant.Constants;
import cn.zhangchuangla.common.core.entity.security.SysUserDetails;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.FileException;
import cn.zhangchuangla.common.core.exception.ParamException;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.system.storage.components.SpringContextHolder;
import cn.zhangchuangla.system.storage.constant.StorageConstants;
import cn.zhangchuangla.system.storage.core.service.OperationService;
import cn.zhangchuangla.system.storage.core.service.StorageConfigRetrievalService;
import cn.zhangchuangla.system.storage.mapper.StorageFileMapper;
import cn.zhangchuangla.system.storage.model.dto.FileOperationDto;
import cn.zhangchuangla.system.storage.model.dto.UploadedFileInfo;
import cn.zhangchuangla.system.storage.model.entity.StorageFile;
import cn.zhangchuangla.system.storage.model.entity.config.AliyunOssStorageConfig;
import cn.zhangchuangla.system.storage.model.entity.config.AmazonS3StorageConfig;
import cn.zhangchuangla.system.storage.model.entity.config.MinioStorageConfig;
import cn.zhangchuangla.system.storage.model.entity.config.TencentCosStorageConfig;
import cn.zhangchuangla.system.storage.model.request.file.StorageFileQueryRequest;
import cn.zhangchuangla.system.storage.service.StorageFileService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class StorageFileServiceImpl extends ServiceImpl<StorageFileMapper, StorageFile>
        implements StorageFileService {

    //文件最大大小
    private static final long MAX_FILE_SIZE = 1024 * 1024 * 50;
    //图片最大大小
    private static final long MAX_IMAGE_SIZE = 1024 * 1024 * 5;

    private final StorageConfigRetrievalService storageConfigRetrievalService;
    private final StorageFileMapper storageFileMapper;

    /**
     * 列出文件列表
     *
     * @param request 查询参数
     * @return 文件列表
     */
    @Override
    public Page<StorageFile> listFileManage(StorageFileQueryRequest request) {
        Page<StorageFile> page = new Page<>(request.getPageNum(), request.getPageSize());
        return storageFileMapper.listFileManage(page, request);
    }

    /**
     * 列出回收站文件列表
     *
     * @param request 删除参数
     * @return 回收站文件列表
     */
    @Override
    public Page<StorageFile> listFileTrashManage(StorageFileQueryRequest request) {
        Page<StorageFile> page = new Page<>(request.getPageNum(), request.getPageSize());
        return storageFileMapper.listFileTrashManage(page, request);
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
            throw new ServiceException(ResultCode.FileUploadFailed, "此接口文件大小最大不能超过50M");
        }
        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();
        OperationService service = getService(activeStorageType);
        UploadedFileInfo upload = service.upload(file);


        // 保存文件信息
        SysUserDetails loginUser = SecurityUtils.getLoginUser();
        StorageFile storageFile = StorageFile.builder()
                .originalName(file.getOriginalFilename())
                .fileName(upload.getFileName())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .originalFileUrl(upload.getFileUrl())
                .originalRelativePath(upload.getFileRelativePath())
                .bucketName(upload.getBucketName())
                .uploadTime(new Date())
                .uploaderId(loginUser.getUserId())
                .uploaderName(loginUser.getUsername())
                .fileExtension(upload.getFileExtension())
                .storageType(activeStorageType)
                .build();
        save(storageFile);
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
            throw new ServiceException(ResultCode.FileUploadFailed, "图片大小不能超过5M");
        }
        String contentType = file.getContentType();
        if (contentType != null && !contentType.startsWith("image")) {
            throw new ServiceException(ResultCode.FileUploadFailed, "只能上传图片类型的资源");
        }
        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();
        OperationService service = getService(activeStorageType);
        UploadedFileInfo uploadedFileInfo = service.uploadImage(file);

        //保存文件信息
        SysUserDetails loginUser = SecurityUtils.getLoginUser();
        StorageFile storageFile = StorageFile.builder()
                .originalName(file.getOriginalFilename())
                .fileName(uploadedFileInfo.getFileName())
                .previewImageUrl(uploadedFileInfo.getPreviewImageUrl())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .bucketName(uploadedFileInfo.getBucketName())
                .uploaderId(loginUser.getUserId())
                .uploaderName(loginUser.getUsername())
                .previewRelativePath(uploadedFileInfo.getPreviewImageRelativePath())
                .originalFileUrl(uploadedFileInfo.getFileUrl())
                .originalRelativePath(uploadedFileInfo.getFileRelativePath())
                .uploadTime(new Date())
                .fileExtension(uploadedFileInfo.getFileExtension())
                .storageType(activeStorageType)
                .build();
        save(storageFile);
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
            throw new ParamException(ResultCode.PARAM_ERROR, "文件ID不能为空");
        }

        // 2. 获取文件记录并校验
        List<StorageFile> StorageFiles = getAndValidateFileRecords(fileIds);

        // 3. 获取存储服务和配置
        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();
        OperationService service = getService(activeStorageType);

        // 4. 批量处理文件删除操作
        processFileDeletion(StorageFiles, service, forceDelete);

        // 5. 批量更新数据库
        return updateBatchById(StorageFiles);
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
    private List<StorageFile> getAndValidateFileRecords(List<Long> fileIds) {
        LambdaQueryWrapper<StorageFile> queryWrapper = new LambdaQueryWrapper<StorageFile>()
                .in(StorageFile::getId, fileIds);
        List<StorageFile> storageFiles = list(queryWrapper);

        if (CollectionUtils.isEmpty(storageFiles)) {
            throw new ServiceException(ResultCode.DATA_NOT_FOUND, "文件不存在");
        }

        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();

        // 批量校验文件状态
        storageFiles.forEach(file -> {
            validateStorageConsistency(activeStorageType, file);
            if (StorageConstants.dataVerifyConstants.IN_TRASH.equals(file.getIsTrash())) {
                throw new ServiceException(ResultCode.OPERATION_ERROR,
                        String.format("文件编号: %s 已经在回收站中!无法再次删除!", file.getId()));
            }
        });

        return storageFiles;
    }

    /**
     * 批量处理文件删除操作
     */
    private void processFileDeletion(List<StorageFile> storageFiles, OperationService service, boolean forceDelete) {
        storageFiles.forEach(storageFile -> {
            // 构建文件操作DTO
            FileOperationDto fileOperationDto = FileOperationDto.builder()
                    .previewRelativePath(storageFile.getPreviewRelativePath())
                    .originalRelativePath(storageFile.getOriginalRelativePath())
                    .build();
            if (forceDelete) {
                //这边是强制删除
                service.delete(fileOperationDto, true);
                //标记为已删除
                storageFile.setIsDeleted(Constants.LogicDelete.DELETED);
            } else {
                FileOperationDto fileOperationDtoResult = service.delete(fileOperationDto, false);
                storageFile.setOriginalTrashPath(fileOperationDtoResult.getOriginalTrashPath());
                storageFile.setIsTrash(StorageConstants.dataVerifyConstants.IN_TRASH);
                if (StringUtils.isNotBlank(fileOperationDtoResult.getPreviewTrashPath())) {
                    storageFile.setPreviewTrashPath(fileOperationDtoResult.getPreviewTrashPath());
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
            throw new ParamException(ResultCode.PARAM_NOT_NULL, "文件ID不能为空");
        }
        LambdaQueryWrapper<StorageFile> in = new LambdaQueryWrapper<StorageFile>().in(StorageFile::getId, fileIds);
        List<StorageFile> StorageFiles = list(in);
        if (CollectionUtils.isEmpty(StorageFiles)) {
            throw new ServiceException(ResultCode.DATA_NOT_FOUND, "文件不存在");
        }
        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();

        // 校验文件存储类型一致性和判断是否在回收站中
        StorageFiles.forEach(StorageFile -> {
            validateStorageConsistency(activeStorageType, StorageFile);
            if (!StorageConstants.dataVerifyConstants.IN_TRASH.equals(StorageFile.getIsTrash())) {
                throw new ServiceException(ResultCode.FILE_OPERATION_FAILED, "文件未处于回收站中或已被删除，无法恢复");
            }
        });

        OperationService service = getService(activeStorageType);
        //进行文件操作
        StorageFiles.forEach(StorageFile -> {
            FileOperationDto fileOperationDto = new FileOperationDto();
            BeanUtils.copyProperties(StorageFile, fileOperationDto);
            boolean restore = service.restore(fileOperationDto);
            //恢复成功后将数据库中文件状态改为正常
            if (restore) {
                StorageFile.setIsTrash(StorageConstants.dataVerifyConstants.NOT_IN_TRASH);
                StorageFile.setIsDeleted(Constants.LogicDelete.NOT_DELETED);
                // 清空回收站路径信息，恢复原始路径信息
                StorageFile.setOriginalTrashPath(null);
                StorageFile.setPreviewTrashPath(null);
                StorageFile.setUpdateTime(new Date());
                updateById(StorageFile);
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
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTrashFileById(List<Long> fileIds) {
        if (CollectionUtils.isEmpty(fileIds)) {
            throw new ServiceException("文件ID不能为空");
        }

        // 查询文件记录
        LambdaQueryWrapper<StorageFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(StorageFile::getId, fileIds);
        List<StorageFile> recordList = list(queryWrapper);

        if (CollectionUtils.isEmpty(recordList)) {
            throw new ServiceException(ResultCode.RESULT_IS_NULL, "未找到指定的文件记录");
        }

        // 校验文件存储类型一致性，并检查文件是否确实在回收站中
        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();
        recordList.forEach(storageFile -> {
            validateStorageConsistency(activeStorageType, storageFile);

            // 确保文件处于回收站状态
            if (!StorageConstants.dataVerifyConstants.IN_TRASH.equals(storageFile.getIsTrash())) {
                throw new ServiceException(
                        ResultCode.OPERATION_ERROR,
                        String.format("文件 %s 不在回收站中，无法执行删除操作", storageFile.getOriginalName())
                );
            }
        });

        // 获取当前存储服务
        OperationService service = getService(activeStorageType);
        // 对每个文件执行物理删除并更新数据库状态
        recordList.forEach(StorageFile -> {
            // 构造文件操作DTO，包含回收站路径信息
            FileOperationDto fileOperationDto = FileOperationDto.builder()
                    .originalTrashPath(StorageFile.getOriginalTrashPath())
                    .previewTrashPath(StorageFile.getPreviewTrashPath())
                    .build();

            // 调用底层服务执行物理删除
            service.deleteTrashFile(fileOperationDto);
        });
        //删除数据库记录
        removeBatchByIds(recordList);

        // 批量更新文件状态
        return updateBatchById(recordList);
    }

    /**
     * 根据文件ID获取文件信息
     *
     * @param id 文件ID
     * @return 文件信息
     */
    @Override
    public StorageFile getFileById(Long id) {
        return getById(id);
    }

    /**
     * 导出文件列表
     *
     * @param request 查询参数
     * @return 文件列表
     */
    @Override
    public List<StorageFile> exportListFile(StorageFileQueryRequest request) {
        return storageFileMapper.exportListFile(request);
    }

    /**
     * 删除文件记录
     *
     * @param ids 文件ID列表
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFileRecordById(List<Long> ids) {
        return removeByIds(ids);
    }


    /**
     * 根据存储类型获取对应的存储服务
     *
     * @param type 存储类型
     * @return 存储服务
     */
    private OperationService getService(String type) {
        String beanName = getBeanNameByType(type);
        return SpringContextHolder.getBean(beanName, OperationService.class);
    }

    /**
     * 校验文件的存储和当前系统使用的存储是否一致
     *
     * @param activeStorageType 当前系统使用的存储类型
     * @param storageFile       文件记录
     */
    private void validateStorageConsistency(String activeStorageType, StorageFile storageFile) {
        if (!activeStorageType.equals(storageFile.getStorageType())) {
            throw new ServiceException(
                    ResultCode.OPERATION_ERROR,
                    String.format("存储类型不匹配，无法执行删除操作。当前存储类型: %s，当前文件ID为 %d 的实际存储类型: %s",
                            activeStorageType, storageFile.getId(), storageFile.getStorageType())
            );
        }
        // 非本地存储
        if (!StorageConstants.StorageType.LOCAL.equals(activeStorageType)) {
            if (!getCurrentBucketName().equals(storageFile.getBucketName())) {
                throw new ServiceException(ResultCode.OPERATION_ERROR,
                        String.format("存储桶名称不匹配，无法执行删除操作。当前存储桶名称: %s，当前文件ID为 %d 的实际存储桶名称: %s",
                                getCurrentBucketName(), storageFile.getId(), storageFile.getBucketName()));
            }
        }
    }

    /**
     * 获取当前存储桶名称
     *
     * @return 当前存储桶名称
     */
    private String getCurrentBucketName() {
        String currentStorageConfigJson = storageConfigRetrievalService.getCurrentStorageConfigJson();
        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();
        return switch (activeStorageType) {
            case StorageConstants.StorageType.MINIO -> {
                MinioStorageConfig minioStorageConfig = JSON.parseObject(currentStorageConfigJson, MinioStorageConfig.class);
                yield minioStorageConfig.getBucketName();
            }
            case StorageConstants.StorageType.ALIYUN_OSS -> {
                AliyunOssStorageConfig aliyunOssStorageConfig = JSON.parseObject(currentStorageConfigJson, AliyunOssStorageConfig.class);
                yield aliyunOssStorageConfig.getBucketName();
            }
            case StorageConstants.StorageType.TENCENT_COS -> {
                TencentCosStorageConfig tencentCosStorageConfig = JSON.parseObject(currentStorageConfigJson, TencentCosStorageConfig.class);
                yield tencentCosStorageConfig.getBucketName();
            }
            case StorageConstants.StorageType.AMAZON_S3 -> {
                AmazonS3StorageConfig amazonS3StorageConfig = JSON.parseObject(currentStorageConfigJson, AmazonS3StorageConfig.class);
                yield amazonS3StorageConfig.getBucketName();
            }
            default -> throw new ServiceException("未知存储类型: " + activeStorageType);
        };
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
            case StorageConstants.StorageType.AMAZON_S3 -> StorageConstants.springBeanName.AMAZON_S3_STORAGE_SERVICE;
            default -> throw new ServiceException("未知存储类型: " + type);
        };
    }
}
