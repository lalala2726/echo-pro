package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.common.constant.StorageConstants;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.exception.ParamException;
import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.storage.core.StorageOperation;
import cn.zhangchuangla.storage.factory.StorageFactory;
import cn.zhangchuangla.storage.mapper.SysFileManagementMapper;
import cn.zhangchuangla.storage.model.entity.SysFileManagement;
import cn.zhangchuangla.storage.model.request.manage.SysFileManagementListRequest;
import cn.zhangchuangla.storage.service.SysFileManagementService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    private final StorageFactory storageFactory;
    private final AppConfig appConfig;


    public SysFileManagementServiceImpl(SysFileManagementMapper sysFileManagementMapper, StorageFactory storageFactory, AppConfig appConfig) {
        this.sysFileManagementMapper = sysFileManagementMapper;
        this.storageFactory = storageFactory;
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
     * @param isDelete true表示移动到回收站，false表示直接删除
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeFile(List<Long> ids, Boolean isDelete) {
        if (ids == null || ids.isEmpty()) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "文件ID不能为空");
        }

        for (Long id : ids) {
            SysFileManagement fileManagement = getById(id);
            if (fileManagement == null) {
                throw new FileException(ResponseCode.FILE_OPERATION_ERROR, "文件不存在！");
            }

            // 如果是移动到回收站，先更新数据库标记
            if (isDelete) {
                updateFileTrashStatus(id);
            }

            try {
                // 构建文件传输对象
                FileTransferDto fileTransferDto = convertToFileTransferDto(fileManagement);

                // 获取对应的存储操作实现
                StorageOperation storageOperation = storageFactory.getStorageOperation(fileManagement.getStorageType());

                // 执行文件删除操作
                boolean deleteResult = storageOperation.removeFile(fileTransferDto, isDelete);

                // 删除成功后，从数据库中移除记录
                if (deleteResult) {
                    removeById(id);
                    log.info("文件已{}：ID={}, 名称={}", isDelete ? "移至回收站" : "删除", id, fileManagement.getOriginalName());
                } else {
                    log.warn("文件{}操作失败：ID={}", isDelete ? "移至回收站" : "删除", id);
                    throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件" + (isDelete ? "移至回收站" : "删除") + "操作失败");
                }
            } catch (IOException e) {
                log.error("文件处理出现IO异常：ID={}, 错误信息={}", id, e.getMessage(), e);
                throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件删除失败：" + e.getMessage());
            } catch (Exception e) {
                log.error("文件删除发生未预期异常：ID={}, 错误信息={}", id, e.getMessage(), e);
                throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件删除出现异常：" + e.getMessage());
            }
        }

        return true;
    }

    /**
     * 更新文件的回收站状态
     *
     * @param id 文件ID
     */
    private void updateFileTrashStatus(Long id) {
        SysFileManagement sysFileManagement = SysFileManagement.builder()
                .id(id)
                .isTrash(StorageConstants.IS_TRASH)
                .build();
        if (!updateById(sysFileManagement)) {
            log.warn("更新文件回收站状态失败，ID: {}", id);
        }
    }

    /**
     * 将文件管理实体转换为文件传输DTO
     *
     * @param fileManagement 文件管理实体
     * @return 文件传输DTO
     */
    private FileTransferDto convertToFileTransferDto(SysFileManagement fileManagement) {
        FileTransferDto fileTransferDto = new FileTransferDto();
        BeanUtils.copyProperties(fileManagement, fileTransferDto);
        return fileTransferDto;
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




