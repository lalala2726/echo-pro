package cn.zhangchuangla.system.service;

/**
 * @author zhangchuang
 * Created on 2025/3/22 20:27
 */
public interface MinioFileUploadService {




    String minioUploadBytes(byte[] data, String fileName, String contentType);
}
