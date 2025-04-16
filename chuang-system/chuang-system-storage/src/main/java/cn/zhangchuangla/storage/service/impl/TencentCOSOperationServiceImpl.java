package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.constant.StorageConstants;
import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.model.entity.file.TencentCOSConfigEntity;
import cn.zhangchuangla.storage.component.TencentCOSHandler;
import cn.zhangchuangla.storage.loader.SysFileConfigLoader;
import cn.zhangchuangla.storage.service.TencentCOSOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class TencentCOSOperationServiceImpl implements TencentCOSOperationService {

    private final SysFileConfigLoader sysFileConfigLoader;


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
        // 获取腾讯云COS配置
        TencentCOSConfigEntity cosConfig = sysFileConfigLoader.getTencentCOSConfig();

        // 决定是否使用回收站
        // 如果强制使用回收站，则无论系统设置如何都使用回收站
        // 否则，根据系统设置决定
        boolean enableTrash = forceTrash || (cosConfig.getEnableTrash() != null &&
                StorageConstants.IS_TRASH.equals(cosConfig.getEnableTrash()));

        log.info("删除腾讯云COS文件：{}, 强制使用回收站: {}, 系统启用回收站: {}, 最终决定: {}",
                fileTransferDto.getOriginalName(),
                forceTrash,
                cosConfig.getEnableTrash(),
                enableTrash ? "移至回收站" : "永久删除");

        // 调用TencentCOSHandler的删除方法
        return TencentCOSHandler.removeFile(fileTransferDto, cosConfig, enableTrash);
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
        // 获取腾讯云COS配置
        TencentCOSConfigEntity cosConfig = sysFileConfigLoader.getTencentCOSConfig();

        // 调用TencentCOSHandler的恢复方法
        return TencentCOSHandler.recoverFile(fileTransferDto, cosConfig);
    }
}
