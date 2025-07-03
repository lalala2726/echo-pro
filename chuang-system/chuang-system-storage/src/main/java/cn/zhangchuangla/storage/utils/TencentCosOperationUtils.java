package cn.zhangchuangla.storage.utils;

import cn.zhangchuangla.common.core.exception.FileException;
import cn.zhangchuangla.storage.model.entity.config.TencentCOSStorageConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 腾讯云COS操作工具类
 * <p>
 * 提供腾讯云COS相关的基础操作方法，包括：
 * <ul>
 *   <li>COS客户端管理</li>
 *   <li>存储桶（Bucket）操作</li>
 *   <li>对象（Object）上传、下载、删除</li>
 *   <li>对象复制、移动等高级操作</li>
 *   <li>对象元数据获取</li>
 * </ul>
 *
 * <p>工具类设计原则：</p>
 * <ul>
 *   <li>职责单一：专注于腾讯云COS的基础操作</li>
 *   <li>无状态：所有方法都是静态的或无状态的</li>
 *   <li>异常统一：所有异常都包装为FileException</li>
 *   <li>日志完整：记录所有关键操作的执行情况</li>
 * </ul>
 *
 * @author Chuang
 * @since 2025/7/3
 */
@Slf4j
@Component
public class TencentCosOperationUtils {

    /**
     * 创建腾讯云COS客户端
     * <p>
     * 根据提供的配置信息创建COS客户端实例。
     * 该方法会验证配置的完整性，确保所有必需的配置项都已设置。
     *
     * @param config 腾讯云COS配置，必须包含region、secretId、secretKey
     * @return COS客户端实例，用于后续的COS操作
     * @throws FileException 当配置不完整或客户端创建失败时抛出
     */
    public COSClient createCosClient(TencentCOSStorageConfig config) {
        try {
            if (config.getRegion() == null || config.getRegion().isBlank() ||
                    config.getSecretId() == null || config.getSecretId().isBlank() ||
                    config.getSecretKey() == null || config.getSecretKey().isBlank()) {
                throw new FileException("腾讯云COS配置项不完整，无法创建客户端");
            }

            // 初始化用户身份信息（secretId、secretKey）
            COSCredentials cred = new BasicCOSCredentials(config.getSecretId(), config.getSecretKey());

            // 设置bucket的地域
            Region region = new Region(config.getRegion());
            ClientConfig clientConfig = new ClientConfig(region);

            // 生成cos客户端
            COSClient cosClient = new COSClient(cred, clientConfig);

            log.info("腾讯云COS客户端创建成功，区域: {}, 存储桶: {}", config.getRegion(), config.getBucketName());
            return cosClient;

        } catch (Exception e) {
            log.error("腾讯云COS客户端创建失败", e);
            throw new FileException("腾讯云COS客户端创建失败: " + e.getMessage());
        }
    }

