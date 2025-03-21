package cn.zhangchuangla.common.utils;

import cn.zhangchuangla.common.config.AliyunOSSConfig;
import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.common.config.MinioConfig;
import cn.zhangchuangla.common.exception.ProfileException;

/**
 * 配置文件工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/2/17 22:00
 */
public class ProfileUtils {


    /**
     * 检查配置文件中是否有上传文件的配置
     *
     * @param appConfig 配置文件
     * @return 通过代表true
     */
    public static boolean checkLoadFileUploadProperties(AppConfig appConfig) {
        return appConfig.getUploadPath() != null;
    }

    /**
     * 检查Minio文件上传配置
     *
     * @param minioConfig Minio配置
     * @return 通过代表true
     */
    public static boolean checkMinioFileUploadProperties(MinioConfig minioConfig) {
        if (minioConfig.getEndpoint().isEmpty()) {
            throw new ProfileException("Minio地址为空");
        }
        if (minioConfig.getAccessKey().isEmpty()) {
            throw new ProfileException("Minio AccessKey为空");
        }
        if (minioConfig.getSecretKey().isEmpty()) {
            throw new ProfileException("Minio SecretKey为空");
        }
        if (minioConfig.getBucketName().isEmpty()) {
            throw new ProfileException("Minio BucketName为空");
        }
        return true;
    }

    /**
     * 检查阿里云OSS文件上传配置
     *
     * @param aliyunOssConfig OSS配置
     * @return 通过代表true
     */
    public static boolean checkAliyunOssFileUploadProperties(AliyunOSSConfig aliyunOssConfig) {
        if (aliyunOssConfig.getEndPoint().isEmpty()) {
            throw new ProfileException("OSS地址为空");
        }
        if (aliyunOssConfig.getAccessKeyId().isEmpty()) {
            throw new ProfileException("OSS AccessKey为空");
        }
        if (aliyunOssConfig.getAccessKeySecret().isEmpty()) {
            throw new ProfileException("OSS SecretKey为空");
        }
        if (aliyunOssConfig.getBucketName().isEmpty()) {
            throw new ProfileException("OSS BucketName为空");
        }
        if (aliyunOssConfig.getFileDomain().isEmpty()) {
            throw new ProfileException("OSS FileDomain为空");
        }
        return true;
    }
}
