package cn.zhangchuangla.storage.component;

import cn.zhangchuangla.common.constant.StorageConstants;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.model.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.utils.StorageUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CopyObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

/**
 * 阿里云OSS存储工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/3 10:00
 */
@Slf4j
@Component
public class AliyunOSSStorageHandler {

    /**
     * 上传文件到阿里云OSS
     * 如果检测到是图片类型，会自动调用图片上传方法
     *
     * @param fileTransferDto 文件传输对象
     * @param aliyunOSSConfig 阿里云OSS配置
     * @return 文件传输对象
     */
    public static FileTransferDto uploadFile(FileTransferDto fileTransferDto, AliyunOSSConfigEntity aliyunOSSConfig) {
        StorageUtils.validateUploadParams(fileTransferDto, aliyunOSSConfig);

        // 填充文件基础信息
        StorageUtils.fillFileTransferInfo(fileTransferDto, StorageConstants.ALIYUN_OSS,
                aliyunOSSConfig.getBucketName());

        // 如果是图片类型，则调用图片上传方法
        if (StorageUtils.isImage(fileTransferDto)) {
            return imageUpload(fileTransferDto, aliyunOSSConfig);
        }

        String fileName = fileTransferDto.getOriginalName();
        byte[] data = fileTransferDto.getBytes();

        // 创建OSS客户端
        OSS ossClient = createOSSClient(aliyunOSSConfig);

        try {
            // 生成存储路径
            String objectName = StorageUtils.generateFilePath(fileName);

            // 上传文件
            uploadToOSS(ossClient, aliyunOSSConfig.getBucketName(), objectName, data);

            // 构建文件URL
            String fileUrl = StorageUtils.buildFullUrl(aliyunOSSConfig.getFileDomain(), objectName);

            return StorageUtils.createEnhancedFileTransferResponse(fileUrl, objectName, null, null, fileTransferDto);
        } catch (Exception e) {
            log.warn("文件上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed, "文件上传失败！" + e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 上传图片到阿里云OSS
     * 同时上传原图和压缩图，分别保存在不同目录
     *
     * @param fileTransferDto 文件传输对象
     * @param aliyunOSSConfig 阿里云OSS配置
     * @return 增强后的文件传输对象
     */
    public static FileTransferDto imageUpload(FileTransferDto fileTransferDto, AliyunOSSConfigEntity aliyunOSSConfig) {
        StorageUtils.validateUploadParams(fileTransferDto, aliyunOSSConfig);

        // 填充文件基础信息
        StorageUtils.fillFileTransferInfo(fileTransferDto, StorageConstants.ALIYUN_OSS,
                aliyunOSSConfig.getBucketName());

        // 验证是否为图片类型
        if (!StorageUtils.isImage(fileTransferDto)) {
            throw new FileException(ResponseCode.FileUploadFailed, "非图片类型文件不能使用图片上传接口！");
        }

        String fileName = fileTransferDto.getOriginalName();
        byte[] originalData = fileTransferDto.getBytes();

        // 创建OSS客户端
        OSS ossClient = createOSSClient(aliyunOSSConfig);

        try {
            // 生成存储路径
            String originalObjectName = StorageUtils.generateOriginalImagePath(fileName);
            String compressedObjectName = StorageUtils.generateCompressedImagePath(fileName);

            // 上传原图
            uploadToOSS(ossClient, aliyunOSSConfig.getBucketName(), originalObjectName, originalData);
            String originalFileUrl = StorageUtils.buildFullUrl(aliyunOSSConfig.getFileDomain(), originalObjectName);

            // 压缩图片并上传
            byte[] compressedData = StorageUtils.compressImage(originalData);
            uploadToOSS(ossClient, aliyunOSSConfig.getBucketName(), compressedObjectName, compressedData);
            String compressedFileUrl = StorageUtils.buildFullUrl(aliyunOSSConfig.getFileDomain(), compressedObjectName);

            return StorageUtils.createEnhancedFileTransferResponse(
                    originalFileUrl, originalObjectName,
                    compressedFileUrl, compressedObjectName,
                    fileTransferDto);
        } catch (Exception e) {
            log.warn("图片上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed, "图片上传失败！" + e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 删除阿里云OSS上的文件
     *
     * @param fileTransferDto 文件传输对象
     * @param aliyunOSSConfig 阿里云OSS配置
     * @param enableTrash     是否启用回收站
     * @return 删除操作的结果
     */
    public static boolean removeFile(FileTransferDto fileTransferDto, AliyunOSSConfigEntity aliyunOSSConfig,
                                     boolean enableTrash) {
        StorageUtils.validateRemoveParams(fileTransferDto, aliyunOSSConfig);

        // 原文件信息
        String originalObjectName = fileTransferDto.getOriginalRelativePath();
        String bucketName = aliyunOSSConfig.getBucketName();
        String originalFileName = StorageUtils.getFileNameByRelativePath(originalObjectName);

        // 预览图信息（可能不存在）
        String previewObjectName = fileTransferDto.getPreviewImagePath();
        boolean hasPreviewImage = StringUtils.hasText(previewObjectName);

        // 记录操作类型
        StorageUtils.logFileOperationType("阿里云OSS", originalObjectName, previewObjectName, hasPreviewImage,
                enableTrash);

        OSS ossClient = null;
        try {
            // 创建OSS客户端
            ossClient = createOSSClient(aliyunOSSConfig);

            // 检查原始文件是否存在
            boolean originalExists = checkObjectExists(ossClient, bucketName, originalObjectName);
            if (!originalExists) {
                log.warn("原始文件不存在: {}/{}", bucketName, originalObjectName);
                // 原始文件不存在，但可以继续处理预览图
            }

            if (enableTrash) {
                // 使用回收站模式：先复制文件到回收站目录，再删除原文件

                // 1. 处理原始文件
                if (originalExists) {
                    String originalTrashObjectName = StorageUtils.generateTrashPath(originalFileName,
                            StorageConstants.FILE_ORIGINAL_FOLDER);
                    moveObjectToTrash(ossClient, bucketName, originalObjectName, originalTrashObjectName);
                    fileTransferDto.setOriginalTrashPath(originalTrashObjectName);
                    log.info("已将原始文件移至回收站: {}", originalTrashObjectName);
                }

                // 2. 处理预览图（如果存在）
                if (hasPreviewImage) {
                    boolean previewExists = checkObjectExists(ossClient, bucketName, previewObjectName);
                    if (!previewExists) {
                        log.warn("预览图文件不存在: {}/{}", bucketName, previewObjectName);
                    } else {
                        String previewFileName = StorageUtils.getFileNameByRelativePath(previewObjectName);
                        String previewTrashObjectName = StorageUtils.generateTrashPath(previewFileName,
                                StorageConstants.FILE_PREVIEW_FOLDER);

                        moveObjectToTrash(ossClient, bucketName, previewObjectName, previewTrashObjectName);
                        fileTransferDto.setPreviewTrashPath(previewTrashObjectName);
                        log.info("已将预览图文件移至回收站: {}", previewTrashObjectName);
                    }
                }
            } else {
                // 直接删除模式

                // 删除原始文件
                if (originalExists) {
                    ossClient.deleteObject(bucketName, originalObjectName);
                    log.info("已永久删除文件: {}/{}", bucketName, originalObjectName);
                }

                // 删除预览图（如果存在）
                if (hasPreviewImage) {
                    boolean previewExists = checkObjectExists(ossClient, bucketName, previewObjectName);
                    if (previewExists) {
                        ossClient.deleteObject(bucketName, previewObjectName);
                        log.info("已永久删除预览图: {}/{}", bucketName, previewObjectName);
                    }
                }
            }

            return true;
        } catch (Exception e) {
            log.error("阿里云OSS文件删除失败: {}", e.getMessage(), e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件删除失败: " + e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 从阿里云OSS回收站恢复文件
     *
     * @param fileTransferDto 文件传输对象
     * @param aliyunOSSConfig 阿里云OSS配置
     * @return 恢复操作的结果
     */
    public static boolean recoverFile(FileTransferDto fileTransferDto, AliyunOSSConfigEntity aliyunOSSConfig) {
        StorageUtils.validateRecoveryParams(fileTransferDto, aliyunOSSConfig);

        String bucketName = aliyunOSSConfig.getBucketName();
        String originalObjectName = fileTransferDto.getOriginalRelativePath();
        String originalTrashPath = fileTransferDto.getOriginalTrashPath();

        // 预览图信息（可能不存在）
        String previewObjectName = fileTransferDto.getPreviewImagePath();
        String previewTrashPath = fileTransferDto.getPreviewTrashPath();
        boolean hasPreviewImage = StringUtils.hasText(previewObjectName) && StringUtils.hasText(previewTrashPath);

        OSS ossClient = null;
        boolean success = true;
        boolean hasError = false;

        try {
            ossClient = createOSSClient(aliyunOSSConfig);

            // 检查回收站中的文件是否存在
            boolean originalTrashExists = checkObjectExists(ossClient, bucketName, originalTrashPath);
            if (!originalTrashExists) {
                log.warn("回收站中的原始文件不存在: {}/{}", bucketName, originalTrashPath);
                hasError = true;
                success = false;
            } else {
                try {
                    // 1. 恢复原始文件
                    moveObject(ossClient, bucketName, originalTrashPath, bucketName, originalObjectName);
                    log.info("已从回收站恢复文件: {} -> {}", originalTrashPath, originalObjectName);
                } catch (Exception e) {
                    log.error("恢复原始文件失败: {} -> {}, 错误: {}", originalTrashPath, originalObjectName, e.getMessage());
                    throw e; // 原始文件恢复失败，直接抛出异常
                }
            }

            // 2. 恢复预览图（如果存在）
            if (hasPreviewImage) {
                boolean previewTrashExists = checkObjectExists(ossClient, bucketName, previewTrashPath);
                if (!previewTrashExists) {
                    log.warn("回收站中的预览图文件不存在: {}/{}", bucketName, previewTrashPath);
                    // 预览图不存在不影响整体恢复成功状态，因为它是可选的
                } else {
                    try {
                        moveObject(ossClient, bucketName, previewTrashPath, bucketName, previewObjectName);
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
            log.error("阿里云OSS文件恢复失败: {}", e.getMessage(), e);
            throw new FileException(ResponseCode.FILE_OPERATION_ERROR, "文件恢复失败: " + e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 检查对象是否存在于阿里云OSS中
     */
    private static boolean checkObjectExists(OSS ossClient, String bucketName, String objectName) {
        return StorageUtils.checkObjectExistsWithLogging(bucketName, objectName,
                () -> ossClient.doesObjectExist(bucketName, objectName));
    }

    /**
     * 移动对象到回收站
     */
    private static void moveObjectToTrash(OSS ossClient, String bucketName, String sourceObjectName,
                                          String trashObjectName) {
        moveObject(ossClient, bucketName, sourceObjectName, bucketName, trashObjectName);
    }

    /**
     * 移动对象（先复制，后删除）
     */
    private static void moveObject(OSS ossClient, String sourceBucket, String sourceObject, String targetBucket,
                                   String targetObject) {
        try {
            // 1. 复制对象
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest(
                    sourceBucket, sourceObject, targetBucket, targetObject);
            ossClient.copyObject(copyObjectRequest);

            // 2. 删除源对象
            ossClient.deleteObject(sourceBucket, sourceObject);
        } catch (Exception e) {
            log.error("移动对象失败: {} -> {}, 错误: {}", sourceObject, targetObject, e.getMessage(), e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED,
                    String.format("移动对象失败 [%s -> %s]: %s", sourceObject, targetObject, e.getMessage()));
        }
    }

    /**
     * 创建OSS客户端
     */
    private static OSS createOSSClient(AliyunOSSConfigEntity aliyunOSSConfig) {
        if (aliyunOSSConfig == null) {
            throw new FileException(ResponseCode.FileUploadFailed, "阿里云OSS配置不能为空！");
        }

        String endPoint = aliyunOSSConfig.getEndpoint();
        String accessKeyId = aliyunOSSConfig.getAccessKeyId();
        String accessKeySecret = aliyunOSSConfig.getAccessKeySecret();

        return new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);
    }

    /**
     * 上传文件到OSS
     */
    private static void uploadToOSS(OSS ossClient, String bucketName, String objectName,
                                    byte[] data) {
        // 设置元数据
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(data.length);
        metadata.setHeader("Content-Disposition", "inline");
        metadata.setContentType(StorageUtils.generateFileContentType(data));

        // 上传文件
        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(data), metadata);
    }
}