    /**
     * 确保存储桶存在，如果不存在则创建
     * <p>
     * 检查指定的存储桶是否存在，如果不存在则自动创建。
     * 新创建的存储桶将设置为标准存储类型和私有访问权限。
     *
     * @param cosClient  COS客户端实例，不能为null
     * @param bucketName 存储桶名称，不能为空
     * @throws FileException 当存储桶操作失败时抛出
     */
    public void ensureBucketExists(COSClient cosClient, String bucketName) {
        try {
            boolean bucketExists = cosClient.doesBucketExist(bucketName);
            if (!bucketExists) {
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                cosClient.createBucket(createBucketRequest);
                log.info("腾讯云COS存储桶创建成功: {}", bucketName);
            }
        } catch (CosClientException e) {
            log.error("腾讯云COS存储桶操作失败: {}", bucketName, e);
            throw new FileException("腾讯云COS存储桶操作失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件到腾讯云COS
     * <p>
     * 将输入流中的数据上传到指定的COS路径。
     * 支持设置文件的内容类型和长度。
     *
     * @param cosClient   COS客户端实例，不能为null
     * @param bucketName  存储桶名称，不能为空
     * @param objectKey   对象键（文件在COS中的路径），不能为空
     * @param inputStream 文件输入流，不能为null
     * @param size        文件大小（字节数）
     * @param contentType 文件MIME类型，如"image/jpeg"、"application/pdf"等
     * @throws FileException 当文件上传失败时抛出
     */
    public void uploadFile(COSClient cosClient, String bucketName, String objectKey,
                           InputStream inputStream, long size, String contentType) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(size);
            metadata.setContentType(contentType);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectKey, inputStream, metadata);
            cosClient.putObject(putObjectRequest);

            log.debug("文件上传成功到腾讯云COS: {}/{}", bucketName, objectKey);
        } catch (CosClientException e) {
            log.error("腾讯云COS文件上传失败: {}/{}", bucketName, objectKey, e);
            throw new FileException("腾讯云COS文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传字节数组到腾讯云COS
     * <p>
     * 将字节数组数据上传到指定的COS路径。
     * 适用于已在内存中处理好的文件数据，如压缩后的图片等。
     *
     * @param cosClient   COS客户端实例，不能为null
     * @param bucketName  存储桶名称，不能为空
     * @param objectKey   对象键（文件在COS中的路径），不能为空
     * @param data        字节数组数据，不能为null
     * @param contentType 文件MIME类型，如"image/jpeg"等
     * @throws FileException 当上传失败时抛出
     */
    public void uploadByteArray(COSClient cosClient, String bucketName, String objectKey,
                                byte[] data, String contentType) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            uploadFile(cosClient, bucketName, objectKey, inputStream, data.length, contentType);
        } catch (IOException e) {
            log.error("腾讯云COS字节数组上传失败: {}/{}", bucketName, objectKey, e);
            throw new FileException("腾讯云COS字节数组上传失败: " + e.getMessage());
        }
    }

    /**
     * 从腾讯云COS获取文件流
     * <p>
     * 获取指定对象的输入流，用于读取文件内容。
     * 调用方负责关闭返回的输入流。
     *
     * @param cosClient  COS客户端实例，不能为null
     * @param bucketName 存储桶名称，不能为空
     * @param objectKey  对象键（文件在COS中的路径），不能为空
     * @return 文件输入流，调用方需要负责关闭
     * @throws FileException 当获取文件流失败时抛出
     */
    public InputStream getObjectStream(COSClient cosClient, String bucketName, String objectKey) {
        try {
            COSObject cosObject = cosClient.getObject(bucketName, objectKey);
            return cosObject.getObjectContent();
        } catch (CosClientException e) {
            log.error("腾讯云COS获取文件流失败: {}/{}", bucketName, objectKey, e);
            throw new FileException("腾讯云COS获取文件流失败: " + e.getMessage());
        }
    }

    /**
     * 检查对象是否存在
     * <p>
     * 检查指定的对象在COS中是否存在。
     *
     * @param cosClient  COS客户端实例，不能为null
     * @param bucketName 存储桶名称，不能为空
     * @param objectKey  对象键（文件在COS中的路径），不能为空
     * @return true表示对象存在，false表示不存在
     * @throws FileException 当检查操作失败时抛出
     */
    public boolean objectExists(COSClient cosClient, String bucketName, String objectKey) {
        try {
            cosClient.getObjectMetadata(bucketName, objectKey);
            return true;
        } catch (CosClientException e) {
            // CosServiceException extends CosClientException，所以这里会捕获所有异常
            if (e instanceof CosServiceException) {
                CosServiceException serviceException = (CosServiceException) e;
                if ("NoSuchKey".equals(serviceException.getErrorCode())) {
                    return false;
                }
            }
            log.error("腾讯云COS检查对象存在性失败: {}/{}", bucketName, objectKey, e);
            throw new FileException("腾讯云COS检查对象存在性失败: " + e.getMessage());
        }
    }

    /**
     * 复制对象
     * <p>
     * 在同一个存储桶内复制对象到新的路径。
     * 源对象保持不变，目标路径将创建一个副本。
     *
     * @param cosClient  COS客户端实例，不能为null
     * @param bucketName 存储桶名称，不能为空
     * @param sourceKey  源对象键，不能为空
     * @param targetKey  目标对象键，不能为空
     * @throws FileException 当复制操作失败时抛出
     */
    public void copyObject(COSClient cosClient, String bucketName, String sourceKey, String targetKey) {
        try {
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, sourceKey, bucketName, targetKey);
            cosClient.copyObject(copyObjectRequest);
            log.debug("腾讯云COS对象复制成功: {} -> {}", sourceKey, targetKey);
        } catch (CosClientException e) {
            log.error("腾讯云COS对象复制失败: {} -> {}", sourceKey, targetKey, e);
            throw new FileException("腾讯云COS对象复制失败: " + e.getMessage());
        }
    }

