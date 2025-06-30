package cn.zhangchuangla.storage.service;

import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/1 02:49
 */
public interface StorageService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件信息
     */
    UploadedFileInfo upload(MultipartFile file);

    /**
     * 上传图片
     *
     * @param file 文件
     * @return 文件信息
     */
    UploadedFileInfo uploadImage(MultipartFile file);
}
