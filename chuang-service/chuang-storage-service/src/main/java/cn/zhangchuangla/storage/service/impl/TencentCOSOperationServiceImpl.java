package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.model.entity.file.TencentCOSConfigEntity;
import cn.zhangchuangla.common.utils.ImageUtils;
import cn.zhangchuangla.storage.config.loader.SysFileConfigLoader;
import cn.zhangchuangla.storage.dto.FileTransferDto;
import cn.zhangchuangla.storage.service.TencentCOSOperationService;
import cn.zhangchuangla.storage.utils.TencentCOSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 腾讯云COS 操作服务实现类
 *
 * @author Chuang
 *         <p>
 *         created on 2025/4/2 20:03
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

        try {
            // 保存原始文件信息
            byte[] originalData = fileTransferDto.getBytes();
            String originalFileName = fileTransferDto.getFileName();
            String originalFileKey = "images/original/" + originalFileName;

            // 创建压缩版图片
            byte[] compressedData = ImageUtils.compressImage(originalData, 800, 800, 0.7f);
            String compressedFileKey = "images/compressed/" + originalFileName;

            // 上传原始图片
            FileTransferDto originalUpload = new FileTransferDto();
            originalUpload.setBytes(originalData);
            originalUpload.setFileName(originalFileKey);
            FileTransferDto originalResult = TencentCOSUtils.uploadFile(originalUpload, cosConfig);

            // 上传压缩图片
            FileTransferDto compressedUpload = new FileTransferDto();
            compressedUpload.setBytes(compressedData);
            compressedUpload.setFileName(compressedFileKey);
            FileTransferDto compressedResult = TencentCOSUtils.uploadFile(compressedUpload, cosConfig);

            // 返回合并结果
            return FileTransferDto.builder()
                    .originalFileUrl(compressedResult.getOriginalFileUrl()) // 默认使用压缩后的图片URL
                    .originalRelativePath(compressedResult.getOriginalRelativePath())
                    .originalFileUrl(originalResult.getOriginalFileUrl())
                    .originalRelativePath(originalResult.getOriginalRelativePath())
                    .build();

        } catch (IOException e) {
            log.error("图片处理失败", e);
            // 如果压缩失败，回退到普通文件上传
            log.warn("图片压缩失败，回退到普通文件上传");
            return fileUpload(fileTransferDto);
        }
    }
}
