package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.FileManagement;

import java.util.HashMap;

/**
 * @author zhangchuang
 * Created on 2025/3/22 20:27
 */
public interface MinioFileUploadService {




    HashMap<String, String> minioUploadBytes(byte[] data, String fileName, String contentType);

    void deleteFileByFileId(FileManagement fileManagement);
}
