package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.constant.StorageConstants;
import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.model.entity.file.MinioConfigEntity;
import cn.zhangchuangla.storage.component.MinioStorageHandler;
import cn.zhangchuangla.storage.loader.SysFileConfigLoader;
import cn.zhangchuangla.storage.service.MinioOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
    private final MinioStorageHandler minioStorageHandler;

    @Autowired
    public MinioOperationServiceImpl(SysFileConfigLoader sysFileConfigLoader, MinioStorageHandler minioStorageHandler) {
        this.sysFileConfigLoader = sysFileConfigLoader;
        this.minioStorageHandler = minioStorageHandler;
    }

    @Override
    public FileTransferDto fileUpload(FileTransferDto fileTransferDto) {
        MinioConfigEntity minioConfig = sysFileConfigLoader.getMinioConfig();
        return minioStorageHandler.uploadFile(fileTransferDto, minioConfig);
    }

    /**
     * 上传图片
     *
     * @param fileTransferDto 文件传输对象
     * @return FileTransferDto
     */
    @Override
    public FileTransferDto imageUpload(FileTransferDto fileTransferDto) {
        return minioStorageHandler.uploadFile(fileTransferDto, sysFileConfigLoader.getMinioConfig());
    }

    /**
     * 删除文件
     *
     * @param fileTransferDto 文件传输对象
     * @param forceTrash      是否强制使用回收站，无视系统设置
     * @return 删除结果
     */
    @Override
    public boolean removeFile(FileTransferDto fileTransferDto, boolean forceTrash) {
        // 获取Minio配置
        MinioConfigEntity minioConfig = sysFileConfigLoader.getMinioConfig();

        // 决定是否使用回收站
        // 如果强制使用回收站，则无论系统设置如何都使用回收站
        // 否则，根据系统设置决定
        boolean enableTrash = forceTrash || (minioConfig.getEnableTrash() != null && StorageConstants.IS_TRASH.equals(minioConfig.getEnableTrash()));

        log.info("删除Minio文件：{}, 强制使用回收站: {}, 系统启用回收站: {}, 最终决定: {}",
                fileTransferDto.getOriginalName(),
                forceTrash,
                minioConfig.getEnableTrash(),
                enableTrash ? "移至回收站" : "永久删除");

        // 调用MinioComponent进行删除
        return minioStorageHandler.removeFile(fileTransferDto, minioConfig, enableTrash);
    }

    /**
     * 删除文件 - 使用系统默认回收站设置
     *
     * @param fileTransferDto 文件传输对象
     * @return 删除结果
     */
    @Override
    public boolean removeFile(FileTransferDto fileTransferDto) {
        return removeFile(fileTransferDto, false);
    }

    /**
     * 从回收站恢复文件
     *
     * @param fileTransferDto 文件传输对象
     * @return 恢复操作结果
     * @throws IOException IO异常
     */
    @Override
    public boolean recoverFile(FileTransferDto fileTransferDto) throws IOException {
        // 获取Minio配置
        MinioConfigEntity minioConfig = sysFileConfigLoader.getMinioConfig();

        // 调用MinioComponent进行恢复
        return minioStorageHandler.recoverFile(fileTransferDto, minioConfig);
    }
}
