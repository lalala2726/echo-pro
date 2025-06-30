package cn.zhangchuangla.storage.service;

import cn.zhangchuangla.storage.model.entity.FileRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 文件管理接口，用于在文件上传时记录相关信息，并支持后续的文件管理操作
 *
 * @author Chuang
 */
public interface StorageManageService extends IService<FileRecord> {

    boolean saveFileInfo(FileRecord fileInfo);



}
