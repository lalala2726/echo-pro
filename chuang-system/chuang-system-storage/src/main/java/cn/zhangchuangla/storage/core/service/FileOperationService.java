package cn.zhangchuangla.storage.core.service;

import cn.zhangchuangla.storage.model.dto.FileTrashInfoDTO;
import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import cn.zhangchuangla.storage.model.entity.FileRecord;
import cn.zhangchuangla.storage.model.entity.config.LocalFileStorageConfig;
import org.springframework.web.multipart.MultipartFile;


/**
 * 存储操作的核心接口。
 *
 * @author Chuang
 */
public interface FileOperationService {

    LocalFileStorageConfig getConfig();

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
     * 删除文件。
     * 根据 forceDelete 参数，决定是直接从文件系统删除还是移入回收站。
     *
     * @param originalRelativePath 原始文件的相对路径
     * @param previewRelativePath  预览文件（如果存在）的相对路径
     * @param forceDelete          true: 强制从文件系统删除；false: 移入回收站
     * @return 如果是移入回收站，返回包含新路径的DTO；如果是强制删除或文件不存在，返回null
     */
    FileTrashInfoDTO delete(String originalRelativePath, String previewRelativePath, boolean forceDelete);

    /**
     * 恢复文件
     *
     * @return 恢复结果
     */
    boolean restore(FileRecord fileRecord);

    /**
     * 删除文件回收站文件
     *
     * @param relativePath 文件相对路径
     * @return 删除结果
     */
    boolean deleteTrashFile(String relativePath);


}
