package cn.zhangchuangla.storage.core.service.impl;

import cn.zhangchuangla.common.core.constant.Constants;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.FileException;
import cn.zhangchuangla.common.core.exception.ParamException;
import cn.zhangchuangla.storage.async.StorageAsyncService;
import cn.zhangchuangla.storage.constant.StorageConstants;
import cn.zhangchuangla.storage.core.service.OperationService;
import cn.zhangchuangla.storage.core.service.StorageConfigRetrievalService;
import cn.zhangchuangla.storage.model.dto.FileOperationDto;
import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import cn.zhangchuangla.storage.model.entity.config.LocalStorageConfig;
import cn.zhangchuangla.storage.utils.StorageUtils;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * 本地文件存储服务实现类
 *
 * @author Chuang
 */
@Slf4j
@Service(StorageConstants.springBeanName.LOCAL_STORAGE_SERVICE)
public class LocalOperationServiceImpl implements OperationService {


    private final StorageConfigRetrievalService storageConfigRetrievalService;
    private final StorageAsyncService storageAsyncService;
    private LocalStorageConfig localStorageConfig;

    public LocalOperationServiceImpl(StorageConfigRetrievalService storageConfigRetrievalService, StorageAsyncService storageAsyncService) {
        this.storageConfigRetrievalService = storageConfigRetrievalService;
        this.storageAsyncService = storageAsyncService;
    }


    /**
     * 每次操作前拉取最新配置
     */
    public LocalStorageConfig getConfig() {
        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();
        String json = storageConfigRetrievalService.getCurrentStorageConfigJson();
        if (!StorageConstants.StorageType.LOCAL.equals(activeStorageType)) {
            throw new FileException(String.format("当前调用的服务是:%s,而你激活的配置是:%s,调用的服务和激活的配置不符合!请你仔细检查配置!"
                    , StorageConstants.StorageType.LOCAL, activeStorageType));
        }
        if (json == null || json.isBlank()) {
            throw new FileException("本地文件存储配置未找到");
        }
        localStorageConfig = JSON.parseObject(json, LocalStorageConfig.class);
        return localStorageConfig;
    }


