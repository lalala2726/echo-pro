package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.model.entity.file.TencentCOSConfigEntity;
import cn.zhangchuangla.storage.component.TencentCOSHandler;
import cn.zhangchuangla.storage.loader.SysFileConfigLoader;
import cn.zhangchuangla.storage.service.TencentCOSOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 腾讯云COS 操作服务实现类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/2 20:03
 */
@Service
@Slf4j
public class TencentCOSOperationServiceImpl implements TencentCOSOperationService {

    private final SysFileConfigLoader sysFileConfigLoader;

    @Autowired
    public TencentCOSOperationServiceImpl(SysFileConfigLoader sysFileConfigLoader) {
        this.sysFileConfigLoader = sysFileConfigLoader;
    }

    @Override
    public FileTransferDto fileUpload(FileTransferDto fileTransferDto) {
        TencentCOSConfigEntity cosConfig = sysFileConfigLoader.getTencentCOSConfig();
        return TencentCOSHandler.uploadFile(fileTransferDto, cosConfig);
    }

    @Override
    public FileTransferDto imageUpload(FileTransferDto fileTransferDto) {
        TencentCOSConfigEntity cosConfig = sysFileConfigLoader.getTencentCOSConfig();

        return TencentCOSHandler.imageUpload(fileTransferDto, cosConfig);
    }

    @Override
    public boolean removeFile(FileTransferDto fileTransferDto, boolean forceTrash) {
        return false;
    }

    @Override
    public boolean removeFile(FileTransferDto fileTransferDto) {
        return false;
    }

    @Override
    public boolean recoverFile(FileTransferDto fileTransferDto) throws IOException {
        return false;
    }

}
