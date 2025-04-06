package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.model.entity.file.MinioConfigEntity;
import cn.zhangchuangla.storage.config.loader.SysFileConfigLoader;
import cn.zhangchuangla.storage.dto.FileTransferDto;
import cn.zhangchuangla.storage.service.MinioOperationService;
import cn.zhangchuangla.storage.utils.MinioUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Minio 操作服务实现类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/2 20:03
 */
@Service
@Slf4j
public class MinioOperationServiceImpl implements MinioOperationService {

    private final SysFileConfigLoader sysFileConfigLoader;

    @Autowired
    public MinioOperationServiceImpl(SysFileConfigLoader sysFileConfigLoader) {
        this.sysFileConfigLoader = sysFileConfigLoader;
    }

    @Override
    public FileTransferDto fileUpload(FileTransferDto fileTransferDto) {
        MinioConfigEntity minioConfig = sysFileConfigLoader.getMinioConfig();
        return MinioUtils.uploadFile(fileTransferDto, minioConfig);
    }

    /**
     * 上传图片
     *
     * @param fileTransferDto 文件传输对象
     * @return FileTransferDto
     */
    @Override
    public FileTransferDto imageUpload(FileTransferDto fileTransferDto) {
        return MinioUtils.uploadFile(fileTransferDto, sysFileConfigLoader.getMinioConfig());
    }
}