    /**
     * 保存原始文件到本地。
     */
    @Override
    public UploadedFileInfo upload(MultipartFile file) {
        getConfig();
        try {
            String datePath = StorageUtils.createDateDir();
            String newFileName = StorageUtils.generateFileName(Objects.requireNonNull(file.getOriginalFilename()));
            String targetDirectory = Paths.get(StorageConstants.dirName.RESOURCE, datePath, StorageConstants.dirName.FILE).toString();
            File destDir = ensureDir(targetDirectory);
            File destFile = new File(destDir, newFileName);

            file.transferTo(destFile);

            return buildFileInfo(file, destFile, targetDirectory, newFileName);
        } catch (IOException e) {
            log.error("文件上传传失败", e);
            throw new FileException(ResultCode.FILE_OPERATION_FAILED, "文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 图片上传处理方法
     * <p>
     * 处理流程：
     * 1. 校验文件为有效图片格式
     * 2. 创建日期目录结构（包含原图和压缩图目录）
     * 3. 先保存原始图片到指定路径
     * 4. 对原始图片进行压缩处理并保存
     * 5. 返回压缩后的图片信息
     *
     * @param file 上传的MultipartFile对象
     * @return 包含上传文件信息的UploadedFileInfo对象
     */

    @Override
    public UploadedFileInfo uploadImage(MultipartFile file) {
        getConfig();
        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());

        String datePath = StorageUtils.createDateDir();
        String originalImageDir = Paths.get(StorageConstants.dirName.RESOURCE, datePath, StorageConstants.dirName.IMAGE, StorageConstants.dirName.ORIGINAL)
                .toString();
        String previewImageDir = Paths.get(StorageConstants.dirName.RESOURCE, datePath, StorageConstants.dirName.IMAGE, StorageConstants.dirName.PREVIEW)
                .toString();

        File originalDir;
        File previewDir;
        File originalFile = null;
        File compressedFile;
        String originalFileName;
        try {
            // 目录创建
            originalDir = ensureDir(originalImageDir);
            previewDir = ensureDir(previewImageDir);

            // 原图保存
            originalFileName = StorageUtils.generateFileName(originalFilename);
            originalFile = new File(originalDir, originalFileName);
            file.transferTo(originalFile);
            log.info("原图保存成功: {}", originalFile.getAbsolutePath());

            // 压缩图目标文件
            compressedFile = new File(previewDir, originalFileName);

            // 提交异步压缩
            storageAsyncService.compressImageLocal(
                    originalFile.getAbsolutePath(),
                    compressedFile.getAbsolutePath(),
                    StorageConstants.imageCompression.MAX_WIDTH,
                    StorageConstants.imageCompression.MAX_HEIGHT,
                    StorageConstants.imageCompression.QUALITY,
                    originalFilename
            );
            log.info("图片压缩任务已提交到后台执行: {}", compressedFile.getAbsolutePath());

        } catch (IOException e) {
            log.error("图片处理失败", e);
            // 清理可能已创建的文件，避免残留
            if (originalFile != null) {
                FileUtils.deleteQuietly(originalFile);
            }
            throw new FileException(ResultCode.FILE_OPERATION_FAILED, "图片处理失败：" + e.getMessage());
        }

        // 返回原图的信息，压缩图正在后台处理
        return buildFileInfo(originalFilename, originalFile, originalImageDir, previewImageDir, originalFileName, file.getContentType());
    }


    /**
     * 删除文件
     *
     * @param fileOperationDto 文件传输对象
     * @param forceDelete      true: 强制从文件系统删除；false: 移入回收站
     * @return 如果是移入回收站，返回包含新路径的DTO；如果是强制删除或文件不存在，返回null
     */
    @Override
    public FileOperationDto delete(FileOperationDto fileOperationDto, boolean forceDelete) {
        // 1. 参数校验
        if (ObjectUtils.isEmpty(fileOperationDto)) {
            throw new ParamException(ResultCode.PARAM_NOT_NULL, "参数不能为空");
        }
        if (StringUtils.isEmpty(fileOperationDto.getOriginalRelativePath())) {
            throw new ParamException(ResultCode.PARAM_NOT_NULL, "原始文件路径不能为空");
        }

        // 2. 获取配置和初始化
        getConfig();
        File uploadRootDir = new File(localStorageConfig.getUploadPath());
        File originalFile = new File(uploadRootDir, fileOperationDto.getOriginalRelativePath());

        File previewFile = null;
        if (StringUtils.isNotBlank(fileOperationDto.getPreviewRelativePath())) {
            previewFile = new File(uploadRootDir, fileOperationDto.getPreviewRelativePath());
        }

        // 3. 判断删除模式
        if (forceDelete && localStorageConfig.isRealDelete()) {
            // 物理删除模式
            return performPhysicalDelete(originalFile, previewFile, fileOperationDto);
        } else {
            // 移入回收站模式
            return performMoveToTrash(uploadRootDir, originalFile, previewFile, fileOperationDto);
        }
    }

    /**
     * 执行物理删除
     */
    private FileOperationDto performPhysicalDelete(File originalFile, File previewFile, FileOperationDto fileOperationDto) {
        log.info("开始执行物理删除操作，原始文件路径: {}", fileOperationDto.getOriginalRelativePath());

        // 删除原始文件
        boolean originalDeleted = FileUtils.deleteQuietly(originalFile);
        log.info("原始文件删除结果: {}, 路径: {}", originalDeleted, fileOperationDto.getOriginalRelativePath());

        // 删除预览文件（如果存在）
        boolean previewDeleted = true;
        if (previewFile != null) {
            previewDeleted = FileUtils.deleteQuietly(previewFile);
            log.info("预览文件删除结果: {}, 路径: {}", previewDeleted, fileOperationDto.getPreviewRelativePath());
        }

        log.info("物理删除操作完成，原始文件: {}, 预览文件: {}", originalDeleted, previewDeleted);
        return null;
    }

    /**
     * 执行移入回收站操作
     */
    private FileOperationDto performMoveToTrash(File uploadRootDir, File originalFile, File previewFile, FileOperationDto fileOperationDto) {
        log.info("开始执行移入回收站操作，原始文件路径: {}", fileOperationDto.getOriginalRelativePath());

        // 检查原始文件是否存在
        if (!originalFile.exists()) {
            log.warn("文件不存在，无法移入回收站: {}", fileOperationDto.getOriginalRelativePath());
            return null;
        }

        try {
            // 处理原始文件
            String pathWithoutResourcePrefix = StorageUtils.removeResourcePrefix(fileOperationDto.getOriginalRelativePath());
            String originalTrashPath = Paths.get(StorageConstants.dirName.TRASH, pathWithoutResourcePrefix).toString();
            File originalTrashFile = new File(uploadRootDir, originalTrashPath);

            // 创建目标目录并移动原始文件
            FileUtils.forceMkdir(originalTrashFile.getParentFile());
            FileUtils.moveFile(originalFile, originalTrashFile);
            log.debug("原始文件移动成功: {} -> {}", fileOperationDto.getOriginalRelativePath(), originalTrashPath);

            // 处理预览文件
            String previewTrashPath = null;
            if (previewFile != null && previewFile.exists()) {
                String previewPathWithoutResourcePrefix = StorageUtils.removeResourcePrefix(fileOperationDto.getPreviewRelativePath());
                previewTrashPath = Paths.get(StorageConstants.dirName.TRASH, previewPathWithoutResourcePrefix).toString();
                File previewTrashFile = new File(uploadRootDir, previewTrashPath);

                FileUtils.forceMkdir(previewTrashFile.getParentFile());
                FileUtils.moveFile(previewFile, previewTrashFile);
                log.debug("预览文件移动成功: {} -> {}", fileOperationDto.getPreviewRelativePath(), previewTrashPath);
            }

            log.info("文件已成功移入回收站. 原始文件新路径: {}, 预览文件新路径: {}", originalTrashPath, previewTrashPath);

            return FileOperationDto.builder()
                    .originalTrashPath(originalTrashPath)
                    .previewTrashPath(previewTrashPath)
                    .build();

        } catch (IOException e) {
            log.error("移入回收站失败，原始文件路径: {}", fileOperationDto.getOriginalRelativePath(), e);
            throw new FileException(ResultCode.FILE_OPERATION_FAILED, "文件移入回收站失败: " + e.getMessage());
        }
    }

    /**
     * 恢复文件
     *
     * @param fileOperationDto 文件操作传输对象
     * @return 是否恢复成功
     */
    @Override
    public boolean restore(FileOperationDto fileOperationDto) {
        // 1. 参数校验
        if (fileOperationDto == null) {
            throw new FileException(ResultCode.FILE_OPERATION_ERROR, "文件记录为空");
        }

        // 2. 获取配置和初始化
        getConfig();
        File uploadRootDir = new File(localStorageConfig.getUploadPath());

        // 3. 获取路径信息
        String originalTrashPath = fileOperationDto.getOriginalTrashPath();
        String previewTrashPath = fileOperationDto.getPreviewTrashPath();
        String originalRelativePath = fileOperationDto.getOriginalRelativePath();
        String previewImagePath = fileOperationDto.getPreviewRelativePath();

        // 4. 校验必要路径
        if (StringUtils.isBlank(originalTrashPath) || StringUtils.isBlank(originalRelativePath)) {
            log.error("文件恢复失败：回收站路径或原始路径为空，文件ID: {}", fileOperationDto.getFileId());
            throw new FileException(ResultCode.FILE_OPERATION_FAILED, "此文件无法恢复!可能文件在计算机中已经被删除!");
        }

        try {
            // 5. 恢复原始文件
            restoreOriginalFile(uploadRootDir, originalTrashPath, originalRelativePath);

            // 6. 恢复预览文件（如果存在）
            restorePreviewFile(uploadRootDir, previewTrashPath, previewImagePath);

            log.info("文件恢复成功，文件ID: {}, 原始路径: {}", fileOperationDto.getFileId(), originalRelativePath);
            return true;

        } catch (IOException e) {
            log.error("文件恢复失败，文件ID: {}, 错误信息: {}", fileOperationDto.getFileId(), e.getMessage(), e);
            throw new FileException(ResultCode.FILE_OPERATION_FAILED, "文件恢复失败: " + e.getMessage());
        }
    }

    /**
     * 恢复原始文件
     */
    private void restoreOriginalFile(File uploadRootDir, String originalTrashPath, String originalRelativePath) throws IOException {
        File trashFile = new File(uploadRootDir, originalTrashPath);
        File originalFile = new File(uploadRootDir, originalRelativePath);

        if (!trashFile.exists()) {
            log.error("回收站中的文件不存在：{}", originalTrashPath);
            throw new FileException(ResultCode.FILE_OPERATION_FAILED, "文件在回收站中不存在，无法恢复");
        }

        // 创建原始文件的目标目录
        String originalDir = Paths.get(originalRelativePath).getParent().toString();
        ensureDir(originalDir);

        // 移动文件从回收站到原位置
        FileUtils.moveFile(trashFile, originalFile);
        log.info("主文件恢复成功：{} -> {}", originalTrashPath, originalRelativePath);
    }

    /**
     * 恢复预览文件
     */
    private void restorePreviewFile(File uploadRootDir, String previewTrashPath, String previewImagePath) throws IOException {
        if (StringUtils.isBlank(previewTrashPath) || StringUtils.isBlank(previewImagePath)) {
            log.debug("预览文件路径为空，跳过预览文件恢复");
            return;
        }

        File previewTrashFile = new File(uploadRootDir, previewTrashPath);
        File previewOriginalFile = new File(uploadRootDir, previewImagePath);

        if (previewTrashFile.exists()) {
            // 创建预览图的目标目录
            String previewDir = Paths.get(previewImagePath).getParent().toString();
            ensureDir(previewDir);

            // 移动预览图从回收站到原位置
            FileUtils.moveFile(previewTrashFile, previewOriginalFile);
            log.info("预览图恢复成功：{} -> {}", previewTrashPath, previewImagePath);
        } else {
            log.warn("预览图在回收站中不存在，跳过预览图恢复：{}", previewTrashPath);
        }
    }

    /**
     * 删除回收站文件
     *
     * @param fileOperationDto 文件传输对象
     */
    @Override
    public void deleteTrashFile(FileOperationDto fileOperationDto) {
        getConfig();
        if (fileOperationDto == null) {
            throw new FileException(ResultCode.FILE_OPERATION_ERROR, "文件记录为空");
        }
        if (fileOperationDto.getOriginalTrashPath().isBlank()) {
            throw new FileException(ResultCode.FILE_OPERATION_ERROR, "文件记录不能为空!");
        }
        //如果不是真实删除，则直接返回成功
        boolean realDelete = localStorageConfig.isRealDelete();
        if (!realDelete) {
            log.info("文件不是真实删除，系统将不会执行实际的删除操作!");
            return;
        }
        //1.删除文件
        String originalTrash = Paths.get(localStorageConfig.getUploadPath(), fileOperationDto.getOriginalTrashPath())
                .toString();
        File uploadRootDir = new File(originalTrash);
        try {
            FileUtils.delete(uploadRootDir);
            log.info("文件删除成功：{}", originalTrash);
            //2.如果预览图存在，则删除预览图
            if (fileOperationDto.getPreviewTrashPath() != null && !fileOperationDto.getPreviewTrashPath().isBlank()) {
                String previewTrash = Paths.get(localStorageConfig.getUploadPath(), fileOperationDto.getPreviewTrashPath()).toString();
                File previewTrashFile = new File(previewTrash);
                if (previewTrashFile.exists()) {
                    FileUtils.delete(previewTrashFile);
                }
                log.info("预览图删除成功：{}", previewTrash);
            }
        } catch (IOException e) {
            throw new FileException(ResultCode.FILE_OPERATION_FAILED, "文件删除失败: " + e.getMessage());
        }
    }


    /**
     * 构建文件信息
     *
     * @param src         文件源
     * @param savedFile   保存文件
     * @param targetPath  文件保存路径
     * @param newFileName 新文件名
     * @return 文件信息
     */
    private UploadedFileInfo buildFileInfo(MultipartFile src, File savedFile, String targetPath, String newFileName) {
        UploadedFileInfo info = new UploadedFileInfo();
        info.setFileOriginalName(src.getOriginalFilename());
        info.setFileName(newFileName);
        info.setFileExtension(StorageUtils.getFileExtension(newFileName));
        info.setFileSize(savedFile.length());
        info.setFileType(src.getContentType());
        info.setExtension(StorageUtils.getFileExtension(newFileName));
        info.setFileUrl(Paths.get(localStorageConfig.getFileDomain(), Constants.RESOURCE_PREFIX,
                targetPath, newFileName).toString());
        info.setFileRelativePath(Paths.get(targetPath, newFileName).toString());
        return info;
    }

    /**
     * 构建文件信息
     *
     * @param originalFileName 文件源
     * @param savedFile        保存文件
     * @param filePath         文件保存路径
     * @param newFileName      新文件名
     * @return 文件信息
     */
    private UploadedFileInfo buildFileInfo(String originalFileName, File savedFile, String filePath, String previewPath,
                                           String newFileName, String fileType) {
        UploadedFileInfo info = new UploadedFileInfo();
        info.setFileOriginalName(originalFileName);
        info.setFileName(newFileName);
        info.setFileExtension(StorageUtils.getFileExtension(newFileName));
        info.setFileSize(savedFile.length());
        info.setFileType(fileType);
        info.setExtension(StorageUtils.getFileExtension(newFileName));
        info.setFileUrl(Paths.get(localStorageConfig.getFileDomain(), Constants.RESOURCE_PREFIX, filePath, newFileName).toString());
        info.setFileRelativePath(Paths.get(filePath, newFileName).toString());
        info.setPreviewImage(Paths.get(localStorageConfig.getFileDomain(), Constants.RESOURCE_PREFIX, previewPath
                , newFileName).toString());
        info.setPreviewImageRelativePath(Paths.get(previewPath, newFileName).toString());
        return info;
    }


    /**
     * 根据目录确保存在,并返回文件夹
     */
    private File ensureDir(String path) throws IOException {
        File dir = new File(localStorageConfig.getUploadPath(), path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("无法创建目录: " + dir.getAbsolutePath());
            }
        }
        return dir;
    }


}
