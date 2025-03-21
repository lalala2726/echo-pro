package cn.zhangchuangla.system.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhangchuang
 * Created on 2025/3/21 23:43
 */
public interface FileUploadService {

    /**
     * 阿里云OSS文件上传
     * @return 文件访问路径
     */
    String AliyunOssFileUpload(MultipartFile file);


    /**
     * Minio文件上传
     *
     * @param file 文件
     * @return 文件访问路径
     */
    String MinioFileUpload(MultipartFile file);


    /**
     * 本地文件上传
     *
     * @param file 文件
     * @return 文件访问路径
     */
    String localFileUpload(MultipartFile file);
}
