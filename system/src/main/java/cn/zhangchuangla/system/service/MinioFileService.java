package cn.zhangchuangla.system.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhangchuang
 */
public interface MinioFileService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件访问路径
     */
    String uploadFile(MultipartFile file);
}
