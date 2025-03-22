package cn.zhangchuangla.system.service;

import java.io.IOException;

/**
 * @author zhangchuang
 * Created on 2025/3/22 20:27
 */
public interface AliyunOssUploadService {

    String aliyunOssUploadBytes(byte[] data, String fileName, String contentType) throws IOException;
}
