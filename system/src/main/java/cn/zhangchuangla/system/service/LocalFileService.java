package cn.zhangchuangla.system.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhangchuang
 */
public interface LocalFileService {

    /**
     * 本地文件上传
     *
     * @param file 文件
     * @return 返回资源地址
     */
    String uploadFile(MultipartFile file);

}
