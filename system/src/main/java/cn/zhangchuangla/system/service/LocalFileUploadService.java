package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.dto.FileUploadByByteDto;
import cn.zhangchuangla.system.model.entity.FileManagement;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author zhangchuang
 * Created on 2025/3/22 20:26
 */
public interface LocalFileUploadService {


    /**
     * 上传文件
     *
     * @param fileUploadByByteDto 文件信息
     * @return 文件URL和文件存储相对路径
     * @throws IOException 文件上传失败
     */
    HashMap<String, String> localUploadBytes(FileUploadByByteDto fileUploadByByteDto);


    /**
     * 删除文件
     *
     * @param fileManagement 文件管理实体
     */
    void deleteFileByFileId(FileManagement fileManagement);
}
