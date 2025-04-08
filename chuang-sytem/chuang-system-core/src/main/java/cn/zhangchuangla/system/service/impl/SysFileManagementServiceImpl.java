package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.system.mapper.SysFileManagementMapper;
import cn.zhangchuangla.system.model.entity.SysFileManagement;
import cn.zhangchuangla.system.model.request.file.manage.SysFileManagementListRequest;
import cn.zhangchuangla.system.service.SysFileManagementService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
public class SysFileManagementServiceImpl extends ServiceImpl<SysFileManagementMapper, SysFileManagement>
        implements SysFileManagementService {

    private final SysFileManagementMapper sysFileManagementMapper;

    public SysFileManagementServiceImpl(SysFileManagementMapper sysFileManagementMapper) {
        this.sysFileManagementMapper = sysFileManagementMapper;
    }

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
                .previewImageUrl(fileTransferDto.getCompressedFileUrl())
                .previewImagePath(fileTransferDto.getCompressedRelativePath())
                .fileExtension(fileTransferDto.getFileExtension())
                .storageType(fileTransferDto.getStorageType())
                .bucketName(fileTransferDto.getBucketName())
                .uploaderId(userId)
                .uploaderName(userName)
                .uploadTime(new Date())
                .build();
        save(sysFileManagement);
    }

    @Override
    public Page<SysFileManagement> listFileManage(SysFileManagementListRequest request) {
        Page<SysFileManagement> sysFileManagementPage = new Page<>(request.getPageNum(), request.getPageSize());
        return sysFileManagementMapper.listFileManage(sysFileManagementPage, request);
    }
}




