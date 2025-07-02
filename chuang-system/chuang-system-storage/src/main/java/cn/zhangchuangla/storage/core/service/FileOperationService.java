package cn.zhangchuangla.storage.core.service;

import cn.zhangchuangla.storage.model.dto.FileOperationDto;
import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import cn.zhangchuangla.storage.model.entity.config.LocalFileStorageConfig;
import org.springframework.web.multipart.MultipartFile;


/**
 * 存储操作的核心接口。
 *
 * @author Chuang
 */
public interface FileOperationService {

    /**
     * 获取本地文件存储配置
     *
     * @return 当前存储配置
     */
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
     * 根据 forceDelete 参数，决定是直接从文件系统删除还是移入回收站。
     *
     * @param fileOperationDto 文件传输对象
     * @param forceDelete      true: 强制从文件系统删除；false: 移入回收站
     * @return 如果是移入回收站，返回包含新路径的DTO；如果是强制删除或文件不存在，返回null
     */
    FileOperationDto delete(FileOperationDto fileOperationDto, boolean forceDelete);

    /**
     * 恢复文件
     *
     * @return 恢复结果
     */
    boolean restore(FileOperationDto fileOperationDto);


    /**
     * 删除回收站文件
     *
     * @param fileOperationDto 文件传输对象
     * @return 删除结果
     */
    boolean deleteTrashFile(FileOperationDto fileOperationDto);

}
