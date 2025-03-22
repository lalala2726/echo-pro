package cn.zhangchuangla.system.service;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author zhangchuang
 * Created on 2025/3/22 20:27
 */
public interface AliyunOssUploadService {

    HashMap<String, String> aliyunOssUploadBytes(byte[] data, String fileName, String contentType) throws IOException;
}
