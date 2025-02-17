package cn.zhangchuangla.system.service;

import org.springframework.web.multipart.MultipartFile;

public interface AliyunOssFileService {

    /**
     * 阿里云OSS文件上传
     */
    String upload(MultipartFile file);
}
