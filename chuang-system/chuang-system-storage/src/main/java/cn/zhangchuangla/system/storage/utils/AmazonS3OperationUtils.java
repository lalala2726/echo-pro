package cn.zhangchuangla.system.storage.utils;

import cn.zhangchuangla.common.core.exception.FileException;
import cn.zhangchuangla.system.storage.model.entity.config.AmazonS3StorageConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * 亚马逊S3操作工具类
 * <p>
 * 提供亚马逊S3相关的基础操作方法，包括：
 * <ul>
 *   <li>S3客户端管理</li>
 *   <li>存储桶（Bucket）操作</li>
 *   <li>对象（Object）上传、下载、删除</li>
 *   <li>对象复制、移动等高级操作</li>
 *   <li>对象元数据获取</li>
 * </ul>
 *
 * <p>工具类设计原则：</p>
 * <ul>
 *   <li>职责单一：专注于亚马逊S3的基础操作</li>
 *   <li>无状态：所有方法都是静态的或无状态的</li>
 *   <li>异常统一：所有异常都包装为FileException</li>
 *   <li>日志完整：记录所有关键操作的执行情况</li>
 *   <li>兼容性：支持标准S3协议和兼容S3的存储服务</li>
 * </ul>
 *
 * @author Chuang
 */
@Slf4j
@Component
public class AmazonS3OperationUtils {

