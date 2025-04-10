package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.constant.StorageConstants;
import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.model.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.storage.component.AliyunOSSStorageHandler;
import cn.zhangchuangla.storage.loader.SysFileConfigLoader;
import cn.zhangchuangla.storage.service.AliyunOssOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 阿里云OSS 操作服务实现类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/2 20:03
 */
@Service
@Slf4j
public class AliyunOssOperationServiceImpl implements AliyunOssOperationService {

    private final SysFileConfigLoader sysFileConfigLoader;

    @Autowired
    public AliyunOssOperationServiceImpl(SysFileConfigLoader sysFileConfigLoader) {
        this.sysFileConfigLoader = sysFileConfigLoader;
    }

    @Override
    public FileTransferDto fileUpload(FileTransferDto fileTransferDto) {
        AliyunOSSConfigEntity aliyunOSSConfig = sysFileConfigLoader.getAliyunOSSConfig();
        return AliyunOSSStorageHandler.uploadFile(fileTransferDto, aliyunOSSConfig);
    }

    /**
     * 上传图片
     *
     * @param fileTransferDto 文件传输对象
     * @return FileTransferDto
     */
    @Override
    public FileTransferDto imageUpload(FileTransferDto fileTransferDto) {
        return AliyunOSSStorageHandler.imageUpload(fileTransferDto, sysFileConfigLoader.getAliyunOSSConfig());
    }

    /**
     * 删除文件
     * 根据参数决定是移动到回收站还是直接删除
     *
     * @param fileTransferDto 文件传输对象
     * @param forceTrash      是否强制使用回收站，无视系统设置
     * @return 删除操作的结果
     */
    @Override
    public boolean removeFile(FileTransferDto fileTransferDto, boolean forceTrash) {
        // 获取阿里云OSS配置
        AliyunOSSConfigEntity ossConfig = sysFileConfigLoader.getAliyunOSSConfig();

        // 决定是否使用回收站
        // 如果强制使用回收站，则无论系统设置如何都使用回收站
        // 否则，根据系统设置决定
        boolean enableTrash = forceTrash || (ossConfig.getEnableTrash() != null &&
                StorageConstants.IS_TRASH.equals(ossConfig.getEnableTrash()));

        log.info("删除阿里云OSS文件：{}, 强制使用回收站: {}, 系统启用回收站: {}, 最终决定: {}",
                fileTransferDto.getOriginalName(),
                forceTrash,
                ossConfig.getEnableTrash(),
                enableTrash ? "移至回收站" : "永久删除");

        // 调用AliyunOSSStorageHandler进行删除
        return AliyunOSSStorageHandler.removeFile(fileTransferDto, ossConfig, enableTrash);
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
        // 获取阿里云OSS配置
        AliyunOSSConfigEntity ossConfig = sysFileConfigLoader.getAliyunOSSConfig();

        // 调用AliyunOSSStorageHandler进行恢复
        return AliyunOSSStorageHandler.recoverFile(fileTransferDto, ossConfig);
    }
}
