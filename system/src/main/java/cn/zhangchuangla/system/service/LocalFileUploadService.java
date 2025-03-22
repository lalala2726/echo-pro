package cn.zhangchuangla.system.service;

import java.io.IOException;

/**
 * @author zhangchuang
 * Created on 2025/3/22 20:26
 */
public interface LocalFileUploadService {


    String localUploadBytes(byte[] data, String fileName) throws IOException;
}