    /**
     * 删除对象
     * <p>
     * 从COS中删除指定的对象。
     * 此操作不可恢复，请谨慎使用。
     *
     * @param cosClient  COS客户端实例，不能为null
     * @param bucketName 存储桶名称，不能为空
     * @param objectKey  要删除的对象键，不能为空
     * @throws FileException 当删除操作失败时抛出
     */
    public void deleteObject(COSClient cosClient, String bucketName, String objectKey) {
        try {
            cosClient.deleteObject(bucketName, objectKey);
            log.debug("腾讯云COS对象删除成功: {}", objectKey);
        } catch (CosClientException e) {
            log.error("腾讯云COS对象删除失败: {}", objectKey, e);
            throw new FileException("腾讯云COS对象删除失败: " + e.getMessage());
        }
    }

    /**
     * 移动对象（复制后删除原对象）
     * <p>
     * 将对象从源路径移动到目标路径。
     * 实现原理：先复制到目标路径，再删除源路径的对象。
     *
     * @param cosClient  COS客户端实例，不能为null
     * @param bucketName 存储桶名称，不能为空
     * @param sourceKey  源对象键，不能为空
     * @param targetKey  目标对象键，不能为空
     * @throws FileException 当移动操作失败时抛出
     */
    public void moveObject(COSClient cosClient, String bucketName, String sourceKey, String targetKey) {
        try {
            // 先复制
            copyObject(cosClient, bucketName, sourceKey, targetKey);
            // 再删除原对象
            deleteObject(cosClient, bucketName, sourceKey);
            log.debug("腾讯云COS对象移动成功: {} -> {}", sourceKey, targetKey);
        } catch (Exception e) {
            log.error("腾讯云COS对象移动失败: {} -> {}", sourceKey, targetKey, e);
            throw new FileException("腾讯云COS对象移动失败: " + e.getMessage());
        }
    }

    /**
     * 获取对象元数据信息
     * <p>
     * 获取对象的元数据信息，包括大小、类型、最后修改时间等。
     * 不会下载对象内容，只获取元数据。
     *
     * @param cosClient  COS客户端实例，不能为null
     * @param bucketName 存储桶名称，不能为空
     * @param objectKey  对象键，不能为空
     * @return 对象元数据，包含文件大小、类型等信息
     * @throws FileException 当获取元数据失败时抛出
     */
    public ObjectMetadata getObjectMetadata(COSClient cosClient, String bucketName, String objectKey) {
        try {
            return cosClient.getObjectMetadata(bucketName, objectKey);
        } catch (CosClientException e) {
            log.error("腾讯云COS获取对象信息失败: {}/{}", bucketName, objectKey, e);
            throw new FileException("腾讯云COS获取对象信息失败: " + e.getMessage());
        }
    }

    /**
     * 安全关闭COS客户端
     * <p>
     * 安全地关闭COS客户端并释放相关资源。
     * 该方法会捕获所有异常，确保不会因为关闭操作而影响主业务流程。
     *
     * @param cosClient COS客户端实例，可以为null
     */
    public void closeCosClient(COSClient cosClient) {
        if (cosClient != null) {
            try {
                cosClient.shutdown();
                log.debug("腾讯云COS客户端已安全关闭");
            } catch (Exception e) {
                log.warn("关闭腾讯云COS客户端时发生异常: {}", e.getMessage());
            }
        }
    }
}
