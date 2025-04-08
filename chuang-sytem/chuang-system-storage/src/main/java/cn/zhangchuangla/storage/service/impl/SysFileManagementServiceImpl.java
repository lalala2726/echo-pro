package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.common.utils.file.LocalStorageUtils;
import cn.zhangchuangla.storage.mapper.SysFileManagementMapper;
import cn.zhangchuangla.storage.model.entity.SysFileManagement;
import cn.zhangchuangla.storage.model.request.manage.SysFileManagementListRequest;
import cn.zhangchuangla.storage.service.SysFileManagementService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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
    private final AppConfig appConfig;


    public SysFileManagementServiceImpl(SysFileManagementMapper sysFileManagementMapper, AppConfig appConfig) {
        this.sysFileManagementMapper = sysFileManagementMapper;
        this.appConfig = appConfig;
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
                .previewImageUrl(fileTransferDto.getPreviewImageUrl())
                .previewImagePath(fileTransferDto.getPreviewImagePath())
                .fileExtension(fileTransferDto.getFileExtension())
                .storageType(fileTransferDto.getStorageType())
                .bucketName(fileTransferDto.getBucketName())
                .uploaderId(userId)
                .uploaderName(userName)
                .uploadTime(new Date())
                .build();
        save(sysFileManagement);
    }

    /**
     * 查询文件列表
     *
     * @param request 查询参数
     * @return 分页结果
     */
    @Override
    public Page<SysFileManagement> listFileManage(SysFileManagementListRequest request) {
        Page<SysFileManagement> sysFileManagementPage = new Page<>(request.getPageNum(), request.getPageSize());
        return sysFileManagementMapper.listFileManage(sysFileManagementPage, request);
    }

    /**
     * 删除文件
     *
     * @param ids      文件id列表
     * @param isDelete true代表文件，false将会转移到回收站
     * @return 操作结果
     */
    @Override
    public boolean removeFile(List<Long> ids, Boolean isDelete) {
        ids.forEach(id -> {
            SysFileManagement fileManageById = getFileManageById(id);
            if (fileManageById == null) {
                throw new ServiceException(ResponseCode.RESULT_IS_NULL, "ID:" + id + "的文件不存在！");
            }
            FileTransferDto fileTransferDto = new FileTransferDto();
            BeanUtils.copyProperties(fileManageById, fileTransferDto);
            boolean deleteResult = LocalStorageUtils.removeFile(appConfig.getUploadPath(), fileTransferDto, isDelete);
            //删除数据库中的文件信息
            if (deleteResult) {
                removeById(id);
            }
        });
        return true;
    }

    /**
     * 根据id查询文件信息
     *
     * @param id 文件id
     * @return 文件信息
     */
    @Override
    public SysFileManagement getFileManageById(Long id) {
        return getById(id);
    }
}




