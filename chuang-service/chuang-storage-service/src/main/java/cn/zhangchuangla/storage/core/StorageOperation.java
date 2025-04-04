package cn.zhangchuangla.storage.core;

import cn.zhangchuangla.storage.dto.FileTransferDto;

/**
 * 存储操作接口
 * 定义所有存储服务需要实现的基本方法
 *
 * @author Chuang
 * <p>
 * created on 2025/4/2 19:59
 */
public interface StorageOperation {

    /**
     * 保存文件
     *
     * @param fileTransferDto 文件传输对象
     * @return 文件传输结果
     */
    FileTransferDto save(FileTransferDto fileTransferDto);
}
