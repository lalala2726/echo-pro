package cn.zhangchuangla.system.storage.utils;

import cn.zhangchuangla.common.core.exception.FileException;
import cn.zhangchuangla.system.storage.model.entity.config.MinioStorageConfig;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * MinIO 操作工具类
 * 提供所有MinIO相关的基础操作方法
 *
 * @author Chuang
 */
@Slf4j
@Component
public class MinioOperationUtils {

    /**
     * 创建MinIO客户端
     *
     * @param config MinIO配置
     * @return MinioClient实例
     */
    public MinioClient createMinioClient(MinioStorageConfig config) {
        try {
            if (config.getEndpoint() == null || config.getEndpoint().isBlank() ||
                    config.getAccessKey() == null || config.getAccessKey().isBlank() ||
                    config.getSecretKey() == null || config.getSecretKey().isBlank()) {
                throw new FileException("MinIO配置项不完整，无法创建客户端");
            }

            MinioClient client = MinioClient.builder()
                    .endpoint(config.getEndpoint())
                    .credentials(config.getAccessKey(), config.getSecretKey())
                    .build();

            log.info("MinIO客户端创建成功，端点: {}, 桶名称: {}", config.getEndpoint(), config.getBucketName());
            return client;

        } catch (Exception e) {
            log.error("MinIO客户端创建失败", e);
            throw new FileException("MinIO客户端创建失败: " + e.getMessage());
        }
    }

    /**
     * 确保存储桶存在，如果不存在则创建
     *
     * @param client     MinioClient实例
     * @param bucketName 存储桶名称
     */
    public void ensureBucketExists(MinioClient client, String bucketName) {
        try {
            boolean bucketExists = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("MinIO存储桶创建成功: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("MinIO存储桶操作失败: {}", bucketName, e);
            throw new FileException("MinIO存储桶操作失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件到MinIO
     *
     * @param client      MinioClient实例
     * @param bucketName  存储桶名称
     * @param objectPath  对象路径
     * @param inputStream 文件输入流
     * @param size        文件大小
     * @param contentType 文件类型
     */
    public void uploadFile(MinioClient client, String bucketName, String objectPath,
                           InputStream inputStream, long size, String contentType) {
        try {
            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectPath)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );
            log.debug("文件上传成功到MinIO: {}/{}", bucketName, objectPath);
        } catch (Exception e) {
            log.error("MinIO文件上传失败: {}/{}", bucketName, objectPath, e);
            throw new FileException("MinIO文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传字节数组到MinIO
     *
     * @param client      MinioClient实例
     * @param bucketName  存储桶名称
     * @param objectPath  对象路径
     * @param data        字节数组数据
     * @param contentType 文件类型
     */
    public void uploadByteArray(MinioClient client, String bucketName, String objectPath,
                                byte[] data, String contentType) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            uploadFile(client, bucketName, objectPath, inputStream, data.length, contentType);
        } catch (IOException e) {
            log.error("MinIO字节数组上传失败: {}/{}", bucketName, objectPath, e);
            throw new FileException("MinIO字节数组上传失败: " + e.getMessage());
        }
    }

    /**
     * 从MinIO获取文件流
     *
     * @param client     MinioClient实例
     * @param bucketName 存储桶名称
     * @param objectPath 对象路径
     * @return 文件输入流
     */
    public InputStream getObjectStream(MinioClient client, String bucketName, String objectPath) {
        try {
            return client.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectPath)
                            .build()
            );
        } catch (Exception e) {
            log.error("MinIO获取文件流失败: {}/{}", bucketName, objectPath, e);
            throw new FileException("MinIO获取文件流失败: " + e.getMessage());
        }
    }

    /**
     * 检查对象是否存在
     *
     * @param client     MinioClient实例
     * @param bucketName 存储桶名称
     * @param objectPath 对象路径
     * @return true: 存在，false: 不存在
     */
    public boolean objectExists(MinioClient client, String bucketName, String objectPath) {
        try {
            client.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectPath).build());
            return true;
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                return false;
            }
            log.error("MinIO检查对象存在性失败: {}/{}", bucketName, objectPath, e);
            throw new FileException("MinIO检查对象存在性失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("MinIO检查对象存在性失败: {}/{}", bucketName, objectPath, e);
            throw new FileException("MinIO检查对象存在性失败: " + e.getMessage());
        }
    }

    /**
     * 复制对象
     *
     * @param client     MinioClient实例
     * @param bucketName 存储桶名称
     * @param sourcePath 源路径
     * @param targetPath 目标路径
     */
    public void copyObject(MinioClient client, String bucketName, String sourcePath, String targetPath) {
        try {
            client.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucketName)
                            .object(targetPath)
                            .source(CopySource.builder().bucket(bucketName).object(sourcePath).build())
                            .build()
            );
            log.debug("MinIO对象复制成功: {} -> {}", sourcePath, targetPath);
        } catch (Exception e) {
            log.error("MinIO对象复制失败: {} -> {}", sourcePath, targetPath, e);
            throw new FileException("MinIO对象复制失败: " + e.getMessage());
        }
    }

    /**
     * 删除对象
     *
     * @param client     MinioClient实例
     * @param bucketName 存储桶名称
     * @param objectPath 对象路径
     */
    public void deleteObject(MinioClient client, String bucketName, String objectPath) {
        try {
            client.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectPath)
                            .build()
            );
            log.debug("MinIO对象删除成功: {}", objectPath);
        } catch (Exception e) {
            log.error("MinIO对象删除失败: {}", objectPath, e);
            throw new FileException("MinIO对象删除失败: " + e.getMessage());
        }
    }

    /**
     * 移动对象（复制后删除原对象）
     *
     * @param client     MinioClient实例
     * @param bucketName 存储桶名称
     * @param sourcePath 源路径
     * @param targetPath 目标路径
     */
    public void moveObject(MinioClient client, String bucketName, String sourcePath, String targetPath) {
        try {
            // 先复制
            copyObject(client, bucketName, sourcePath, targetPath);
            // 再删除原文件
            deleteObject(client, bucketName, sourcePath);
            log.debug("MinIO对象移动成功: {} -> {}", sourcePath, targetPath);
        } catch (Exception e) {
            log.error("MinIO对象移动失败: {} -> {}", sourcePath, targetPath, e);
            throw new FileException("MinIO对象移动失败: " + e.getMessage());
        }
    }

    /**
     * 获取对象信息
     *
     * @param client     MinioClient实例
     * @param bucketName 存储桶名称
     * @param objectPath 对象路径
     * @return 对象统计信息
     */
    public StatObjectResponse getObjectStat(MinioClient client, String bucketName, String objectPath) {
        try {
            return client.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectPath).build());
        } catch (Exception e) {
            log.error("MinIO获取对象信息失败: {}/{}", bucketName, objectPath, e);
            throw new FileException("MinIO获取对象信息失败: " + e.getMessage());
        }
    }
}
