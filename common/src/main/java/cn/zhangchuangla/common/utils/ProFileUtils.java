package cn.zhangchuangla.common.utils;

import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.common.config.MinioConfig;
import cn.zhangchuangla.common.config.OSSConfig;
import cn.zhangchuangla.common.exception.ProFileException;

/**
 * 配置文件工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/2/17 22:00
 */
public class ProFileUtils {


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
            throw new ProFileException("Minio地址为空");
        }
        if (minioConfig.getAccessKey().isEmpty()) {
            throw new ProFileException("Minio AccessKey为空");
        }
        if (minioConfig.getSecretKey().isEmpty()) {
            throw new ProFileException("Minio SecretKey为空");
        }
        if (minioConfig.getBucketName().isEmpty()) {
            throw new ProFileException("Minio BucketName为空");
        }
        return true;
    }

    /**
     * 检查阿里云OSS文件上传配置
     *
     * @param ossConfig OSS配置
     * @return 通过代表true
     */
    public static boolean checkAliyunOssFileUploadProperties(OSSConfig ossConfig) {
        if (ossConfig.getEndPoint().isEmpty()) {
            throw new ProFileException("OSS地址为空");
        }
        if (ossConfig.getAccessKeyId().isEmpty()) {
            throw new ProFileException("OSS AccessKey为空");
        }
        if (ossConfig.getAccessKeySecret().isEmpty()) {
            throw new ProFileException("OSS SecretKey为空");
        }
        if (ossConfig.getBucketName().isEmpty()) {
            throw new ProFileException("OSS BucketName为空");
        }
        if (ossConfig.getFileDomain().isEmpty()) {
            throw new ProFileException("OSS FileDomain为空");
        }
        return true;
    }
}
