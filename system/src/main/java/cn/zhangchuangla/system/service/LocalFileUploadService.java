package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.FileManagement;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author zhangchuang
 * Created on 2025/3/22 20:26
 */
public interface LocalFileUploadService {


    HashMap<String,String> localUploadBytes(byte[] data, String fileName) throws IOException;


    void deleteFileByFileId(FileManagement fileManagement);
}
