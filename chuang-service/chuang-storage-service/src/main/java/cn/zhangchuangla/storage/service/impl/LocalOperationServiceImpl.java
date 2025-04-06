package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.storage.dto.FileTransferDto;
import cn.zhangchuangla.storage.service.LocalOperationService;
import cn.zhangchuangla.storage.utils.LocalStorageUtils;
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
}
