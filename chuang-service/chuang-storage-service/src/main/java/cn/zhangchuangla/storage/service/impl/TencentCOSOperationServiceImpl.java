package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.storage.service.TencentCOSOperationService;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/2 20:03
 */
public class TencentCOSOperationServiceImpl implements TencentCOSOperationService {
    @Override
    public boolean save(String fileName, byte[] bytes) {
        return false;
    }

    @Override
    public byte[] load(String fileName) {
        return new byte[0];
    }

    @Override
    public boolean delete(String fileName) {
        return false;
    }

    @Override
    public boolean update(String fileName, byte[] bytes) {
        return false;
    }

    @Override
    public boolean exists(String fileName) {
        return false;
    }
}
