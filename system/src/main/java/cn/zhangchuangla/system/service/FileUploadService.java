package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.response.FileUploadResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhangchuang
 * Created on 2025/3/21 23:43
 */
public interface FileUploadService {

    /**
     * 阿里云OSS文件上传
     *
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

    /**
     * 智能文件上传（检测图片并处理）
     * 如果是图片文件，会进行压缩处理并上传两个版本（原图和压缩图）
     * 如果不是图片文件，则正常上传
     *
     * @param file        上传的文件
     * @param storageType 存储类型 (LOCAL/MINIO/ALIYUN_OSS)
     * @return 文件上传结果，包含文件URL
     */
    FileUploadResult uploadWithImageProcess(MultipartFile file, String storageType);
}
