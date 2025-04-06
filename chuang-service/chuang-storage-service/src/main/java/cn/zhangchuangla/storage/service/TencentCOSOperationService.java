package cn.zhangchuangla.storage.service;

import cn.zhangchuangla.storage.core.StorageOperation;
import cn.zhangchuangla.storage.dto.FileTransferDto;

/**
 * 腾讯云COS操作接口
 *
 * @author Chuang
 * <p>
 * created on 2025/4/2 20:01
 */
public interface TencentCOSOperationService extends StorageOperation {

    /**
     * 保存文件
     *
     * @param fileTransferDto 文件传输对象
     * @return 文件传输对象
     */
    FileTransferDto save(FileTransferDto fileTransferDto);
}
