package cn.zhangchuangla.storage.utils;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.model.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

/**
 * Minio存储工具类
 *
 * @author Chuang
 *         <p>
 *         created on 2025/4/3 10:00
 */
@Slf4j
public class MinioUtils extends AbstractStorageUtils {

    private static final String STORAGE_TYPE = "MINIO";

    /**
     * 上传文件到Minio
     * 如果检测到是图片类型，会自动调用图片上传方法
     *
     * @param fileTransferDto   文件传输对象
     * @param minioConfigEntity Minio配置
     * @return 文件传输对象
     */
    public static FileTransferDto uploadFile(FileTransferDto fileTransferDto, MinioConfigEntity minioConfigEntity) {
        validateUploadParams(fileTransferDto, minioConfigEntity);

        // 填充文件基础信息
        fillFileTransferInfo(fileTransferDto, STORAGE_TYPE, minioConfigEntity.getBucketName());

        // 如果是图片类型，则调用图片上传方法
        if (isImage(fileTransferDto)) {
            return imageUpload(fileTransferDto, minioConfigEntity);
        }

        String fileName = fileTransferDto.getOriginalName();
        byte[] data = fileTransferDto.getBytes();

        try {
            // 创建Minio客户端并确保存储桶存在
            MinioClient minioClient = createMinioClient(minioConfigEntity);
            ensureBucketExists(minioClient, minioConfigEntity.getBucketName());

            // 生成存储路径
            String objectName = generateFilePath(fileName);

            // 上传文件
            uploadToMinio(minioClient, minioConfigEntity.getBucketName(), objectName, data, fileName);

            // 构建文件URL
            String fileUrl = "";
            if (!StringUtils.isEmpty(minioConfigEntity.getFileDomain())) {
                fileUrl = buildFullUrl(minioConfigEntity.getFileDomain(), objectName);
            }

            return createEnhancedFileTransferResponse(fileUrl, objectName, null, null, fileTransferDto);
        } catch (Exception e) {
            log.warn("文件上传失败", e);
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
    public static FileTransferDto imageUpload(FileTransferDto fileTransferDto, MinioConfigEntity minioConfigEntity) {
        validateUploadParams(fileTransferDto, minioConfigEntity);

        // 填充文件基础信息
        fillFileTransferInfo(fileTransferDto, STORAGE_TYPE, minioConfigEntity.getBucketName());

        // 验证是否为图片类型
        if (!isImage(fileTransferDto)) {
            throw new FileException(ResponseCode.FileUploadFailed, "非图片类型文件不能使用图片上传接口！");
        }

        String fileName = fileTransferDto.getOriginalName();
        byte[] originalData = fileTransferDto.getBytes();

        try {
            // 创建Minio客户端并确保存储桶存在
            MinioClient minioClient = createMinioClient(minioConfigEntity);
            ensureBucketExists(minioClient, minioConfigEntity.getBucketName());

            // 生成存储路径
            String originalObjectName = generateOriginalImagePath(fileName);
            String compressedObjectName = generateCompressedImagePath(fileName);

            // 上传原图
            uploadToMinio(minioClient, minioConfigEntity.getBucketName(), originalObjectName, originalData, fileName);

            // 上传压缩图
            byte[] compressedData = compressImage(originalData);
            uploadToMinio(minioClient, minioConfigEntity.getBucketName(), compressedObjectName, compressedData,
                    fileName);

            // 构建URL
            String originalFileUrl = "";
            String compressedFileUrl = "";
            if (!StringUtils.isEmpty(minioConfigEntity.getFileDomain())) {
                originalFileUrl = buildFullUrl(minioConfigEntity.getFileDomain(), originalObjectName);
                compressedFileUrl = buildFullUrl(minioConfigEntity.getFileDomain(), compressedObjectName);
            }

            return createEnhancedFileTransferResponse(
                    originalFileUrl, originalObjectName,
                    compressedFileUrl, compressedObjectName,
                    fileTransferDto);
        } catch (Exception e) {
            log.warn("图片上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed, "图片上传失败！" + e.getMessage());
        }
    }

    /**
     * 创建Minio客户端
     */
    private static MinioClient createMinioClient(MinioConfigEntity minioConfigEntity) {
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
    private static void ensureBucketExists(MinioClient minioClient, String bucketName) throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 上传文件到Minio
     */
    private static void uploadToMinio(MinioClient minioClient, String bucketName, String objectName,
                                      byte[] data, String fileName) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .contentType(FileUtils.generateFileContentType(fileName))
                        .stream(new ByteArrayInputStream(data), data.length, -1)
                        .build());
    }
}
