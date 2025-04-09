package cn.zhangchuangla.storage.component;

import cn.zhangchuangla.common.constant.StorageConstants;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.model.entity.file.TencentCOSConfigEntity;
import cn.zhangchuangla.common.utils.StorageUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.CopyObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 腾讯云COS存储工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/3 10:00
 */
@Slf4j
@Component
public class TencentCOSHandler {

    /**
     * 上传文件到腾讯云COS
     * 如果检测到是图片类型，会自动调用图片上传方法
     *
     * @param fileTransferDto        文件传输对象
     * @param tencentCOSConfigEntity 腾讯云COS配置
     * @return 文件传输对象
     */
    public static FileTransferDto uploadFile(FileTransferDto fileTransferDto,
                                             TencentCOSConfigEntity tencentCOSConfigEntity) {
        StorageUtils.validateUploadParams(fileTransferDto, tencentCOSConfigEntity);

        // 填充文件基础信息
        StorageUtils.fillFileTransferInfo(fileTransferDto, StorageConstants.TENCENT_COS,
                tencentCOSConfigEntity.getBucketName());

        // 如果是图片类型，则调用图片上传方法
        if (StorageUtils.isImage(fileTransferDto)) {
            return imageUpload(fileTransferDto, tencentCOSConfigEntity);
        }

        String fileName = fileTransferDto.getOriginalName();
        byte[] data = fileTransferDto.getBytes();

        // 创建COS客户端
        COSClient cosClient = createCOSClient(tencentCOSConfigEntity);
        try {
            ensureBucketExists(cosClient, tencentCOSConfigEntity.getBucketName());

            // 生成存储路径
            String objectName = StorageUtils.generateFilePath(fileName);

            // 上传文件
            uploadToCOS(cosClient, tencentCOSConfigEntity.getBucketName(), objectName, data);

            // 构建文件URL
            String fileUrl = StorageUtils.buildFullUrl(tencentCOSConfigEntity.getFileDomain(), objectName);

            return StorageUtils.createEnhancedFileTransferResponse(fileUrl, objectName, null, null, fileTransferDto);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed, "文件上传失败！" + e.getMessage());
        } finally {
            cosClient.shutdown();
        }
    }

    /**
     * 上传图片到腾讯云COS
     * 同时上传原图和压缩图，分别保存在不同目录
     *
     * @param fileTransferDto        文件传输对象
     * @param tencentCOSConfigEntity 腾讯云COS配置
     * @return 增强后的文件传输对象
     */
    public static FileTransferDto imageUpload(FileTransferDto fileTransferDto,
                                              TencentCOSConfigEntity tencentCOSConfigEntity) {
        StorageUtils.validateUploadParams(fileTransferDto, tencentCOSConfigEntity);

        // 填充文件基础信息
        StorageUtils.fillFileTransferInfo(fileTransferDto, StorageConstants.TENCENT_COS,
                tencentCOSConfigEntity.getBucketName());

        // 验证是否为图片类型
        if (!StorageUtils.isImage(fileTransferDto)) {
            throw new FileException(ResponseCode.FileUploadFailed, "非图片类型文件不能使用图片上传接口！");
        }

        String fileName = fileTransferDto.getOriginalName();
        byte[] originalData = fileTransferDto.getBytes();

        // 创建COS客户端
        COSClient cosClient = createCOSClient(tencentCOSConfigEntity);
        try {
            ensureBucketExists(cosClient, tencentCOSConfigEntity.getBucketName());

            // 生成存储路径
            String originalObjectName = StorageUtils.generateOriginalImagePath(fileName);
            String compressedObjectName = StorageUtils.generateCompressedImagePath(fileName);

            // 上传原图
            uploadToCOS(cosClient, tencentCOSConfigEntity.getBucketName(), originalObjectName, originalData);
            String originalFileUrl = StorageUtils.buildFullUrl(tencentCOSConfigEntity.getFileDomain(),
                    originalObjectName);

            // 压缩并上传缩略图
            byte[] compressedData = StorageUtils.compressImage(originalData);
            uploadToCOS(cosClient, tencentCOSConfigEntity.getBucketName(), compressedObjectName, compressedData);
            String compressedFileUrl = StorageUtils.buildFullUrl(tencentCOSConfigEntity.getFileDomain(),
                    compressedObjectName);

            return StorageUtils.createEnhancedFileTransferResponse(
                    originalFileUrl, originalObjectName,
                    compressedFileUrl, compressedObjectName,
                    fileTransferDto);
        } catch (Exception e) {
            log.error("图片上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed, "图片上传失败！" + e.getMessage());
        } finally {
            cosClient.shutdown();
        }
    }

    /**
     * 删除腾讯云COS上的文件
     *
     * @param fileTransferDto        文件传输对象
     * @param tencentCOSConfigEntity 腾讯云COS配置
     * @param enableTrash            是否启用回收站
     * @return 删除操作的结果
     */
    public static boolean removeFile(FileTransferDto fileTransferDto, TencentCOSConfigEntity tencentCOSConfigEntity,
                                     boolean enableTrash) {
        StorageUtils.validateRemoveParams(fileTransferDto, tencentCOSConfigEntity);

        // 原文件信息
        String originalObjectName = fileTransferDto.getOriginalRelativePath();
        String bucketName = tencentCOSConfigEntity.getBucketName();
        String originalFileName = StorageUtils.getFileNameByRelativePath(originalObjectName);

        // 预览图信息（可能不存在）
        String previewObjectName = fileTransferDto.getPreviewImagePath();
        boolean hasPreviewImage = StringUtils.hasText(previewObjectName);

        // 记录操作类型
        StorageUtils.logFileOperationType("腾讯云COS", originalObjectName, previewObjectName, hasPreviewImage,
                enableTrash);

        COSClient cosClient = null;
        try {
            // 创建COS客户端
            cosClient = createCOSClient(tencentCOSConfigEntity);

            // 检查原始文件是否存在
            boolean originalExists = checkObjectExists(cosClient, bucketName, originalObjectName);
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
                    moveObjectToTrash(cosClient, bucketName, originalObjectName, originalTrashObjectName);
                    fileTransferDto.setOriginalTrashPath(originalTrashObjectName);
                    log.info("已将原始文件移至回收站: {}", originalTrashObjectName);
                }

                // 2. 处理预览图（如果存在）
                if (hasPreviewImage) {
                    boolean previewExists = checkObjectExists(cosClient, bucketName, previewObjectName);
                    if (!previewExists) {
                        log.warn("预览图文件不存在: {}/{}", bucketName, previewObjectName);
                    } else {
                        String previewFileName = StorageUtils.getFileNameByRelativePath(previewObjectName);
                        String previewTrashObjectName = StorageUtils.generateTrashPath(previewFileName,
                                StorageConstants.FILE_PREVIEW_FOLDER);

                        moveObjectToTrash(cosClient, bucketName, previewObjectName, previewTrashObjectName);
                        fileTransferDto.setPreviewTrashPath(previewTrashObjectName);
                        log.info("已将预览图文件移至回收站: {}", previewTrashObjectName);
                    }
                }
            } else {
                // 直接删除模式

                // 删除原始文件
                if (originalExists) {
                    cosClient.deleteObject(bucketName, originalObjectName);
                    log.info("已永久删除文件: {}/{}", bucketName, originalObjectName);
                }

                // 删除预览图（如果存在）
                if (hasPreviewImage) {
                    boolean previewExists = checkObjectExists(cosClient, bucketName, previewObjectName);
                    if (previewExists) {
                        cosClient.deleteObject(bucketName, previewObjectName);
                        log.info("已永久删除预览图: {}/{}", bucketName, previewObjectName);
                    }
                }
            }

            return true;
        } catch (Exception e) {
            log.error("腾讯云COS文件删除失败: {}", e.getMessage(), e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件删除失败: " + e.getMessage());
        } finally {
            if (cosClient != null) {
                cosClient.shutdown();
            }
        }
    }

    /**
     * 从腾讯云COS回收站恢复文件
     *
     * @param fileTransferDto        文件传输对象
     * @param tencentCOSConfigEntity 腾讯云COS配置
     * @return 恢复操作的结果
     */
    public static boolean recoverFile(FileTransferDto fileTransferDto, TencentCOSConfigEntity tencentCOSConfigEntity)
            throws IOException {
        StorageUtils.validateRecoveryParams(fileTransferDto, tencentCOSConfigEntity);

        String bucketName = tencentCOSConfigEntity.getBucketName();
        String originalObjectName = fileTransferDto.getOriginalRelativePath();
        String originalTrashPath = fileTransferDto.getOriginalTrashPath();

        // 预览图信息（可能不存在）
        String previewObjectName = fileTransferDto.getPreviewImagePath();
        String previewTrashPath = fileTransferDto.getPreviewTrashPath();
        boolean hasPreviewImage = StringUtils.hasText(previewObjectName) && StringUtils.hasText(previewTrashPath);

        COSClient cosClient = null;
        boolean success = true;
        boolean hasError = false;

        try {
            cosClient = createCOSClient(tencentCOSConfigEntity);

            // 检查回收站中的文件是否存在
            boolean originalTrashExists = checkObjectExists(cosClient, bucketName, originalTrashPath);
            if (!originalTrashExists) {
                log.warn("回收站中的原始文件不存在: {}/{}", bucketName, originalTrashPath);
                hasError = true;
                success = false;
            } else {
                try {
                    // 1. 恢复原始文件
                    moveObject(cosClient, bucketName, originalTrashPath, bucketName, originalObjectName);
                    log.info("已从回收站恢复文件: {} -> {}", originalTrashPath, originalObjectName);
                } catch (Exception e) {
                    log.error("恢复原始文件失败: {} -> {}, 错误: {}", originalTrashPath, originalObjectName, e.getMessage());
                    throw e; // 原始文件恢复失败，直接抛出异常
                }
            }

            // 2. 恢复预览图（如果存在）
            if (hasPreviewImage) {
                boolean previewTrashExists = checkObjectExists(cosClient, bucketName, previewTrashPath);
                if (!previewTrashExists) {
                    log.warn("回收站中的预览图文件不存在: {}/{}", bucketName, previewTrashPath);
                    // 预览图不存在不影响整体恢复成功状态，因为它是可选的
                } else {
                    try {
                        moveObject(cosClient, bucketName, previewTrashPath, bucketName, previewObjectName);
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
            log.error("腾讯云COS文件恢复失败: {}", e.getMessage(), e);
            throw new FileException(ResponseCode.FILE_OPERATION_ERROR, "文件恢复失败: " + e.getMessage());
        } finally {
            if (cosClient != null) {
                cosClient.shutdown();
            }
        }
    }

    /**
     * 创建COS客户端
     */
    private static COSClient createCOSClient(TencentCOSConfigEntity tencentCOSConfigEntity) {
        if (tencentCOSConfigEntity == null) {
            throw new FileException(ResponseCode.FileUploadFailed, "腾讯云COS配置不能为空！");
        }

        COSCredentials cred = new BasicCOSCredentials(
                tencentCOSConfigEntity.getSecretId(),
                tencentCOSConfigEntity.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(tencentCOSConfigEntity.getRegion()));
        return new COSClient(cred, clientConfig);
    }

    /**
     * 确保Bucket存在
     */
    private static void ensureBucketExists(COSClient cosClient, String bucketName) {
        if (!cosClient.doesBucketExist(bucketName)) {
            log.warn("Bucket {} 不存在，系统将尝试创建", bucketName);
            cosClient.createBucket(bucketName);
        }
    }

    /**
     * 上传文件到COS
     */
    private static void uploadToCOS(COSClient cosClient, String bucketName, String objectName,
                                    byte[] data) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(data.length);
        metadata.setContentType(StorageUtils.generateFileContentType(data));

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName, objectName, new ByteArrayInputStream(data), metadata);
        cosClient.putObject(putObjectRequest);
    }

    /**
     * 检查对象是否存在于腾讯云COS中
     */
    private static boolean checkObjectExists(COSClient cosClient, String bucketName, String objectName) {
        return StorageUtils.checkObjectExistsWithLogging(bucketName, objectName, () -> {
            try {
                cosClient.getObjectMetadata(bucketName, objectName);
                return true;
            } catch (Exception e) {
                if (e.getMessage().contains("Not Found") || e.getMessage().contains("404")) {
                    return false;
                }
                throw e;
            }
        });
    }

    /**
     * 移动对象到回收站
     */
    private static void moveObjectToTrash(COSClient cosClient, String bucketName, String sourceObjectName,
                                          String trashObjectName) {
        moveObject(cosClient, bucketName, sourceObjectName, bucketName, trashObjectName);
    }

    /**
     * 移动对象（先复制，后删除）
     */
    private static void moveObject(COSClient cosClient, String sourceBucket, String sourceObject, String targetBucket,
                                   String targetObject) {
        try {
            // 1. 复制对象
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest(
                    sourceBucket, sourceObject, targetBucket, targetObject);
            cosClient.copyObject(copyObjectRequest);

            // 2. 删除源对象
            cosClient.deleteObject(sourceBucket, sourceObject);
        } catch (Exception e) {
            log.error("移动对象失败: {} -> {}, 错误: {}", sourceObject, targetObject, e.getMessage(), e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED,
                    String.format("移动对象失败 [%s -> %s]: %s", sourceObject, targetObject, e.getMessage()));
        }
    }

}
