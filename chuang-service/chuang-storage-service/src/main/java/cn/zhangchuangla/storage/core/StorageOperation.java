package cn.zhangchuangla.storage.core;

import cn.zhangchuangla.storage.entity.FileTransferDto;

/**
 * 文件存储操作基础接口
 * 定义所有存储实现必须实现的标准方法
 *
 * @author Chuang
 * <p>
 * created on 2025/4/2
 */
public interface StorageOperation {

    FileTransferDto save(FileTransferDto fileTransferDto);


}
