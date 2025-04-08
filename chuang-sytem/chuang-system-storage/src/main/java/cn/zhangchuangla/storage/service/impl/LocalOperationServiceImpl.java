package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.utils.file.LocalStorageUtils;
import cn.zhangchuangla.storage.service.LocalOperationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
     * 默认会将文件传入回收站，也可以直接删除
     *
     * @param fileTransferDto 文件传输对象
     * @param isDelete        如果是true，则直接删除文件，如果是false，则将文件放入回收站
     * @return 文件操作结果
     */
    @Override
    public boolean removeFile(FileTransferDto fileTransferDto, boolean isDelete) {
        return LocalStorageUtils.removeFile(appConfig.getUploadPath(), fileTransferDto, isDelete);
    }
}
