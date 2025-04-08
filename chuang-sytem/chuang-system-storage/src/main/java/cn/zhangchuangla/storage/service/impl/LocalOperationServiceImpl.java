package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.utils.file.LocalStorageUtils;
import cn.zhangchuangla.storage.service.LocalOperationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 本地存储服务实现类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/2 20:42
 */
@Service
@Slf4j
public class LocalOperationServiceImpl implements LocalOperationService {

    @Resource(name = "appConfig")
    private AppConfig appConfig;

    @Override
    public FileTransferDto fileUpload(FileTransferDto fileTransferDto) {

        String uploadPath = appConfig.getUploadPath();
        String fileDomain;
        try {
            fileDomain = appConfig.getFileDomain();
        } catch (NullPointerException e) {
            // 文件域名不是必填项，可以不配置，默认为空字符串
            fileDomain = "";
        }
        return LocalStorageUtils.uploadFile(fileTransferDto, uploadPath, fileDomain);
    }

    /**
     * 图片上传
     *
     * @param fileTransferDto 文件传输对象
     * @return 文件信息
     */
    @Override
    public FileTransferDto imageUpload(FileTransferDto fileTransferDto) {
        String uploadPath = appConfig.getUploadPath();
        String fileDomain;
        try {
            fileDomain = appConfig.getFileDomain();
        } catch (NullPointerException e) {
            // 文件域名不是必填项，可以不配置，默认为空字符串
            fileDomain = "";
        }
        return LocalStorageUtils.imageUpload(fileTransferDto, uploadPath, fileDomain);
    }

    /**
     * 删除文件
     *
     * @param fileTransferDto 文件传输对象
     * @param forceTrash 是否强制使用回收站，无视系统设置
     * @return 文件操作结果
     */
    @Override
    public boolean removeFile(FileTransferDto fileTransferDto, boolean forceTrash) {
        // 决定是否使用回收站
        // 如果强制使用回收站，则无论系统设置如何都使用回收站
        // 否则，根据系统设置决定
        boolean enableTrash = forceTrash || appConfig.isEnableTrash();

        log.info("删除文件：{}, 强制使用回收站: {}, 系统启用回收站: {}, 最终决定: {}",
                fileTransferDto.getOriginalName(),
                forceTrash,
                appConfig.isEnableTrash(),
                enableTrash ? "移至回收站" : "永久删除");

        // 调用LocalStorageUtils的删除方法
        return LocalStorageUtils.removeFile(appConfig.getUploadPath(), fileTransferDto, enableTrash);
    }

    /**
     * 删除文件 - 兼容旧接口，使用系统默认回收站设置
     *
     * @param fileTransferDto 文件传输对象
     * @return 文件操作结果
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
        // 调用LocalStorageUtils的恢复方法
        return LocalStorageUtils.recoverFile(appConfig.getUploadPath(), fileTransferDto);
    }
}
