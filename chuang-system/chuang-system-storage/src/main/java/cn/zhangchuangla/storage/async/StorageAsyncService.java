package cn.zhangchuangla.storage.async;

import cn.zhangchuangla.storage.constant.StorageConstants;
import cn.zhangchuangla.storage.core.service.StorageConfigRetrievalService;
import cn.zhangchuangla.storage.model.entity.config.AliyunOssStorageConfig;
import cn.zhangchuangla.storage.model.entity.config.AmazonS3StorageConfig;
import cn.zhangchuangla.storage.model.entity.config.MinioStorageConfig;
import cn.zhangchuangla.storage.model.entity.config.TencentCosStorageConfig;
import cn.zhangchuangla.storage.utils.*;
import com.alibaba.fastjson2.JSON;
import com.aliyun.oss.OSS;
import com.qcloud.cos.COSClient;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.*;

/**
 * 存储模块异步服务
 * <p>
 * 专门处理存储相关的异步任务，支持多种存储类型的图片压缩功能：
 * <ul>
 *   <li>本地文件系统异步压缩</li>
 *   <li>MinIO存储异步压缩</li>
 *   <li>阿里云OSS存储异步压缩</li>
 *   <li>腾讯云COS存储异步压缩</li>
 *   <li>亚马逊S3存储异步压缩</li>
 * </ul>
 *
 * @author Chuang
 * @since 2025/7/3
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageAsyncService {

    private final StorageConfigRetrievalService storageConfigRetrievalService;
    private final MinioOperationUtils minioOperationUtils;
    private final AliyunOssOperationUtils aliyunOssOperationUtils;
    private final TencentCosOperationUtils tencentCosOperationUtils;
    private final AmazonS3OperationUtils amazonS3OperationUtils;

    /**
     * 异步压缩图片 - 本地存储专用
     * <p>
     * 此方法专门为本地存储设计，直接操作本地文件系统
     * 适用于LocalFileOperationServiceImpl中的图片压缩需求
     *
     * @param originalFilePath   原图文件路径（本地文件系统绝对路径）
     * @param compressedFilePath 压缩图文件路径（本地文件系统绝对路径）
     * @param maxWidth           最大宽度
     * @param maxHeight          最大高度
     * @param quality            压缩质量
     * @param originalFilename   原始文件名
     */
    @Async("imageProcessExecutor")
    public void compressImageLocal(String originalFilePath, String compressedFilePath,
                                   int maxWidth, int maxHeight, float quality, String originalFilename) {
        try {
            log.info("开始异步压缩图片: {} -> {}", originalFilePath, compressedFilePath);

            // 确保压缩图目录存在
            File compressedFile = new File(compressedFilePath);
            File parentDir = compressedFile.getParentFile();
            if (!parentDir.exists() && !parentDir.mkdirs()) {
                throw new IOException("无法创建压缩图目录: " + parentDir.getAbsolutePath());
            }

            // 执行压缩
            try (InputStream in = new FileInputStream(originalFilePath);
                 OutputStream out = new FileOutputStream(compressedFilePath)) {
                ImageStreamUtils.compress(in, out, maxWidth, maxHeight, quality, originalFilename);
            }

            log.info("异步图片压缩完成: {}", compressedFilePath);
        } catch (Exception e) {
            log.error("异步图片压缩失败: {} -> {}, 错误: {}", originalFilePath, compressedFilePath, e.getMessage(), e);
            // 压缩失败时清理可能创建的空文件
            try {
                File compressedFile = new File(compressedFilePath);
                if (compressedFile.exists()) {
                    FileUtils.deleteQuietly(compressedFile);
                }
            } catch (Exception cleanupException) {
                log.error("清理压缩失败文件时出错: {}", cleanupException.getMessage());
            }
        }
    }

    /**
     * 异步压缩图片 - MinIO存储专用
     * <p>
     * 此方法专门为MinIO存储设计，从MinIO下载原图，压缩后上传预览图
     *
     * @param bucketName        存储桶名称
     * @param originalImagePath 原图在MinIO中的路径
     * @param previewImagePath  预览图在MinIO中的路径
     * @param maxWidth          最大宽度
     * @param maxHeight         最大高度
     * @param quality           压缩质量
     * @param originalFilename  原始文件名
     */
    @Async("imageProcessExecutor")
    public void compressImageMinio(String bucketName, String originalImagePath, String previewImagePath,
                                   int maxWidth, int maxHeight, float quality, String originalFilename) {
        MinioClient client = null;
        InputStream originalInputStream = null;
        ByteArrayOutputStream compressedOutputStream = null;
        ByteArrayInputStream compressedInputStream = null;

        try {
            log.info("开始MinIO异步图片压缩: {}/{} -> {}/{}", bucketName, originalImagePath, bucketName, previewImagePath);

            // 获取MinIO配置
            MinioStorageConfig config = getMinioConfig();
            client = minioOperationUtils.createMinioClient(config);

            // 确保存储桶存在
            minioOperationUtils.ensureBucketExists(client, bucketName);

            // 检查原图是否存在
            if (!minioOperationUtils.objectExists(client, bucketName, originalImagePath)) {
                log.error("MinIO原图不存在: {}/{}", bucketName, originalImagePath);
                return;
            }

            // 从MinIO下载原图
            originalInputStream = minioOperationUtils.getObjectStream(client, bucketName, originalImagePath);

            // 创建压缩输出流
            compressedOutputStream = new ByteArrayOutputStream();

            // 执行图片压缩
            ImageStreamUtils.compress(
                    originalInputStream,
                    compressedOutputStream,
                    maxWidth,
                    maxHeight,
                    quality,
                    originalFilename
            );

            // 上传压缩后的图片到MinIO
            byte[] compressedData = compressedOutputStream.toByteArray();
            minioOperationUtils.uploadByteArray(client, bucketName, previewImagePath, compressedData, "image/jpeg");

            log.info("MinIO异步图片压缩完成: {}/{} -> {}/{}, 压缩后大小: {} bytes",
                    bucketName, originalImagePath, bucketName, previewImagePath, compressedData.length);

        } catch (Exception e) {
            log.error("MinIO异步图片压缩失败: {}/{} -> {}/{}, 错误: {}",
                    bucketName, originalImagePath, bucketName, previewImagePath, e.getMessage(), e);
        } finally {
            // 确保所有流都被正确关闭
            closeStreams(originalInputStream, compressedOutputStream, compressedInputStream);
        }
    }

    /**
     * 异步压缩图片 - 阿里云OSS存储专用
     * <p>
     * 此方法专门为阿里云OSS存储设计，从OSS下载原图，压缩后上传预览图
     *
     * @param bucketName        存储桶名称
     * @param originalImagePath 原图在OSS中的路径
     * @param previewImagePath  预览图在OSS中的路径
     * @param maxWidth          最大宽度
     * @param maxHeight         最大高度
     * @param quality           压缩质量
     * @param originalFilename  原始文件名
     */
    @Async("imageProcessExecutor")
    public void compressImageAliyunOss(String bucketName, String originalImagePath, String previewImagePath,
                                       int maxWidth, int maxHeight, float quality, String originalFilename) {
        OSS ossClient = null;
        InputStream originalInputStream = null;
        ByteArrayOutputStream compressedOutputStream = null;

        try {
            log.info("开始阿里云OSS异步图片压缩: {}/{} -> {}/{}", bucketName, originalImagePath, bucketName, previewImagePath);

            // 获取阿里云OSS配置
            AliyunOssStorageConfig config = getAliyunOssConfig();
            ossClient = aliyunOssOperationUtils.createOssClient(config);

            // 确保存储桶存在
            aliyunOssOperationUtils.ensureBucketExists(ossClient, bucketName);

            // 检查原图是否存在
            if (!aliyunOssOperationUtils.objectExists(ossClient, bucketName, originalImagePath)) {
                log.error("阿里云OSS原图不存在: {}/{}", bucketName, originalImagePath);
                return;
            }

            // 从阿里云OSS下载原图
            originalInputStream = aliyunOssOperationUtils.getObjectStream(ossClient, bucketName, originalImagePath);

            // 创建压缩输出流
            compressedOutputStream = new ByteArrayOutputStream();

            // 执行图片压缩
            ImageStreamUtils.compress(
                    originalInputStream,
                    compressedOutputStream,
                    maxWidth,
                    maxHeight,
                    quality,
                    originalFilename
            );

            // 上传压缩后的图片到阿里云OSS
            byte[] compressedData = compressedOutputStream.toByteArray();
            aliyunOssOperationUtils.uploadByteArray(ossClient, bucketName, previewImagePath, compressedData, "image/jpeg");

            log.info("阿里云OSS异步图片压缩完成: {}/{} -> {}/{}, 压缩后大小: {} bytes",
                    bucketName, originalImagePath, bucketName, previewImagePath, compressedData.length);

        } catch (Exception e) {
            log.error("阿里云OSS异步图片压缩失败: {}/{} -> {}/{}, 错误: {}",
                    bucketName, originalImagePath, bucketName, previewImagePath, e.getMessage(), e);
        } finally {
            // 确保所有流都被正确关闭
            closeStreams(originalInputStream, compressedOutputStream);
            // 关闭OSS客户端
            if (ossClient != null) {
                aliyunOssOperationUtils.closeOssClient(ossClient);
            }
        }
    }

    /**
     * 异步压缩图片 - 腾讯云COS存储专用
     * <p>
     * 此方法专门为腾讯云COS存储设计，从COS下载原图，压缩后上传预览图
     *
     * @param bucketName        存储桶名称
     * @param originalImagePath 原图在COS中的路径
     * @param previewImagePath  预览图在COS中的路径
     * @param maxWidth          最大宽度
     * @param maxHeight         最大高度
     * @param quality           压缩质量
     * @param originalFilename  原始文件名
     */
    @Async("imageProcessExecutor")
    public void compressImageTencentCos(String bucketName, String originalImagePath, String previewImagePath,
                                        int maxWidth, int maxHeight, float quality, String originalFilename) {
        COSClient cosClient = null;
        InputStream originalInputStream = null;
        ByteArrayOutputStream compressedOutputStream = null;

        try {
            log.info("开始腾讯云COS异步图片压缩: {}/{} -> {}/{}", bucketName, originalImagePath, bucketName, previewImagePath);

            // 获取腾讯云COS配置
            TencentCosStorageConfig config = getTencentCosConfig();
            cosClient = tencentCosOperationUtils.createCosClient(config);

            // 确保存储桶存在
            tencentCosOperationUtils.ensureBucketExists(cosClient, bucketName);

            // 检查原图是否存在
            if (!tencentCosOperationUtils.objectExists(cosClient, bucketName, originalImagePath)) {
                log.error("腾讯云COS原图不存在: {}/{}", bucketName, originalImagePath);
                return;
            }

            // 从腾讯云COS下载原图
            originalInputStream = tencentCosOperationUtils.getObjectStream(cosClient, bucketName, originalImagePath);

            // 创建压缩输出流
            compressedOutputStream = new ByteArrayOutputStream();

            // 执行图片压缩
            ImageStreamUtils.compress(
                    originalInputStream,
                    compressedOutputStream,
                    maxWidth,
                    maxHeight,
                    quality,
                    originalFilename
            );

            // 上传压缩后的图片到腾讯云COS
            byte[] compressedData = compressedOutputStream.toByteArray();
            tencentCosOperationUtils.uploadByteArray(cosClient, bucketName, previewImagePath, compressedData, "image/jpeg");

            log.info("腾讯云COS异步图片压缩完成: {}/{} -> {}/{}, 压缩后大小: {} bytes",
                    bucketName, originalImagePath, bucketName, previewImagePath, compressedData.length);

        } catch (Exception e) {
            log.error("腾讯云COS异步图片压缩失败: {}/{} -> {}/{}, 错误: {}",
                    bucketName, originalImagePath, bucketName, previewImagePath, e.getMessage(), e);
        } finally {
            // 确保所有流都被正确关闭
            closeStreams(originalInputStream, compressedOutputStream);
            // 关闭COS客户端
            if (cosClient != null) {
                tencentCosOperationUtils.closeCosClient(cosClient);
            }
        }
    }

    /**
     * 异步压缩图片 - 亚马逊S3存储专用
     * <p>
     * 此方法专门为亚马逊S3存储设计，从S3下载原图，压缩后上传预览图
     * 兼容标准S3协议和其他S3兼容的存储服务
     *
     * @param bucketName        存储桶名称
     * @param originalImagePath 原图在S3中的路径
     * @param previewImagePath  预览图在S3中的路径
     * @param maxWidth          最大宽度
     * @param maxHeight         最大高度
     * @param quality           压缩质量
     * @param originalFilename  原始文件名
     */
    @Async("imageProcessExecutor")
    public void compressImageAmazonS3(String bucketName, String originalImagePath, String previewImagePath,
                                      int maxWidth, int maxHeight, float quality, String originalFilename) {
        S3Client s3Client = null;
        InputStream originalInputStream = null;
        ByteArrayOutputStream compressedOutputStream = null;

        try {
            log.info("开始亚马逊S3异步图片压缩: {}/{} -> {}/{}", bucketName, originalImagePath, bucketName, previewImagePath);

            // 获取亚马逊S3配置
            AmazonS3StorageConfig config = getAmazonS3Config();
            s3Client = amazonS3OperationUtils.createS3Client(config);

            // 确保存储桶存在
            amazonS3OperationUtils.ensureBucketExists(s3Client, bucketName);

            // 检查原图是否存在
            if (!amazonS3OperationUtils.objectExists(s3Client, bucketName, originalImagePath)) {
                log.error("亚马逊S3原图不存在: {}/{}", bucketName, originalImagePath);
                return;
            }

            // 从亚马逊S3下载原图
            originalInputStream = amazonS3OperationUtils.getObjectStream(s3Client, bucketName, originalImagePath);

            // 创建压缩输出流
            compressedOutputStream = new ByteArrayOutputStream();

            // 执行图片压缩
            ImageStreamUtils.compress(
                    originalInputStream,
                    compressedOutputStream,
                    maxWidth,
                    maxHeight,
                    quality,
                    originalFilename
            );

            // 上传压缩后的图片到亚马逊S3
            byte[] compressedData = compressedOutputStream.toByteArray();
            amazonS3OperationUtils.uploadByteArray(s3Client, bucketName, previewImagePath, compressedData, "image/jpeg");

            log.info("亚马逊S3异步图片压缩完成: {}/{} -> {}/{}, 压缩后大小: {} bytes",
                    bucketName, originalImagePath, bucketName, previewImagePath, compressedData.length);

        } catch (Exception e) {
            log.error("亚马逊S3异步图片压缩失败: {}/{} -> {}/{}, 错误: {}",
                    bucketName, originalImagePath, bucketName, previewImagePath, e.getMessage(), e);
        } finally {
            // 确保所有流都被正确关闭
            closeStreams(originalInputStream, compressedOutputStream);
            // 关闭S3客户端
            if (s3Client != null) {
                amazonS3OperationUtils.closeS3Client(s3Client);
            }
        }
    }

    /**
     * 获取MinIO配置
     *
     * @return MinIO存储配置
     */
    private MinioStorageConfig getMinioConfig() {
        try {
            String json = storageConfigRetrievalService.getCurrentStorageConfigJson();
            if (json == null || json.isBlank()) {
                throw new RuntimeException("MinIO配置未找到");
            }
            return JSON.parseObject(json, MinioStorageConfig.class);
        } catch (Exception e) {
            log.error("获取MinIO配置失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取MinIO配置失败: " + e.getMessage());
        }
    }

    /**
     * 获取阿里云OSS配置
     *
     * @return 阿里云OSS存储配置
     */
    private AliyunOssStorageConfig getAliyunOssConfig() {
        try {
            String json = storageConfigRetrievalService.getCurrentStorageConfigJson();
            if (json == null || json.isBlank()) {
                throw new RuntimeException("阿里云OSS配置未找到");
            }
            return JSON.parseObject(json, AliyunOssStorageConfig.class);
        } catch (Exception e) {
            log.error("获取阿里云OSS配置失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取阿里云OSS配置失败: " + e.getMessage());
        }
    }

    /**
     * 获取腾讯云COS配置
     *
     * @return 腾讯云COS存储配置
     */
    private TencentCosStorageConfig getTencentCosConfig() {
        try {
            String json = storageConfigRetrievalService.getCurrentStorageConfigJson();
            if (json == null || json.isBlank()) {
                throw new RuntimeException("腾讯云COS配置未找到");
            }
            return JSON.parseObject(json, TencentCosStorageConfig.class);
        } catch (Exception e) {
            log.error("获取腾讯云COS配置失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取腾讯云COS配置失败: " + e.getMessage());
        }
    }

    /**
     * 获取亚马逊S3配置
     *
     * @return 亚马逊S3存储配置
     */
    private AmazonS3StorageConfig getAmazonS3Config() {
        try {
            String json = storageConfigRetrievalService.getCurrentStorageConfigJson();
            if (json == null || json.isBlank()) {
                throw new RuntimeException("亚马逊S3配置未找到");
            }
            return JSON.parseObject(json, AmazonS3StorageConfig.class);
        } catch (Exception e) {
            log.error("获取亚马逊S3配置失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取亚马逊S3配置失败: " + e.getMessage());
        }
    }

    /**
     * 关闭流资源
     *
     * @param streams 要关闭的流
     */
    private void closeStreams(Closeable... streams) {
        for (Closeable stream : streams) {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    log.warn("关闭流时发生异常: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * 通用异步压缩图片方法
     * 根据当前存储类型自动选择对应的压缩方式
     *
     * @param originalPath     原图路径（本地路径或对象存储路径）
     * @param compressedPath   压缩图路径（本地路径或对象存储路径）
     * @param maxWidth         最大宽度
     * @param maxHeight        最大高度
     * @param quality          压缩质量
     * @param originalFilename 原始文件名
     */
    @Async("imageProcessExecutor")
    public void compressImageAuto(String originalPath, String compressedPath,
                                  int maxWidth, int maxHeight, float quality, String originalFilename) {
        try {
            String activeStorageType = storageConfigRetrievalService.getActiveStorageType();
            log.info("自动选择压缩方式 - 当前存储类型: {}", activeStorageType);

            switch (activeStorageType) {
                case StorageConstants.StorageType.LOCAL:
                    // 本地存储使用文件系统压缩
                    compressImageLocal(originalPath, compressedPath, maxWidth, maxHeight, quality, originalFilename);
                    break;

                case StorageConstants.StorageType.MINIO:
                    // MinIO存储需要额外的桶名称参数，这里从配置获取
                    MinioStorageConfig minioConfig = getMinioConfig();
                    compressImageMinio(minioConfig.getBucketName(), originalPath, compressedPath,
                            maxWidth, maxHeight, quality, originalFilename);
                    break;

                case StorageConstants.StorageType.ALIYUN_OSS:
                    // 阿里云OSS存储需要额外的桶名称参数，这里从配置获取
                    AliyunOssStorageConfig ossConfig = getAliyunOssConfig();
                    compressImageAliyunOss(ossConfig.getBucketName(), originalPath, compressedPath,
                            maxWidth, maxHeight, quality, originalFilename);
                    break;

                case StorageConstants.StorageType.TENCENT_COS:
                    // 腾讯云COS存储需要额外的桶名称参数，这里从配置获取
                    TencentCosStorageConfig cosConfig = getTencentCosConfig();
                    compressImageTencentCos(cosConfig.getBucketName(), originalPath, compressedPath,
                            maxWidth, maxHeight, quality, originalFilename);
                    break;

                case StorageConstants.StorageType.AMAZON_S3:
                    // 亚马逊S3存储需要额外的桶名称参数，这里从配置获取
                    AmazonS3StorageConfig s3Config = getAmazonS3Config();
                    compressImageAmazonS3(s3Config.getBucketName(), originalPath, compressedPath,
                            maxWidth, maxHeight, quality, originalFilename);
                    break;

                default:
                    log.warn("未知的存储类型: {}，跳过异步压缩", activeStorageType);
            }

        } catch (Exception e) {
            log.error("自动异步图片压缩失败，存储类型判断异常: {}", e.getMessage(), e);
        }
    }
}
