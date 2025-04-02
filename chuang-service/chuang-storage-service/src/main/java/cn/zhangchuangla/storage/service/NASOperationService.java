package cn.zhangchuangla.storage.service;

import cn.zhangchuangla.storage.core.StorageOperation;

/**
 * NAS文件操作服务
 *
 * @author Chuang
 * <p>
 * created on 2025/4/2 19:33
 */
public interface NASOperationService extends StorageOperation {
    /**
     * 获取NAS存储空间使用情况
     *
     * @return 已使用空间(字节)
     */
    long getUsedSpace();

    /**
     * 获取NAS存储挂载点
     *
     * @return 挂载点路径
     */
    String getMountPoint();
}
