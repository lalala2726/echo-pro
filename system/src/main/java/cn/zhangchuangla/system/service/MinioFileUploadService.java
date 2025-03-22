package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.dto.FileUploadByByteDto;
import cn.zhangchuangla.system.model.entity.FileManagement;

import java.util.HashMap;

/**
 * @author zhangchuang
 * Created on 2025/3/22 20:27
 */
public interface MinioFileUploadService {


    /**
     * minio文件上传
     *
     * @param fileUploadByByteDto 文件信息
     * @return 文件访问路径和文件存储相对路径
     */
    HashMap<String, String> minioUploadBytes(FileUploadByByteDto fileUploadByByteDto);

    /**
     * 根据文件id删除文件
     *
     * @param fileManagement 文件管理实体
     */
    void deleteFileByFileId(FileManagement fileManagement);
}
