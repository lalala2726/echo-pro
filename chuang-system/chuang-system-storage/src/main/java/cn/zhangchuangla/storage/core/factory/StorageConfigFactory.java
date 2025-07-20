package cn.zhangchuangla.storage.core.factory;

import cn.zhangchuangla.storage.enums.StorageType;
import cn.zhangchuangla.storage.model.entity.config.AliyunOSSStorageConfig;
import cn.zhangchuangla.storage.model.entity.config.AmazonS3StorageConfig;
import cn.zhangchuangla.storage.model.entity.config.MinioStorageConfig;
import cn.zhangchuangla.storage.model.entity.config.TencentCOSStorageConfig;
import cn.zhangchuangla.storage.model.request.file.UnifiedStorageConfigRequest;

/**
 * 存储配置工厂类
 * 负责根据存储类型创建相应的配置对象
 *
 * @author Chuang
 * @since 2025/1/7
 */
public class StorageConfigFactory {

    /**
     * 根据统一请求对象创建对应的存储配置对象
     *
     * @param request 统一存储配置请求对象
     * @return 存储配置对象
     */
    public static Object createStorageConfig(UnifiedStorageConfigRequest request) {
        // 验证请求参数
        request.validate();

        return switch (request.getStorageType()) {
            case MINIO -> createMinioConfig(request);
            case ALIYUN_OSS -> createAliyunOssConfig(request);
            case TENCENT_COS -> createTencentCosConfig(request);
            case AMAZON_S3 -> createAmazonS3Config(request);
            default -> throw new IllegalArgumentException("不支持的存储类型: " + request.getStorageType());
        };
    }

    /**
     * 创建MinIO配置对象
     */
    private static MinioStorageConfig createMinioConfig(UnifiedStorageConfigRequest request) {
        MinioStorageConfig config = new MinioStorageConfig();
        config.setEndpoint(request.getEndpoint());
        config.setAccessKey(request.getAccessKeyId());
        config.setSecretKey(request.getAccessKeySecret());
        config.setBucketName(request.getBucketName());
        config.setFileDomain(request.getFileDomain());
        config.setRealDelete(request.isRealDelete());
        return config;
    }

    /**
     * 创建阿里云OSS配置对象
     */
    private static AliyunOSSStorageConfig createAliyunOssConfig(UnifiedStorageConfigRequest request) {
        AliyunOSSStorageConfig config = new AliyunOSSStorageConfig();
        config.setEndpoint(request.getEndpoint());
        config.setAccessKeyId(request.getAccessKeyId());
        config.setAccessKeySecret(request.getAccessKeySecret());
        config.setBucketName(request.getBucketName());
        config.setFileDomain(request.getFileDomain());
        config.setRealDelete(request.isRealDelete());
        return config;
    }

    /**
     * 创建腾讯云COS配置对象
     */
    private static TencentCOSStorageConfig createTencentCosConfig(UnifiedStorageConfigRequest request) {
        TencentCOSStorageConfig config = new TencentCOSStorageConfig();
        // 腾讯云COS使用region字段
        config.setRegion(request.getEndpoint());
        config.setSecretId(request.getAccessKeyId());
        config.setSecretKey(request.getAccessKeySecret());
        config.setBucketName(request.getBucketName());
        config.setFileDomain(request.getFileDomain());
        config.setRealDelete(request.isRealDelete());
        return config;
    }

    /**
     * 创建亚马逊S3配置对象
     */
    private static AmazonS3StorageConfig createAmazonS3Config(UnifiedStorageConfigRequest request) {
        AmazonS3StorageConfig config = new AmazonS3StorageConfig();
        config.setEndpoint(request.getEndpoint());
        config.setAccessKey(request.getAccessKeyId());
        config.setSecretKey(request.getAccessKeySecret());
        config.setBucketName(request.getBucketName());
        config.setRegion(request.getRegion());
        config.setFileDomain(request.getFileDomain());
        config.setRealDelete(request.isRealDelete());
        return config;
    }

    /**
     * 获取存储类型代码
     *
     * @param storageType 存储类型枚举
     * @return 存储类型代码
     */
    public static String getStorageTypeCode(StorageType storageType) {
        return storageType.getCode();
    }
}
