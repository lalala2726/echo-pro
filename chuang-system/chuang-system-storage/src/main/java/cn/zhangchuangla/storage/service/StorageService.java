package cn.zhangchuangla.storage.service;

import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    /**
     * 删除文件
     *
     * @param id 文件id
     * @return 是否成功
     */
    boolean delete(Long id, boolean forceDelete);

    /**
     * 批量删除文件
     *
     * @param ids 文件id
     * @return 是否成功
     */
    boolean delete(List<Long> ids, boolean forceDelete);

}
