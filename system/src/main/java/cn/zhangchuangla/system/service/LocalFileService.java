package cn.zhangchuangla.system.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhangchuang
 */
public interface LocalFileService {

    String uploadFile(MultipartFile file);

}
