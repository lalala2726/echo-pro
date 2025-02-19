package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.common.config.MinioConfig;
import cn.zhangchuangla.common.config.AliyunOSSConfig;
import cn.zhangchuangla.common.enums.FileUploadMethod;
import cn.zhangchuangla.common.utils.ProfileUtils;
import cn.zhangchuangla.system.service.AliyunOssFileService;
import cn.zhangchuangla.system.service.FileService;
import cn.zhangchuangla.system.service.LocalFileService;
import cn.zhangchuangla.system.service.MinioFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/17 20:59
 */
@Service
public class FileServiceImpl implements FileService {

    private final AppConfig appConfig;

    private final MinioConfig minioConfig;

    private final AliyunOSSConfig aliyunOssConfig;

    private final LocalFileService localFileService;

    private final MinioFileService minioFileService;

    private final AliyunOssFileService aliyunOssFileService;

    public FileServiceImpl(AppConfig appConfig, MinioConfig minioConfig, AliyunOSSConfig aliyunOssConfig, LocalFileService localFileService, MinioFileService minioFileService, AliyunOssFileService aliyunOssFileService) {
        this.appConfig = appConfig;
        this.minioConfig = minioConfig;
        this.aliyunOssConfig = aliyunOssConfig;
        this.localFileService = localFileService;
        this.minioFileService = minioFileService;
        this.aliyunOssFileService = aliyunOssFileService;
    }

    /**
     * 自动选择上传文件的方式
     *
     * @param file 文件
     * @return URL
     */
    @Override
    public String autoUploadFile(MultipartFile file) {
        // 本地上传
        if (ProfileUtils.checkLoadFileUploadProperties(appConfig)) {
            return localFileService.uploadFile(file);
            // Minio上传
        } else if (ProfileUtils.checkMinioFileUploadProperties(minioConfig)) {
            return minioFileService.uploadFile(file);
            // 阿里云OSS上传
        } else if (ProfileUtils.checkAliyunOssFileUploadProperties(aliyunOssConfig)) {
            return aliyunOssFileService.upload(file);
        }
        return null;
    }

    /**
     * 指定上传文件的方式
     *
     * @param file   文件
     * @param method 上传方式
     * @return URL
     */
    @Override
    public String specifyUploadFile(MultipartFile file, FileUploadMethod method) {
        return switch (method) {
            case LOCAL -> localFileService.uploadFile(file);
            case MINIO -> minioFileService.uploadFile(file);
            case ALIYUN_OSS -> aliyunOssFileService.upload(file);
        };
    }
}
