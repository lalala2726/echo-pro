package cn.zhangchuangla.common.utils;

import cn.zhangchuangla.common.config.AliyunOSSConfig;
import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.common.config.MinioConfig;
import cn.zhangchuangla.common.exception.ProfileException;
import cn.zhangchuangla.common.model.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.model.entity.file.LocalFileConfigEntity;
import cn.zhangchuangla.common.model.entity.file.MinioConfigEntity;
import org.apache.commons.lang3.StringUtils;

/**
 * 配置文件工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/2/17 22:00
 */
public class ProfileUtils {

    /**
     * 校验字符串是否为空
     *
     * @param value   需要校验的字符串
     * @param message 异常提示信息
     */
    private static void validate(String value, String message) {
        if (StringUtils.isBlank(value)) {
            throw new ProfileException("系统配置中的 " + message + " 不能为空");
        }
    }

    /**
     * 检查配置文件中是否有上传文件的配置
     *
     * @param appConfig 配置文件
     */
    public static void checkLocalPropertiesLoadFileUpload(AppConfig appConfig) {
        if (appConfig == null) {
            throw new ProfileException("本地配置文件为空，请检查！");
        }
        if (appConfig.getUploadPath() == null) {
            throw new ProfileException("本地配置文件中的上传文件路径为空！建议设置本地文件上传路径");
        }
    }

    /**
     * 检查 Minio 文件上传配置
     *
     * @param minioConfig Minio 配置
     * @return 通过代表 true
     */
    public static boolean checkLocalPropertiesMinioFileUpload(MinioConfig minioConfig) {
        if (minioConfig == null) {
            throw new ProfileException("本地配置文件的Minio 配置为空，请检查！");
        }
        validate(minioConfig.getEndpoint(), "本地配置文件Minio 访问端点 (endpoint)");
        validate(minioConfig.getAccessKey(), "本地配置文件Minio 访问密钥 (accessKey)");
        validate(minioConfig.getSecretKey(), "本地配置文件Minio 密钥 (secretKey)");
        validate(minioConfig.getBucketName(), "本地配置文件Minio 存储桶名称 (bucketName)");
        return true;
    }

    /**
     * 检查阿里云 OSS 文件上传配置
     *
     * @param aliyunOssConfig OSS 配置
     * @return 通过代表 true
     */
    public static boolean checkLocalPropertiesAliyunOssFileUpload(AliyunOSSConfig aliyunOssConfig) {
        if (aliyunOssConfig == null) {
            throw new ProfileException("本地配置文件的阿里云 OSS 配置为空，请检查！");
        }
        validate(aliyunOssConfig.getEndPoint(), "本地配置文件OSS 访问端点 (endpoint)");
        validate(aliyunOssConfig.getAccessKeyId(), "本地配置文件OSS AccessKey (accessKeyId)");
        validate(aliyunOssConfig.getAccessKeySecret(), "本地配置文件OSS AccessKey Secret (accessKeySecret)");
        validate(aliyunOssConfig.getBucketName(), "本地配置文件OSS BucketName (bucketName)");
        validate(aliyunOssConfig.getFileDomain(), "本地配置文件OSS FileDomain (fileDomain)");
        return true;
    }

    /**
     * 校验缓存中阿里云 OSS 配置属性
     *
     * @param aliyunOssConfig OSS 配置实体
     * @throws ProfileException 如果某个属性为空，抛出异常
     */
    public static void checkCachePropertiesAliyunOssFileUpload(AliyunOSSConfigEntity aliyunOssConfig) {
        if (aliyunOssConfig == null) {
            throw new ProfileException("缓存中的阿里云 OSS 配置为空，请检查！");
        }
        validate(aliyunOssConfig.getEndpoint(), "缓存中的阿里云OSS 访问端点 (endpoint)");
        validate(aliyunOssConfig.getAccessKeyId(), "缓存中的阿里云OSS AccessKey (accessKeyId)");
        validate(aliyunOssConfig.getAccessKeySecret(), "缓存中的阿里云OSS AccessKey Secret (accessKeySecret)");
        validate(aliyunOssConfig.getBucketName(), "缓存中的阿里云OSS BucketName (bucketName)");
        validate(aliyunOssConfig.getFileDomain(), "缓存中的阿里云OSS 访问域名 (FileDomain)");
        validate(aliyunOssConfig.getBucketPath(), "缓存中的阿里云OSS 存储空间路径 (bucketPath)");
    }

    /**
     * 校验缓存中 Minio 配置属性
     *
     * @param minioConfig Minio 配置实体
     * @throws ProfileException 如果某个属性为空，抛出异常
     */
    public static void checkCachePropertiesMinioConfig(MinioConfigEntity minioConfig) {
        if (minioConfig == null) {
            throw new ProfileException("缓存中的Minio 配置为空，请检查！");
        }
        validate(minioConfig.getEndpoint(), "缓存中的Minio 访问端点 (endpoint)");
        validate(minioConfig.getAccessKey(), "缓存中的Minio 访问密钥 (accessKey)");
        validate(minioConfig.getSecretKey(), "缓存中的Minio 密钥 (secretKey)");
        validate(minioConfig.getBucketName(), "缓存中的Minio 存储桶名称 (bucketName)");
        validate(minioConfig.getFileDomain(), "缓存中的Minio 文件访问域名 (FileDomain)");
        validate(minioConfig.getBucketRegion(), "缓存中的Minio 存储桶区域 (bucketRegion)");
    }

    /**
     * 校验缓存中本地文件上传配置
     *
     * @param localFileConfig 本地文件上传配置
     */
    public static void checkCachePropertiesLocalFileUpload(LocalFileConfigEntity localFileConfig) {
        if (localFileConfig.getUploadPath() == null) {
            throw new ProfileException("缓存中的中的本地文件上传路径为空，请检查！");
        }
    }

}
