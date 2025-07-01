package cn.zhangchuangla.storage.service;

import cn.zhangchuangla.storage.model.entity.FileRecord;
import cn.zhangchuangla.storage.model.request.file.FileRecordQueryRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 文件管理接口，用于在文件上传时记录相关信息，并支持后续的文件管理操作
 *
 * @author Chuang
 */
public interface StorageManageService extends IService<FileRecord> {


    /**
     * 保存文件信息
     *
     * @param fileInfo 文件信息
     */
    void saveFileInfo(FileRecord fileInfo);

    /**
     * 获取文件列表
     *
     * @param request 查询条件
     * @return 文件信息
     */
    Page<FileRecord> listFileManage(FileRecordQueryRequest request);

    /**
     * 获取文件列表
     *
     * @param request 查询条件
     * @return 文件信息
     */
    Page<FileRecord> listFileTrashManage(FileRecordQueryRequest request);
}
