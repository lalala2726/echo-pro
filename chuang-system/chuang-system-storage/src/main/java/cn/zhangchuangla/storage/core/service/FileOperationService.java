package cn.zhangchuangla.storage.core.service;

import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import org.springframework.web.multipart.MultipartFile;


/**
 * 存储操作的核心接口。
 *
 * @author Chuang
 */
public interface FileOperationService {

    /**
     * 上传文件
     *
     * @return 文件上传结果
     */
    UploadedFileInfo upload(MultipartFile file);

    /**
     * 上传图片
     *
     * @return 文件上传结果
     */
    UploadedFileInfo uploadImage(MultipartFile file);

    /**
     * 删除文件 (默认到回收站)
     *
     * @param relativePath 文件相对路径
     * @param realDelete   是否永久删除
     * @return 删除结果
     */
    boolean delete(String relativePath, boolean realDelete);

    /**
     * 恢复文件
     *
     * @param originalRelativePath 文件原始相对路径
     * @param trashRelativePath    文件回收相对路径
     * @return 恢复结果
     */
    boolean restore(String originalRelativePath, String trashRelativePath);

    /**
     * 删除文件回收站文件
     *
     * @param relativePath 文件相对路径
     * @return 删除结果
     */
    boolean deleteTrash(String relativePath);


}
