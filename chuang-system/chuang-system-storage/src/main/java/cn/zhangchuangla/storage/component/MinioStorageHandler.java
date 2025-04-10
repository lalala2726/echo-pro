package cn.zhangchuangla.storage.component;

import cn.zhangchuangla.common.constant.StorageConstants;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.model.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.utils.StorageUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

/**
 * Minio存储组件
 * 由Spring容器管理的组件，替代原工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/3 10:00
 */
@Slf4j
@Component
public class MinioStorageHandler {

    /**
     * 上传文件到Minio
     * 如果检测到是图片类型，会自动调用图片上传方法
     *
     * @param fileTransferDto   文件传输对象
     * @param minioConfigEntity Minio配置
     * @return 文件传输对象
     */
    public FileTransferDto uploadFile(FileTransferDto fileTransferDto, MinioConfigEntity minioConfigEntity) {
        validateUploadParams(fileTransferDto, minioConfigEntity);

        // 填充文件基础信息
        StorageUtils.fillFileTransferInfo(fileTransferDto, StorageConstants.MINIO,
                minioConfigEntity.getBucketName());

        // 如果是图片类型，则调用图片上传方法
        if (StorageUtils.isImage(fileTransferDto)) {
            return imageUpload(fileTransferDto, minioConfigEntity);
        }

        String fileName = fileTransferDto.getOriginalName();
        byte[] data = fileTransferDto.getBytes();

        try {
            // 创建Minio客户端并确保存储桶存在
            MinioClient minioClient = createMinioClient(minioConfigEntity);
            ensureBucketExists(minioClient, minioConfigEntity.getBucketName());

            // 生成存储路径
            String objectName = StorageUtils.generateFilePath(fileName);

            // 上传文件
            uploadToMinio(minioClient, minioConfigEntity.getBucketName(), objectName, data);

            // 构建文件URL
            String fileUrl = "";
            if (!StringUtils.isEmpty(minioConfigEntity.getFileDomain())) {
                fileUrl = StorageUtils.buildFullUrl(minioConfigEntity.getFileDomain(), objectName);
            }

            return StorageUtils.createEnhancedFileTransferResponse(fileUrl, objectName, null, null,
                    fileTransferDto);
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new FileException(ResponseCode.FileUploadFailed, "文件上传失败！" + e.getMessage());
        }
    }

    /**
     * 上传图片到Minio
     * 同时上传原图和压缩图，分别保存在不同目录
     *
     * @param fileTransferDto   文件传输对象
     * @param minioConfigEntity Minio配置
     * @return 增强后的文件传输对象
     */
    public FileTransferDto imageUpload(FileTransferDto fileTransferDto, MinioConfigEntity minioConfigEntity) {
        validateUploadParams(fileTransferDto, minioConfigEntity);

        // 填充文件基础信息
        StorageUtils.fillFileTransferInfo(fileTransferDto, StorageConstants.MINIO,
                minioConfigEntity.getBucketName());

        // 验证是否为图片类型
        if (!StorageUtils.isImage(fileTransferDto)) {
            throw new FileException(ResponseCode.FileUploadFailed, "非图片类型文件不能使用图片上传接口！");
        }

        String fileName = fileTransferDto.getOriginalName();
        byte[] originalData = fileTransferDto.getBytes();

        try {
            // 创建Minio客户端并确保存储桶存在
            MinioClient minioClient = createMinioClient(minioConfigEntity);
            ensureBucketExists(minioClient, minioConfigEntity.getBucketName());

            // 生成存储路径
            String originalObjectName = StorageUtils.generateOriginalImagePath(fileName);
            String compressedObjectName = StorageUtils.generateCompressedImagePath(fileName);

            // 上传原图
            uploadToMinio(minioClient, minioConfigEntity.getBucketName(), originalObjectName, originalData);

            // 上传压缩图
            byte[] compressedData = StorageUtils.compressImage(originalData);
            uploadToMinio(minioClient, minioConfigEntity.getBucketName(), compressedObjectName, compressedData);

            // 构建URL
            String originalFileUrl = "";
            String compressedFileUrl = "";
            if (!StringUtils.isEmpty(minioConfigEntity.getFileDomain())) {
                originalFileUrl = StorageUtils.buildFullUrl(minioConfigEntity.getFileDomain(),
                        originalObjectName);
                compressedFileUrl = StorageUtils.buildFullUrl(minioConfigEntity.getFileDomain(),
                        compressedObjectName);
            }

            return StorageUtils.createEnhancedFileTransferResponse(
                    originalFileUrl, originalObjectName,
                    compressedFileUrl, compressedObjectName,
                    fileTransferDto);
        } catch (Exception e) {
            log.error("图片上传失败: {}", e.getMessage(), e);
            throw new FileException(ResponseCode.FileUploadFailed, "图片上传失败！" + e.getMessage());
        }
    }

