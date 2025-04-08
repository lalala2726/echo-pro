package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.system.mapper.FileManagementMapper;
import cn.zhangchuangla.system.model.entity.SysFileManagement;
import cn.zhangchuangla.system.service.FileManagementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 文件管理服务实现类
 *
 * @author zhangchuang
 */
@Service
@Slf4j
public class FileManagementServiceImpl extends ServiceImpl<FileManagementMapper, SysFileManagement>
        implements FileManagementService {

    /**
     * 保存文件信息
     *
     * @param fileTransferDto 文件上传结果
     */
    @Override
    public void saveFileInfo(FileTransferDto fileTransferDto) {
        Long userId = SecurityUtils.getUserId();
        String userName = SecurityUtils.getUsername();
        SysFileManagement sysFileManagement = SysFileManagement.builder()
                .originalName(fileTransferDto.getOriginalName())
                .contentType(fileTransferDto.getContentType())
                .fileSize(fileTransferDto.getFileSize())
                .fileMd5(fileTransferDto.getFileMd5())
                .originalFileUrl(fileTransferDto.getOriginalFileUrl())
                .originalRelativePath(fileTransferDto.getOriginalRelativePath())
                .compressedFileUrl(fileTransferDto.getCompressedFileUrl())
                .compressedRelativePath(fileTransferDto.getCompressedRelativePath())
                .fileExtension(fileTransferDto.getFileExtension())
                .storageType(fileTransferDto.getStorageType())
                .bucketName(fileTransferDto.getBucketName())
                .uploaderId(userId)
                .uploaderName(userName)
                .uploadTime(new Date())
                .build();
        save(sysFileManagement);
    }
}




