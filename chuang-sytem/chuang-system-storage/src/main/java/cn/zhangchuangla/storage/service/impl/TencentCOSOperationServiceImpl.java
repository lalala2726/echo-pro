package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.common.constant.StorageConstants;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.model.entity.file.TencentCOSConfigEntity;
import cn.zhangchuangla.storage.config.loader.SysFileConfigLoader;
import cn.zhangchuangla.storage.service.TencentCOSOperationService;
import cn.zhangchuangla.storage.utils.TencentCOSUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
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

    private AppConfig appConfig;

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

    /**
     * 删除文件
     *
     * @param fileTransferDto 文件传输对象
     * @param isDelete        如果是true，则直接删除文件，如果是false，则将文件放入回收站
     * @return 操作结果
     */
    @Override
    public boolean removeFile(FileTransferDto fileTransferDto, boolean isDelete) {
        // 获取上传路径
        String uploadPath = appConfig.getUploadPath();
        //获取压缩图片相对路径
        String compressedRelativePath = fileTransferDto.getCompressedRelativePath();
        // 获取回收站路径
        String targetDir = uploadPath + File.separator + StorageConstants.TRASH_DIR;
        //获取原始文件相对路径
        String originalFilePath = uploadPath + File.separator + fileTransferDto.getOriginalRelativePath();

        try {
            if (isDelete) {
                // 移动原文件到回收站
                FileUtils.moveFile(new File(originalFilePath), new File(targetDir));
                // 只有图片资源才有压缩图片，其他资源没有压缩图片
                if (compressedRelativePath != null) {
                    String compressedFilePath = uploadPath + File.separator + compressedRelativePath;
                    FileUtils.moveFile(new File(compressedFilePath), new File(targetDir));
                }
            } else {
                // 删除原始文件
                FileUtils.delete(new File(originalFilePath));
                if (compressedRelativePath != null) {
                    String compressedFilePath = uploadPath + File.separator + compressedRelativePath;
                    FileUtils.delete(new File(compressedFilePath));
                }
            }
        } catch (IOException e) {
            log.error("文件操作失败: {}", e.getMessage(), e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件操作失败！");
        }
        return true;
    }
}
