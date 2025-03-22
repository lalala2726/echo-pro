package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.dto.FileUploadByByteDto;
import cn.zhangchuangla.system.model.entity.FileManagement;

import java.util.HashMap;

/**
 * @author zhangchuang
 * Created on 2025/3/22 20:27
 */
public interface AliyunOssUploadService {

    /**
     * 阿里云OSS文件上传
     *
     * @param fileUploadByByteDto 文件信息
     * @return 文件访问URL和文件存储相对路径
     */
    HashMap<String, String> aliyunOssUploadBytes(FileUploadByByteDto fileUploadByByteDto);


    void deleteFileByFileId(FileManagement fileManagement);
}
