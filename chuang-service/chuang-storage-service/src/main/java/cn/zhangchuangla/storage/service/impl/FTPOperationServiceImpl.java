package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.storage.service.FTPOperationService;
import org.springframework.stereotype.Service;

/**
 * FTP存储操作实现类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/2
 */
@Service
public class FTPOperationServiceImpl implements FTPOperationService {

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