    /**
     * 创建亚马逊S3客户端
     * <p>
     * 根据提供的配置信息创建S3客户端实例。
     * 该方法会验证配置的完整性，确保所有必需的配置项都已设置。
     * 支持标准S3服务和兼容S3协议的其他存储服务。
     *
     * @param config 亚马逊S3配置，必须包含endpoint、accessKey、secretKey、region
     * @return S3客户端实例，用于后续的S3操作
     * @throws FileException 当配置不完整或客户端创建失败时抛出
     */
    public S3Client createS3Client(AmazonS3StorageConfig config) {
        try {
            if (config.getAccessKey() == null || config.getAccessKey().isBlank() ||
                    config.getSecretKey() == null || config.getSecretKey().isBlank() ||
                    config.getRegion() == null || config.getRegion().isBlank()) {
                throw new FileException("亚马逊S3配置项不完整，无法创建客户端");
            }

            // 创建认证凭据
            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
                    config.getAccessKey(),
                    config.getSecretKey()
            );

            // 构建S3客户端
            S3ClientBuilder builder = S3Client.builder()
                    .region(Region.of(config.getRegion()))
                    .credentialsProvider(StaticCredentialsProvider.create(awsCredentials));

            // 如果指定了自定义端点，则使用自定义端点（用于兼容S3的服务）
            if (config.getEndpoint() != null && !config.getEndpoint().isBlank()) {
                builder.endpointOverride(URI.create(config.getEndpoint()));
            }

            S3Client s3Client = builder.build();

            log.info("亚马逊S3客户端创建成功，端点: {}, 区域: {}, 存储桶: {}",
                    config.getEndpoint() != null ? config.getEndpoint() : "默认",
                    config.getRegion(),
                    config.getBucketName());
            return s3Client;

        } catch (Exception e) {
            log.error("亚马逊S3客户端创建失败", e);
            throw new FileException("亚马逊S3客户端创建失败: " + e.getMessage());
        }
    }

    /**
     * 确保存储桶存在，如果不存在则创建
     * <p>
     * 检查指定的存储桶是否存在，如果不存在则自动创建。
     * 新创建的存储桶将设置为标准存储类型和私有访问权限。
     *
     * @param s3Client   S3客户端实例，不能为null
     * @param bucketName 存储桶名称，不能为空
     * @throws FileException 当存储桶操作失败时抛出
     */
    public void ensureBucketExists(S3Client s3Client, String bucketName) {
        try {
            // 检查存储桶是否存在
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            try {
                s3Client.headBucket(headBucketRequest);
                log.debug("亚马逊S3存储桶已存在: {}", bucketName);
            } catch (NoSuchBucketException e) {
                // 存储桶不存在，创建它
                CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .build();

                s3Client.createBucket(createBucketRequest);
                log.info("亚马逊S3存储桶创建成功: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("亚马逊S3存储桶操作失败: {}", bucketName, e);
            throw new FileException("亚马逊S3存储桶操作失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件到亚马逊S3
     * <p>
     * 将输入流中的数据上传到指定的S3路径。
     * 支持设置文件的内容类型和长度。
     *
     * @param s3Client    S3客户端实例，不能为null
     * @param bucketName  存储桶名称，不能为空
     * @param objectKey   对象键（文件在S3中的路径），不能为空
     * @param inputStream 文件输入流，不能为null
     * @param size        文件大小（字节数）
     * @param contentType 文件MIME类型，如"image/jpeg"、"application/pdf"等
     * @throws FileException 当文件上传失败时抛出
     */
    public void uploadFile(S3Client s3Client, String bucketName, String objectKey,
                           InputStream inputStream, long size, String contentType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(contentType)
                    .contentLength(size)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, size));

            log.debug("文件上传成功到亚马逊S3: {}/{}", bucketName, objectKey);
        } catch (Exception e) {
            log.error("亚马逊S3文件上传失败: {}/{}", bucketName, objectKey, e);
            throw new FileException("亚马逊S3文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传字节数组到亚马逊S3
     * <p>
     * 将字节数组数据上传到指定的S3路径。
     * 适用于已在内存中处理好的文件数据，如压缩后的图片等。
     *
     * @param s3Client    S3客户端实例，不能为null
     * @param bucketName  存储桶名称，不能为空
     * @param objectKey   对象键（文件在S3中的路径），不能为空
     * @param data        字节数组数据，不能为null
     * @param contentType 文件MIME类型，如"image/jpeg"等
     * @throws FileException 当上传失败时抛出
     */
    public void uploadByteArray(S3Client s3Client, String bucketName, String objectKey,
                                byte[] data, String contentType) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            uploadFile(s3Client, bucketName, objectKey, inputStream, data.length, contentType);
        } catch (IOException e) {
            log.error("亚马逊S3字节数组上传失败: {}/{}", bucketName, objectKey, e);
            throw new FileException("亚马逊S3字节数组上传失败: " + e.getMessage());
        }
    }

    /**
     * 从亚马逊S3获取文件流
     * <p>
     * 获取指定对象的输入流，用于读取文件内容。
     * 调用方负责关闭返回的输入流。
     *
     * @param s3Client   S3客户端实例，不能为null
     * @param bucketName 存储桶名称，不能为空
     * @param objectKey  对象键（文件在S3中的路径），不能为空
     * @return 文件输入流，调用方需要负责关闭
     * @throws FileException 当获取文件流失败时抛出
     */
    public InputStream getObjectStream(S3Client s3Client, String bucketName, String objectKey) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            return s3Client.getObject(getObjectRequest);
        } catch (Exception e) {
            log.error("亚马逊S3获取文件流失败: {}/{}", bucketName, objectKey, e);
            throw new FileException("亚马逊S3获取文件流失败: " + e.getMessage());
        }
    }

    /**
     * 检查对象是否存在
     * <p>
     * 检查指定的对象在S3中是否存在。
     *
     * @param s3Client   S3客户端实例，不能为null
     * @param bucketName 存储桶名称，不能为空
     * @param objectKey  对象键（文件在S3中的路径），不能为空
     * @return true表示对象存在，false表示不存在
     * @throws FileException 当检查操作失败时抛出
     */
    public boolean objectExists(S3Client s3Client, String bucketName, String objectKey) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            log.error("亚马逊S3检查对象存在性失败: {}/{}", bucketName, objectKey, e);
            throw new FileException("亚马逊S3检查对象存在性失败: " + e.getMessage());
        }
    }

    /**
     * 复制对象
     * <p>
     * 在同一个存储桶内复制对象到新的路径。
     * 源对象保持不变，目标路径将创建一个副本。
     *
     * @param s3Client   S3客户端实例，不能为null
     * @param bucketName 存储桶名称，不能为空
     * @param sourceKey  源对象键，不能为空
     * @param targetKey  目标对象键，不能为空
     * @throws FileException 当复制操作失败时抛出
     */
    public void copyObject(S3Client s3Client, String bucketName, String sourceKey, String targetKey) {
        try {
            CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                    .sourceBucket(bucketName)
                    .sourceKey(sourceKey)
                    .destinationBucket(bucketName)
                    .destinationKey(targetKey)
                    .build();

            s3Client.copyObject(copyObjectRequest);
            log.debug("亚马逊S3对象复制成功: {} -> {}", sourceKey, targetKey);
        } catch (Exception e) {
            log.error("亚马逊S3对象复制失败: {} -> {}", sourceKey, targetKey, e);
            throw new FileException("亚马逊S3对象复制失败: " + e.getMessage());
        }
    }

    /**
     * 删除对象
     * <p>
     * 从S3中删除指定的对象。
     * 此操作不可恢复，请谨慎使用。
     *
     * @param s3Client   S3客户端实例，不能为null
     * @param bucketName 存储桶名称，不能为空
     * @param objectKey  要删除的对象键，不能为空
     * @throws FileException 当删除操作失败时抛出
     */
    public void deleteObject(S3Client s3Client, String bucketName, String objectKey) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.debug("亚马逊S3对象删除成功: {}", objectKey);
        } catch (Exception e) {
            log.error("亚马逊S3对象删除失败: {}", objectKey, e);
            throw new FileException("亚马逊S3对象删除失败: " + e.getMessage());
        }
    }

    /**
     * 移动对象（复制后删除原对象）
     * <p>
     * 将对象从源路径移动到目标路径。
     * 实现原理：先复制到目标路径，再删除源路径的对象。
     *
     * @param s3Client   S3客户端实例，不能为null
     * @param bucketName 存储桶名称，不能为空
     * @param sourceKey  源对象键，不能为空
     * @param targetKey  目标对象键，不能为空
     * @throws FileException 当移动操作失败时抛出
     */
    public void moveObject(S3Client s3Client, String bucketName, String sourceKey, String targetKey) {
        try {
            // 先复制
            copyObject(s3Client, bucketName, sourceKey, targetKey);
            // 再删除原对象
            deleteObject(s3Client, bucketName, sourceKey);
            log.debug("亚马逊S3对象移动成功: {} -> {}", sourceKey, targetKey);
        } catch (Exception e) {
            log.error("亚马逊S3对象移动失败: {} -> {}", sourceKey, targetKey, e);
            throw new FileException("亚马逊S3对象移动失败: " + e.getMessage());
        }
    }

    /**
     * 获取对象元数据信息
     * <p>
     * 获取对象的元数据信息，包括大小、类型、最后修改时间等。
     * 不会下载对象内容，只获取元数据。
     *
     * @param s3Client   S3客户端实例，不能为null
     * @param bucketName 存储桶名称，不能为空
     * @param objectKey  对象键，不能为空
     * @return 对象元数据，包含文件大小、类型等信息
     * @throws FileException 当获取元数据失败时抛出
     */
    public HeadObjectResponse getObjectMetadata(S3Client s3Client, String bucketName, String objectKey) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            return s3Client.headObject(headObjectRequest);
        } catch (Exception e) {
            log.error("亚马逊S3获取对象信息失败: {}/{}", bucketName, objectKey, e);
            throw new FileException("亚马逊S3获取对象信息失败: " + e.getMessage());
        }
    }

    /**
     * 安全关闭S3客户端
     * <p>
     * 安全地关闭S3客户端并释放相关资源。
     * 该方法会捕获所有异常，确保不会因为关闭操作而影响主业务流程。
     *
     * @param s3Client S3客户端实例，可以为null
     */
    public void closeS3Client(S3Client s3Client) {
        if (s3Client != null) {
            try {
                s3Client.close();
                log.debug("亚马逊S3客户端已安全关闭");
            } catch (Exception e) {
                log.warn("关闭亚马逊S3客户端时发生异常: {}", e.getMessage());
            }
        }
    }
}
