package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.system.service.MinioFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zhangchuang
 * Created on 2025/2/17 15:40
 */
@Service
public class MinioFileServiceImpl implements MinioFileService {
    @Override
    public String uploadFile(MultipartFile file) {
        return "";
    }
}
