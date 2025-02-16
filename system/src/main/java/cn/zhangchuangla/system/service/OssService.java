package cn.zhangchuangla.system.service;

import cn.zhangchuangla.common.result.AjaxResult;
import org.springframework.web.multipart.MultipartFile;

public interface OssService {

    /**
     * 阿里云OSS文件上传
     *
     */
    String  upload(MultipartFile file);
}
