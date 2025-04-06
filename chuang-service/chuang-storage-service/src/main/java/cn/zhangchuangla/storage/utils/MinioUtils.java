package cn.zhangchuangla.storage.utils;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.exception.ProfileException;
import cn.zhangchuangla.common.model.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.storage.dto.FileTransferDto;
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

    /**
     * 上传文件到Minio
     *
     * @param fileTransferDto   文件传输对象
     * @param minioConfigEntity Minio配置
     * @return 文件传输对象
     */
    public static FileTransferDto uploadFile(FileTransferDto fileTransferDto, MinioConfigEntity minioConfigEntity) {
        if (minioConfigEntity == null)
            throw new ProfileException("Minio配置文件为空！请你检查配置文件是否存在？");

        String fileName = fileTransferDto.getFileName();
        byte[] data = fileTransferDto.getBytes();

        // 从配置获取参数
        String endpoint = minioConfigEntity.getEndpoint();
        String accessKey = minioConfigEntity.getAccessKey();
        String secretKey = minioConfigEntity.getSecretKey();
        String bucketName = minioConfigEntity.getBucketName();
        String fileDomain = minioConfigEntity.getFileDomain();

        try {
            // 创建Minio客户端并确保存储桶存在
            MinioClient minioClient = createMinioClient(endpoint, accessKey, secretKey);
            ensureBucketExists(minioClient, bucketName);

            // 生成存储路径
            String objectName = generateFilePath(fileName);

            // 上传文件
            uploadToMinio(minioClient, bucketName, objectName, data, fileName);

            // 构建文件URL
            String fileUrl = "";
            if (!StringUtils.isEmpty(fileDomain)) {
                fileUrl = buildFullUrl(fileDomain, objectName);
            }

            return createFileTransferResponse(fileUrl, objectName, null, null);
        } catch (Exception e) {
            log.warn("文件上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed);
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
        if (minioConfigEntity == null)
            throw new ProfileException("Minio配置文件为空！请你检查配置文件是否存在？");

        String fileName = fileTransferDto.getFileName();
        byte[] originalData = fileTransferDto.getBytes();

        // 从配置获取参数
        String endpoint = minioConfigEntity.getEndpoint();
        String accessKey = minioConfigEntity.getAccessKey();
        String secretKey = minioConfigEntity.getSecretKey();
        String bucketName = minioConfigEntity.getBucketName();
        String fileDomain = minioConfigEntity.getFileDomain();

        try {
            // 创建Minio客户端并确保存储桶存在
            MinioClient minioClient = createMinioClient(endpoint, accessKey, secretKey);
            ensureBucketExists(minioClient, bucketName);

            // 生成存储路径
            String originalObjectName = generateOriginalImagePath(fileName);
            String compressedObjectName = generateCompressedImagePath(fileName);

            // 上传原图
            uploadToMinio(minioClient, bucketName, originalObjectName, originalData, fileName);

            // 上传压缩图
            byte[] compressedData = compressImage(originalData);
            uploadToMinio(minioClient, bucketName, compressedObjectName, compressedData, fileName);

            // 构建URL
            String originalFileUrl = "";
            String compressedFileUrl = "";
            if (!StringUtils.isEmpty(fileDomain)) {
                originalFileUrl = buildFullUrl(fileDomain, originalObjectName);
                compressedFileUrl = buildFullUrl(fileDomain, compressedObjectName);
            }

            return createFileTransferResponse(
                    originalFileUrl, originalObjectName,
                    compressedFileUrl, compressedObjectName);
        } catch (Exception e) {
            log.warn("图片上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed);
        }
    }

    /**
     * 创建Minio客户端
     */
    private static MinioClient createMinioClient(String endpoint, String accessKey, String secretKey) {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
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
