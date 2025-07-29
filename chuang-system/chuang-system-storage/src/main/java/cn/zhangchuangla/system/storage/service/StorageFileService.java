package cn.zhangchuangla.system.storage.service;

import cn.zhangchuangla.system.storage.model.dto.UploadedFileInfo;
import cn.zhangchuangla.system.storage.model.entity.StorageFile;
import cn.zhangchuangla.system.storage.model.request.file.StorageFileQueryRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/1 02:49
 */
public interface StorageFileService extends IService<StorageFile> {

    /**
     * 获取文件列表
     *
     * @param request 查询参数
     * @return 文件列表
     */
    Page<StorageFile> listFileManage(StorageFileQueryRequest request);

    /**
     * 获取回收站文件列表
     *
     * @param request 文件列表查询参数
     * @return 文件列表
     */
    Page<StorageFile> listFileTrashManage(StorageFileQueryRequest request);

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
     * 批量删除文件
     *
     * @param fileIds     文件id集合
     * @param forceDelete 是否强制删除 （不经过回收站）
     * @return 是否成功
     */
    boolean deleteFileById(List<Long> fileIds, boolean forceDelete);

    /**
     * 删除文件
     *
     * @param fileId      文件UD
     * @param forceDelete 是否强制删除(不经过回收站)
     * @return 是否成功
     */
    boolean deleteFileById(Long fileId, boolean forceDelete);


    /**
     * 根据文件编号从回收站还原文件
     *
     * @param fileIds 文件id集合
     * @return 是否成功
     */
    boolean restoreFileFromRecycleBin(List<Long> fileIds);

    /**
     * 根据文件编号从回收站删除文件
     *
     * @param fileId 文件id
     * @return 是否成功
     */
    boolean restoreFileFromRecycleBin(Long fileId);


    /**
     * 根据文件编号从回收站删除文件文件
     * 如果全局参数设置realDelete为true，则永久删除文件,否则只是将文件记录标记为逻辑删除不会调用删除文件方法
     *
     * @param fileIds 文件id集合
     * @return 是否成功
     */
    boolean deleteTrashFileById(List<Long> fileIds);

    /**
     * 根据文件编号获取文件信息
     *
     * @param id 文件id
     * @return 文件信息
     */
    StorageFile getFileById(Long id);
}
