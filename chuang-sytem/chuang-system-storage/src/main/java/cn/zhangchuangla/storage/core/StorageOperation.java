package cn.zhangchuangla.storage.core;

import cn.zhangchuangla.common.model.dto.FileTransferDto;

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
     * 文件上传接口，直接上传图片并返回文件URL
     *
     * @param fileTransferDto 文件传输对象
     * @return 文件传输结果
     */
    FileTransferDto fileUpload(FileTransferDto fileTransferDto);

    /**
     * 图片上传接口，返回两个URL，一个原始图片URL，一个压缩图片URL
     *
     * @param fileTransferDto 文件传输对象
     * @return 文件传输结果
     */
    FileTransferDto imageUpload(FileTransferDto fileTransferDto);
}
