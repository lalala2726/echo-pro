package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.constant.StorageConstants;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.exception.ParamException;
import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.storage.core.StorageOperation;
import cn.zhangchuangla.storage.factory.StorageFactory;
import cn.zhangchuangla.storage.mapper.SysFileMapper;
import cn.zhangchuangla.storage.model.entity.SysFile;
import cn.zhangchuangla.storage.model.request.file.SysFileListRequest;
import cn.zhangchuangla.storage.service.StorageFileService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final StorageFactory storageFactory;


    /**
     * 保存文件信息
     *
     * @param fileTransferDto 文件上传结果
     */
    @Override
    public void saveFileInfo(FileTransferDto fileTransferDto) {
        Long userId = SecurityUtils.getUserId();
        String userName = SecurityUtils.getUsername();
        SysFile sysFile = SysFile.builder()
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
        save(sysFile);
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

        for (Long id : ids) {
            SysFile fileManagement = getFileManageById(id);
            if (fileManagement == null) {
                log.warn("文件不存在，ID: {}", id);
                throw new FileException(ResponseCode.RESULT_IS_NULL, "文件不存在，ID: " + id);
            }

            try {
                // 构建文件传输对象
                FileTransferDto fileTransferDto = convertToFileTransferDto(fileManagement);

                // 获取对应的存储操作实现
                StorageOperation storageOperation = storageFactory.getStorageOperation(fileManagement.getStorageType());

                // 决定是否使用回收站：
                // 1. 如果用户选择移至回收站(isPermanently=false)，无论系统设置如何都使用回收站
                // 2. 如果用户选择永久删除(isPermanently=true)：
                //    - 如果系统启用回收站，仍然使用回收站
                //    - 如果系统关闭回收站，则真正删除文件
                // 如果不是永久删除，强制使用回收站
                boolean forceTrash = !isPermanently;

                // 执行文件删除或移至回收站操作
                boolean operationResult = storageOperation.removeFile(fileTransferDto, forceTrash);

                if (operationResult) {
                    // 检查文件是否被放入回收站（通过检查是否设置了回收站路径）
                    boolean movedToTrash = fileTransferDto.getOriginalTrashPath() != null;

                    if (movedToTrash) {
                        // 文件被移至回收站：更新数据库状态
                        String originalTrashPath = fileTransferDto.getOriginalTrashPath();
                        String previewTrashPath = fileTransferDto.getPreviewTrashPath();

                        updateFileTrashStatus(id, originalTrashPath, previewTrashPath);
                        log.info("文件已移至回收站：ID={}, 名称={}, 源文件回收站路径={}",
                                id, fileManagement.getOriginalName(), originalTrashPath);
                    } else {
                        // 文件被永久删除：从数据库中删除记录
                        removeById(id);
                        log.info("文件已永久删除：ID={}, 名称={}", id, fileManagement.getOriginalName());
                    }
                } else {
                    log.warn("文件操作失败：ID={}", id);
                    throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件操作失败");
                }
            } catch (Exception e) {
                log.error("文件操作发生未预期异常：ID={}, 错误信息={}", id, e.getMessage(), e);
                throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件操作出现异常：" + e.getMessage());
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
                // 设置为"在回收站"
                .isTrash(StorageConstants.IS_TRASH)
                // 记录原始文件回收站路径
                .originalTrashPath(originalTrashPath)
                // 记录预览图回收站路径（可能为null）
                .previewTrashPath(previewTrashPath)
                .updateTime(new Date())
                .build();

        if (!updateById(sysFile)) {
            log.warn("更新文件回收站状态失败，ID: {}", id);
        }
    }

    /**
     * 将文件管理实体转换为文件传输DTO
     *
     * @param fileManagement 文件管理实体
     * @return 文件传输DTO
     */
    private FileTransferDto convertToFileTransferDto(SysFile fileManagement) {
        FileTransferDto fileTransferDto = new FileTransferDto();
        BeanUtils.copyProperties(fileTransferDto, fileManagement);
        return fileTransferDto;
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
        // 获取回收站中的文件记录
        LambdaQueryWrapper<SysFile> eq = new LambdaQueryWrapper<SysFile>()
                .eq(SysFile::getId, id)
                .eq(SysFile::getIsTrash, StorageConstants.IS_TRASH);

        SysFile fileManagement = getOne(eq);

        if (fileManagement == null) {
            throw new FileException(ResponseCode.FILE_OPERATION_ERROR, "文件不存在或未在回收站中");
        }

        // 验证必要的路径字段
        if (fileManagement.getOriginalTrashPath() == null || fileManagement.getOriginalRelativePath() == null) {
            throw new FileException(ResponseCode.FILE_OPERATION_ERROR, "文件回收站路径或原始路径数据不完整，无法恢复");
        }

        try {

            FileTransferDto fileTransferDto = FileTransferDto.builder()
                    .originalName(fileManagement.getOriginalName())
                    .contentType(fileManagement.getContentType())
                    .fileSize(fileManagement.getFileSize())
                    .fileMd5(fileManagement.getFileMd5())
                    .originalFileUrl(fileManagement.getOriginalFileUrl())
                    .storageType(fileManagement.getStorageType())
                    .bucketName(fileManagement.getBucketName())
                    .originalRelativePath(fileManagement.getOriginalRelativePath())
                    .originalTrashPath(fileManagement.getOriginalTrashPath())
                    .previewImagePath(fileManagement.getPreviewImagePath())
                    .previewTrashPath(fileManagement.getPreviewTrashPath())
                    .build();

            // 获取对应的存储操作实现
            StorageOperation storageOperation = storageFactory.getStorageOperation(fileManagement.getStorageType());

            // 执行文件恢复操作
            boolean recoverResult = storageOperation.recoverFile(fileTransferDto);

            if (recoverResult) {
                // 恢复成功，更新数据库状态
                SysFile updateEntity = SysFile.builder()
                        .id(id)
                        // 设置为"不在回收站"
                        .isTrash(StorageConstants.IS_NOT_TRASH)
                        // 清空回收站路径
                        .originalTrashPath(null)
                        // 清空预览图回收站路径
                        .previewTrashPath(null)
                        .updateTime(new Date())
                        .build();

                boolean updateResult = updateById(updateEntity);

                if (updateResult) {
                    log.info("文件已从回收站恢复：ID={}, 名称={}", id, fileManagement.getOriginalName());
                    return true;
                } else {
                    log.warn("文件恢复后更新数据库状态失败：ID={}", id);
                    throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "更新文件状态失败");
                }
            } else {
                log.warn("文件恢复失败：ID={}", id);
                throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件恢复操作失败");
            }
        } catch (Exception e) {
            log.error("文件恢复发生异常：ID={}, 错误信息={}", id, e.getMessage(), e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件恢复出现异常：" + e.getMessage());
        }
    }
}




