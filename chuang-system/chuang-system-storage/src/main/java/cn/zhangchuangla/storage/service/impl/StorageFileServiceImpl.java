package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.constant.StorageConstants;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.exception.ParamException;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.storage.FileInfo;
import cn.zhangchuangla.storage.config.StorageSystemProperties;
import cn.zhangchuangla.storage.config.TrashConfigurable;
import cn.zhangchuangla.storage.core.StorageManager;
import cn.zhangchuangla.storage.core.StorageService;
import cn.zhangchuangla.storage.mapper.SysFileMapper;
import cn.zhangchuangla.storage.model.entity.SysFile;
import cn.zhangchuangla.storage.model.request.file.SysFileListRequest;
import cn.zhangchuangla.storage.service.StorageFileService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 文件管理服务实现类
 *
 * @author Chuang
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StorageFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile>
        implements StorageFileService {

    private final SysFileMapper sysFileMapper;
    private final StorageManager storageManager;

    /**
     * 获取当前激活存储的 rootPathOrBucketName
     */
    private String getCurrentBucketName() {
        Object specificProps = storageManager.getActiveStorageSpecificProperties();
        if (specificProps instanceof StorageSystemProperties.LocalConfig) {
            return ((StorageSystemProperties.LocalConfig) specificProps).getRootPathOrBucketName();
        } else if (specificProps instanceof StorageSystemProperties.MinioConfig) {
            return ((StorageSystemProperties.MinioConfig) specificProps).getRootPathOrBucketName();
        } else if (specificProps instanceof StorageSystemProperties.AliyunOssConfig) {
            return ((StorageSystemProperties.AliyunOssConfig) specificProps).getRootPathOrBucketName();
        } else if (specificProps instanceof StorageSystemProperties.TencentCosConfig) {
            return ((StorageSystemProperties.TencentCosConfig) specificProps).getRootPathOrBucketName();
        }
        log.warn("无法确定当前激活存储的 bucketName/rootPath。");
        return null; // 或者抛出异常
    }

    /**
     * 保存文件信息
     *
     * @param fileInfo 文件信息
     */
    @Override
    public void saveFileInfo(FileInfo fileInfo) {
        if (fileInfo == null) {
            log.error("Attempted to save null FileInfo.");
            throw new ParamException(ResponseCode.PARAM_ERROR, "文件信息不能为空");
        }
        Long userId = SecurityUtils.getUserId();
        String userName = SecurityUtils.getUsername();

        String bucketName = getCurrentBucketName();

        SysFile sysFile = SysFile.builder()
                .originalName(fileInfo.getOriginalFileName())
                .newFileName(fileInfo.getNewFileName())
                .contentType(fileInfo.getContentType())
                .fileSize(fileInfo.getSize())
                .originalFileUrl(fileInfo.getUrl())
                .originalRelativePath(fileInfo.getRelativePath())
                .previewImageUrl(fileInfo.getThumbnailUrl())
                .previewImagePath(fileInfo.getThumbnailPath())
                .storageType(fileInfo.getStorageType().getCode())
                .bucketName(bucketName)
                .uploaderId(userId)
                .uploaderName(userName)
                .uploadTime(new Date())
                .isTrash(StorageConstants.IS_NOT_DELETED)
                .build();
        save(sysFile);
        log.info("Saved file info to DB: {}, Relative Path: {}", sysFile.getOriginalName(), sysFile.getOriginalRelativePath());
    }

    /**
     * 查询文件列表
     *
     * @param request 查询参数
     * @return 分页结果
     */
    @Override
    public Page<SysFile> listFileManage(SysFileListRequest request) {
        Page<SysFile> sysFileManagementPage = new Page<>(request.getPageNum(), request.getPageSize());
        return sysFileMapper.listFileManage(sysFileManagementPage, request);
    }

    /**
     * 删除文件
     *
     * @param ids           文件id列表
     * @param isPermanently true代表永久删除文件，false将会转移到回收站
     * @return 操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeFile(List<Long> ids, Boolean isPermanently) {
        if (ids == null || ids.isEmpty()) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "文件ID不能为空");
        }

        StorageService activeService = storageManager.getActiveStorageService();
        Object specificProps = storageManager.getActiveStorageSpecificProperties();
        TrashConfigurable trashConfig = null;
        if (specificProps instanceof TrashConfigurable) {
            trashConfig = (TrashConfigurable) specificProps;
        } else {
            log.error("当前激活的存储配置不支持回收站功能 (未实现TrashConfigurable)。 specificProps: {}", specificProps != null ? specificProps.getClass().getName() : "null");
            // 根据业务决定是抛异常还是认为回收站禁用
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "当前存储配置不支持回收站。");
        }

        for (Long id : ids) {
            SysFile sysFile = getFileManageById(id);
            if (sysFile == null) {
                log.warn("File not found or already in trash, ID: {}", id);
                throw new FileException(ResponseCode.RESULT_IS_NULL, "文件不存在或已在回收站中，ID: " + id);
            }

            try {
                if (Boolean.FALSE.equals(isPermanently)) {
                    if (trashConfig.isEnableTrash()) {
                        log.warn("Trash is not enabled, but soft delete requested for file ID: {}. Will proceed with permanent delete from storage.", id);
                        boolean deleted = activeService.deleteFile(sysFile.getOriginalRelativePath());
                        if (sysFile.getPreviewImagePath() != null) {
                            activeService.deleteFile(sysFile.getPreviewImagePath());
                        }
                        if (deleted) {
                            removeById(id);
                            log.info("File permanently deleted (trash disabled): ID={}, Name={}", id, sysFile.getOriginalName());
                        } else {
                            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件从存储中永久删除失败 (回收站禁用)");
                        }
                    } else {
                        FileInfo trashInfo = activeService.moveToTrash(sysFile.getOriginalRelativePath());
                        if (trashInfo != null && StringUtils.hasText(trashInfo.getOriginalTrashPath())) {
                            updateFileTrashStatus(id, trashInfo.getOriginalTrashPath(), trashInfo.getThumbnailTrashPath());
                            log.info("File moved to trash: ID={}, Name={}, TrashPath={}", id, sysFile.getOriginalName(), trashInfo.getOriginalTrashPath());
                            if (StringUtils.hasText(sysFile.getPreviewImagePath())) {
                                FileInfo previewTrashInfo = activeService.moveToTrash(sysFile.getPreviewImagePath());
                                if (previewTrashInfo != null && StringUtils.hasText(previewTrashInfo.getOriginalTrashPath())) {
                                    log.info("Preview image moved to trash for file ID: {}, PreviewTrashPath={}", id, previewTrashInfo.getOriginalTrashPath());
                                    SysFile updatedFile = getById(id);
                                    updatedFile.setPreviewTrashPath(previewTrashInfo.getOriginalTrashPath());
                                    updateById(updatedFile);
                                }
                            }
                        } else {
                            log.warn("Failed to move file to trash or trash path not returned, ID: {}", id);
                            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件移至回收站失败");
                        }
                    }
                } else {
                    boolean deleted = activeService.deleteFile(sysFile.getOriginalRelativePath());
                    if (StringUtils.hasText(sysFile.getPreviewImagePath())) {
                        activeService.deleteFile(sysFile.getPreviewImagePath());
                    }
                    if (deleted) {
                        removeById(id);
                        log.info("File permanently deleted: ID={}, Name={}", id, sysFile.getOriginalName());
                    } else {
                        log.warn("Permanent delete failed for file ID: {} from storage.", id);
                        throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件永久删除失败");
                    }
                }
            } catch (Exception e) {
                log.error("File operation exception: ID={}, Error: {}", id, e.getMessage(), e);
                if (e instanceof FileException) throw (FileException) e;
                if (e instanceof ParamException) throw (ParamException) e;
                throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件操作异常: " + e.getMessage());
            }
        }
        return true;
    }

    /**
     * 更新文件的回收站状态
     *
     * @param id                文件ID
     * @param originalTrashPath 源文件在回收站的路径
     * @param previewTrashPath  预览图文件在回收站的路径（可能为null）
     */
    private void updateFileTrashStatus(Long id, String originalTrashPath, String previewTrashPath) {
        SysFile sysFile = SysFile.builder()
                .id(id)
                .isTrash(StorageConstants.IS_TRASH)
                .originalTrashPath(originalTrashPath)
                .previewTrashPath(previewTrashPath)
                .updateTime(new Date())
                .build();
        if (!updateById(sysFile)) {
            log.warn("Failed to update file trash status in DB, ID: {}", id);
        }
    }

    /**
     * 根据id查询文件信息
     *
     * @param id 文件id
     * @return 文件信息
     */
    @Override
    public SysFile getFileManageById(Long id) {
        LambdaQueryWrapper<SysFile> eq = new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getId, id)
                .eq(SysFile::getIsTrash, StorageConstants.IS_NOT_DELETED);
        return getOne(eq);
    }

    /**
     * 查询文件回收站列表
     *
     * @param request 查询参数
     * @return 分页结果
     */
    @Override
    public Page<SysFile> listFileTrash(SysFileListRequest request) {
        Page<SysFile> sysFileManagementPage = new Page<>(request.getPageNum(), request.getPageSize());
        return sysFileMapper.listFileTrash(sysFileManagementPage, request);
    }

    /**
     * 恢复文件
     *
     * @param id 文件id
     * @return 是否恢复成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recoverFile(Long id) {
        LambdaQueryWrapper<SysFile> eq = new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getId, id)
                .eq(SysFile::getIsTrash, StorageConstants.IS_TRASH);
        SysFile sysFileInTrash = getOne(eq);

        if (sysFileInTrash == null) {
            throw new FileException(ResponseCode.FILE_OPERATION_ERROR, "文件不存在或未在回收站中");
        }
        if (!StringUtils.hasText(sysFileInTrash.getOriginalTrashPath())) {
            throw new FileException(ResponseCode.FILE_OPERATION_ERROR, "文件回收站路径数据不完整，无法恢复");
        }

        StorageService activeService = storageManager.getActiveStorageService();
        try {
            FileInfo restoredInfo = activeService.restoreFromTrash(sysFileInTrash.getOriginalTrashPath());
            if (restoredInfo != null && StringUtils.hasText(restoredInfo.getRelativePath())) {
                FileInfo restoredPreviewInfo = null;
                if (StringUtils.hasText(sysFileInTrash.getPreviewTrashPath())) {
                    restoredPreviewInfo = activeService.restoreFromTrash(sysFileInTrash.getPreviewTrashPath());
                    if (restoredPreviewInfo == null) {
                        log.warn("Failed to restore preview image from trash for file ID: {}. Continuing with main file.", id);
                    }
                }
                updateFileAfterRecovery(id, restoredInfo, restoredPreviewInfo);
                log.info("File restored from trash: ID={}, OriginalName={}, NewRelativePath={}",
                        id, sysFileInTrash.getOriginalName(), restoredInfo.getRelativePath());
                return true;
            } else {
                log.warn("Failed to restore file from storage trash or restored path not returned. File ID: {}", id);
                throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "从存储回收站恢复文件失败");
            }
        } catch (Exception e) {
            log.error("Exception during file recovery from trash: ID={}, Error: {}", id, e.getMessage(), e);
            if (e instanceof FileException) throw (FileException) e;
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "恢复文件操作异常: " + e.getMessage());
        }
    }

    private void updateFileAfterRecovery(Long id, FileInfo restoredInfo, FileInfo restoredPreviewInfo) {
        SysFile.SysFileBuilder builder = SysFile.builder()
                .id(id)
                .isTrash(StorageConstants.IS_NOT_DELETED)
                .originalRelativePath(restoredInfo.getRelativePath())
                .originalFileUrl(restoredInfo.getUrl())
                .newFileName(restoredInfo.getNewFileName())
                .originalTrashPath(null)
                .previewTrashPath(null)
                .updateTime(new Date());

        if (restoredPreviewInfo != null && StringUtils.hasText(restoredPreviewInfo.getRelativePath())) {
            builder.previewImagePath(restoredPreviewInfo.getRelativePath());
            builder.previewImageUrl(restoredPreviewInfo.getUrl());
        } else if (restoredPreviewInfo == null) {
            SysFile originalFile = getById(id);
            if (originalFile != null && StringUtils.hasText(originalFile.getPreviewTrashPath())) {
                builder.previewImagePath(null);
                builder.previewImageUrl(null);
            }
        }

        SysFile sysFile = builder.build();

        if (!updateById(sysFile)) {
            log.warn("Failed to update file status in DB after recovery, ID: {}", id);
        }
    }
}