    /**
     * 删除Minio文件
     *
     * @param fileTransferDto   文件传输对象
     * @param minioConfigEntity Minio配置
     * @param enableTrash       是否启用回收站
     * @return 操作结果
     */
    public boolean removeFile(FileTransferDto fileTransferDto, MinioConfigEntity minioConfigEntity,
                              boolean enableTrash) {
        StorageUtils.validateRemoveParams(fileTransferDto, minioConfigEntity);

        // 原文件信息
        String originalObjectName = fileTransferDto.getOriginalRelativePath();
        String bucketName = minioConfigEntity.getBucketName();
        String originalFileName = StorageUtils.getFileNameByRelativePath(originalObjectName);

        // 预览图信息（可能不存在）
        String previewObjectName = fileTransferDto.getPreviewImagePath();
        boolean hasPreviewImage = StringUtils.hasText(previewObjectName);

        // 记录操作类型
        StorageUtils.logFileOperationType("Minio", originalObjectName, previewObjectName, hasPreviewImage, enableTrash);

        try {
            // 创建Minio客户端
            MinioClient minioClient = createMinioClient(minioConfigEntity);

            // 检查对象是否存在
            boolean originalExists = checkObjectExists(minioClient, bucketName, originalObjectName);
            if (!originalExists) {
                log.warn("原始文件不存在: {}/{}", bucketName, originalObjectName);
                // 如果原始文件不存在，可以尝试处理预览图
            }

            if (enableTrash) {
                // 使用回收站模式：复制文件到回收站目录，然后删除原文件

                // 1. 处理原始文件
                if (originalExists) {
                    String originalTrashObjectName = generateTrashPath(originalFileName,
                            StorageConstants.FILE_ORIGINAL_FOLDER);
                    moveObjectToTrash(minioClient, bucketName, originalObjectName, originalTrashObjectName);
                    fileTransferDto.setOriginalTrashPath(originalTrashObjectName);
                    log.info("已将原始文件移至回收站: {}", originalTrashObjectName);
                }

                // 2. 处理预览图（如果存在）
                if (hasPreviewImage) {
                    boolean previewExists = checkObjectExists(minioClient, bucketName, previewObjectName);
                    if (!previewExists) {
                        log.warn("预览图文件不存在: {}/{}", bucketName, previewObjectName);
                    } else {
                        String previewFileName = StorageUtils.getFileNameByRelativePath(previewObjectName);
                        String previewTrashObjectName = generateTrashPath(previewFileName,
                                StorageConstants.FILE_PREVIEW_FOLDER);

                        moveObjectToTrash(minioClient, bucketName, previewObjectName, previewTrashObjectName);
                        fileTransferDto.setPreviewTrashPath(previewTrashObjectName);
                        log.info("已将预览图文件移至回收站: {}", previewTrashObjectName);
                    }
                }
            } else {
                // 直接删除模式

                // 删除原始文件
                if (originalExists) {
                    minioClient.removeObject(RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(originalObjectName)
                            .build());
                    log.info("已永久删除文件: {}/{}", bucketName, originalObjectName);
                }

                // 删除预览图（如果存在）
                if (hasPreviewImage) {
                    boolean previewExists = checkObjectExists(minioClient, bucketName, previewObjectName);
                    if (previewExists) {
                        minioClient.removeObject(RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(previewObjectName)
                                .build());
                        log.info("已永久删除预览图: {}/{}", bucketName, previewObjectName);
                    }
                }
            }

            return true;
        } catch (Exception e) {
            log.error("Minio文件删除失败: {}", e.getMessage(), e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件删除失败: " + e.getMessage());
        }
    }

    /**
     * 从回收站恢复文件
     *
     * @param fileTransferDto   文件传输对象
     * @param minioConfigEntity Minio配置
     * @return 恢复结果
     */
    public boolean recoverFile(FileTransferDto fileTransferDto, MinioConfigEntity minioConfigEntity) {
        StorageUtils.validateRecoveryParams(fileTransferDto, minioConfigEntity);

        String bucketName = minioConfigEntity.getBucketName();
        String originalObjectName = fileTransferDto.getOriginalRelativePath();
        String originalTrashPath = fileTransferDto.getOriginalTrashPath();

        // 预览图信息（可能不存在）
        String previewObjectName = fileTransferDto.getPreviewImagePath();
        String previewTrashPath = fileTransferDto.getPreviewTrashPath();
        boolean hasPreviewImage = StringUtils.hasText(previewObjectName) && StringUtils.hasText(previewTrashPath);

        boolean success = true;
        boolean hasError = false;

        try {
            MinioClient minioClient = createMinioClient(minioConfigEntity);

            // 检查回收站中的文件是否存在
            boolean originalTrashExists = checkObjectExists(minioClient, bucketName, originalTrashPath);
            if (!originalTrashExists) {
                log.warn("回收站中的原始文件不存在: {}/{}", bucketName, originalTrashPath);
                hasError = true;
                success = false;
            } else {
                try {
                    // 1. 恢复原始文件
                    moveObject(minioClient, bucketName, originalTrashPath, bucketName, originalObjectName);
                    log.info("已从回收站恢复文件: {} -> {}", originalTrashPath, originalObjectName);
                } catch (Exception e) {
                    log.error("恢复原始文件失败: {} -> {}, 错误: {}", originalTrashPath, originalObjectName, e.getMessage());
                    throw e; // 原始文件恢复失败，直接抛出异常
                }
            }

            // 2. 恢复预览图（如果存在）
            if (hasPreviewImage) {
                boolean previewTrashExists = checkObjectExists(minioClient, bucketName, previewTrashPath);
                if (!previewTrashExists) {
                    log.warn("回收站中的预览图文件不存在: {}/{}", bucketName, previewTrashPath);
                    // 预览图不存在不影响整体恢复成功状态，因为它是可选的
                } else {
                    try {
                        moveObject(minioClient, bucketName, previewTrashPath, bucketName, previewObjectName);
                        log.info("已从回收站恢复预览图: {} -> {}", previewTrashPath, previewObjectName);
                    } catch (Exception e) {
                        log.error("恢复预览图文件失败: {} -> {}, 错误: {}", previewTrashPath, previewObjectName, e.getMessage());
                        // 预览图恢复失败不应中断整个恢复过程，但应记录错误
                    }
                }
            }

            StorageUtils.handleRecoveryErrors(hasError);

            return success;
        } catch (Exception e) {
            log.error("Minio文件恢复失败: {}", e.getMessage(), e);
            throw new FileException(ResponseCode.FILE_OPERATION_ERROR, "文件恢复失败: " + e.getMessage());
        }
    }

    /**
     * 检查对象是否存在于Minio存储中
     */
    private boolean checkObjectExists(MinioClient minioClient, String bucketName, String objectName) {
        return StorageUtils.checkObjectExistsWithLogging(bucketName, objectName, () -> {
            try {
                minioClient.statObject(StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
                return true;
            } catch (ErrorResponseException e) {
                if (e.errorResponse().code().equals("NoSuchKey")) {
                    return false;
                }
                throw e;
            }
        });
    }

    /**
     * 移动对象到回收站（先复制，再删除）
     */
    private void moveObjectToTrash(MinioClient minioClient, String bucketName,
                                   String sourceObjectName, String trashObjectName) {
        moveObject(minioClient, bucketName, sourceObjectName, bucketName, trashObjectName);
    }

    /**
     * 移动对象（先复制，再删除）
     */
    private void moveObject(MinioClient minioClient, String sourceBucket, String sourceObject,
                            String targetBucket, String targetObject) {
        try {
            // 1. 复制对象
            minioClient.copyObject(CopyObjectArgs.builder()
                    .source(CopySource.builder().bucket(sourceBucket).object(sourceObject).build())
                    .bucket(targetBucket)
                    .object(targetObject)
                    .build());

            // 2. 删除源对象
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(sourceBucket)
                    .object(sourceObject)
                    .build());
        } catch (Exception e) {
            log.error("移动对象失败: {} -> {}, 错误: {}", sourceObject, targetObject, e.getMessage(), e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED,
                    String.format("移动对象失败 [%s -> %s]: %s", sourceObject, targetObject, e.getMessage()));
        }
    }

    /**
     * 生成回收站路径
     */
    private String generateTrashPath(String fileName, String subFolder) {
        return StorageUtils.generateTrashPath(fileName, subFolder);
    }

    /**
     * 验证上传参数
     */
    private void validateUploadParams(FileTransferDto fileTransferDto, MinioConfigEntity minioConfigEntity) {
        if (fileTransferDto == null || fileTransferDto.getBytes() == null
                || StringUtils.isEmpty(fileTransferDto.getOriginalName())) {
            throw new FileException(ResponseCode.FileUploadFailed, "文件数据或文件名不能为空！");
        }

        if (minioConfigEntity == null || StringUtils.isEmpty(minioConfigEntity.getEndpoint())
                || StringUtils.isEmpty(minioConfigEntity.getAccessKey())
                || StringUtils.isEmpty(minioConfigEntity.getSecretKey())
                || StringUtils.isEmpty(minioConfigEntity.getBucketName())) {
            throw new FileException(ResponseCode.FileUploadFailed, "Minio配置信息不完整！");
        }
    }

    /**
     * 创建Minio客户端
     */
    private MinioClient createMinioClient(MinioConfigEntity minioConfigEntity) {
        if (minioConfigEntity == null) {
            throw new FileException(ResponseCode.FileUploadFailed, "Minio配置不能为空！");
        }
        return MinioClient.builder()
                .endpoint(minioConfigEntity.getEndpoint())
                .credentials(minioConfigEntity.getAccessKey(), minioConfigEntity.getSecretKey())
                .build();
    }

    /**
     * 确保存储桶存在
     */
    private void ensureBucketExists(MinioClient minioClient, String bucketName) throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 上传文件到Minio
     */
    private void uploadToMinio(MinioClient minioClient, String bucketName, String objectName,
                               byte[] data) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .contentType(StorageUtils.generateFileContentType(data))
                        .stream(new ByteArrayInputStream(data), data.length, -1)
                        .build());
    }
}
