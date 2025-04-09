package cn.zhangchuangla.storage.core;

import cn.zhangchuangla.common.model.dto.FileTransferDto;

import java.io.IOException;

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
     * 文件上传接口，直接删除文件
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


    /**
     * 删除文件
     *
     * @param fileTransferDto 文件传输对象
     * @param forceTrash      是否强制使用回收站，无视系统设置
     * @return 文件操作结果
     */
    boolean removeFile(FileTransferDto fileTransferDto, boolean forceTrash);

    /**
     * 删除文件 - 兼容旧接口，使用系统默认回收站设置
     *
     * @param fileTransferDto 文件传输对象
     * @return 文件操作结果
     */
    default boolean removeFile(FileTransferDto fileTransferDto) {
        return removeFile(fileTransferDto, false);
    }

    /**
     * 恢复文件
     *
     * @param fileTransferDto 文件传输对象
     * @return 文件操作结果
     * @throws IOException IO异常
     */
    boolean recoverFile(FileTransferDto fileTransferDto) throws IOException;
}
