package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.model.entity.file.TencentCOSConfigEntity;
import cn.zhangchuangla.storage.config.loader.SysFileConfigLoader;
import cn.zhangchuangla.storage.service.TencentCOSOperationService;
import cn.zhangchuangla.storage.utils.TencentCOSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return TencentCOSUtils.uploadFile(fileTransferDto, cosConfig);
    }

    @Override
    public FileTransferDto imageUpload(FileTransferDto fileTransferDto) {
        TencentCOSConfigEntity cosConfig = sysFileConfigLoader.getTencentCOSConfig();

        return TencentCOSUtils.imageUpload(fileTransferDto, cosConfig);
    }
}
