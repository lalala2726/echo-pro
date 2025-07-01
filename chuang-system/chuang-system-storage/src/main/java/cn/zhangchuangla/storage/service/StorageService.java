package cn.zhangchuangla.storage.service;

import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import cn.zhangchuangla.storage.model.entity.FileRecord;
import cn.zhangchuangla.storage.model.request.file.FileRecordQueryRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/1 02:49
 */
public interface StorageService {

    /**
     * 获取文件列表
     *
     * @param request 查询参数
     * @return 文件列表
     */
    Page<FileRecord> listFileManage(FileRecordQueryRequest request);

    /**
     * 获取回收站文件列表
     *
     * @param request 文件列表查询参数
     * @return 文件列表
     */
    Page<FileRecord> listFileTrashManage(FileRecordQueryRequest request);

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
    boolean deleteFileById(Long id, boolean forceDelete);

    /**
     * 批量删除文件
     *
     * @param ids 文件id
     * @return 是否成功
     */
    boolean deleteFileById(List<Long> ids, boolean forceDelete);


    /**
     * 根据文件编号从回收站还原文件
     *
     * @param fileId 文件id
     * @return 是否成功
     */
    boolean restoreFileFromRecycleBin(Long fileId);

    /**
     * 根据文件编号列表从回收站批量还原文件
     *
     * @param fileIds 文件id列表
     * @return 是否成功
     */
    boolean restoreFileFromRecycleBin(List<Long> fileIds);

}
