package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.storage.service.NASOperationService;
import org.springframework.stereotype.Service;

/**
 * NAS存储操作实现类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/2 19:35
 */
@Service
public class NASOperationServiceImpl implements NASOperationService {

    @Override
    public long getUsedSpace() {
        return 0;
    }

    @Override
    public String getMountPoint() {
        return "";
    }

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
