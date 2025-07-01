package cn.zhangchuangla.storage.core.service.impl;

import cn.zhangchuangla.common.core.constant.Constants;
import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.FileException;
import cn.zhangchuangla.storage.async.StorageAsyncService;
import cn.zhangchuangla.storage.constant.StorageConstants;
import cn.zhangchuangla.storage.core.service.FileOperationService;
import cn.zhangchuangla.storage.core.service.StorageConfigRetrievalService;
import cn.zhangchuangla.storage.model.dto.FileTrashInfoDTO;
import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import cn.zhangchuangla.storage.model.entity.FileRecord;
import cn.zhangchuangla.storage.model.entity.config.LocalFileStorageConfig;
import cn.zhangchuangla.storage.utils.StorageUtils;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * 本地文件存储服务实现类
 *
 * @author Chuang
 */
@Slf4j
@Service(StorageConstants.springBeanName.LOCAL_STORAGE_SERVICE)
public class LocalFileOperationServiceImpl implements FileOperationService {


    private final StorageConfigRetrievalService storageConfigRetrievalService;
    private final StorageAsyncService storageAsyncService;
    private final LocalFileStorageConfig localFileStorageConfig;

    public LocalFileOperationServiceImpl(StorageConfigRetrievalService storageConfigRetrievalService,
                                         StorageAsyncService storageAsyncService) {
        this.storageConfigRetrievalService = storageConfigRetrievalService;
        this.storageAsyncService = storageAsyncService;
        this.localFileStorageConfig = getConfig();
    }

    /**
     * 每次操作前拉取最新配置
     */
    @Override
    public LocalFileStorageConfig getConfig() {
        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();
        if (!StorageConstants.StorageType.LOCAL.equals(activeStorageType)) {
            throw new FileException("存储配置异常!请刷新配置后再试!");
        }
        String json = storageConfigRetrievalService.getCurrentStorageConfigJson();
        if (json == null || json.isBlank()) {
            throw new FileException("本地文件存储配置未找到");
        }
        return JSON.parseObject(json, LocalFileStorageConfig.class);
    }


    /**
     * 保存原始文件到本地。
     */
    @Override
    public UploadedFileInfo upload(MultipartFile file) {
        try {
            String datePath = todayDir();
            String newFileName = StorageUtils.generateFileName(Objects.requireNonNull(file.getOriginalFilename()));
            String targetDirectory = Paths.get(datePath, StorageConstants.dirName.FILE).toString();
            File destDir = ensureDir(targetDirectory);
            File destFile = new File(destDir, newFileName);

            file.transferTo(destFile);

            return buildFileInfo(file, destFile, targetDirectory, newFileName);
        } catch (IOException e) {
            log.error("文件上传传失败", e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件上传失败：" + e.getMessage());
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
        //todo 压缩图片的逻辑统一放到常量里面
        //最大宽
        int maxWidth = 1024;
        //最大高
        int maxHeight = 1024;
        //压缩质量
        float quality = 0.9f;

        getConfig();

        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());

        String datePath = todayDir();
        String originalImageDir = Paths.get(datePath, StorageConstants.dirName.IMAGE, StorageConstants.dirName.ORIGINAL)
                .toString();
        String previewImageDir = Paths.get(datePath, StorageConstants.dirName.IMAGE, StorageConstants.dirName.PREVIEW)
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
            storageAsyncService.compressImage(
                    originalFile.getAbsolutePath(),
                    compressedFile.getAbsolutePath(),
                    maxWidth,
                    maxHeight,
                    quality,
                    originalFilename
            );
            log.info("图片压缩任务已提交到后台执行: {}", compressedFile.getAbsolutePath());

        } catch (IOException e) {
            log.error("图片处理失败", e);
            // 清理可能已创建的文件，避免残留
            if (originalFile != null) {
                FileUtils.deleteQuietly(originalFile);
            }
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "图片处理失败：" + e.getMessage());
        }

        // 返回原图的信息，压缩图正在后台处理
        return buildFileInfo(originalFilename, originalFile, originalImageDir, originalFileName, file.getContentType());
    }


    /**
     * 删除文件
     *
     * @param originalRelativePath 原始文件相对路径
     * @param previewRelativePath  预览文件（如果存在）的相对路径
     * @param forceDelete          true: 强制从文件系统删除；false: 移入回收站
     * @return 如果是移入回收站，返回包含新路径的DTO；如果是强制删除或文件不存在，返回null
     */
    @Override
    public FileTrashInfoDTO delete(String originalRelativePath, String previewRelativePath, boolean forceDelete) {
        File uploadRootDir = new File(localFileStorageConfig.getUploadPath());
        File originalFile = new File(uploadRootDir, originalRelativePath);

        File previewFile = null;
        if (previewRelativePath != null && !previewRelativePath.isBlank()) {
            previewFile = new File(uploadRootDir, previewRelativePath);
        }

        // 强制删除模式
        if (forceDelete) {
            boolean originalDeleted = FileUtils.deleteQuietly(originalFile);
            // 默认为true，如果没有预览文件或删除成功
            boolean previewDeleted = true;
            if (previewFile != null) {
                previewDeleted = FileUtils.deleteQuietly(previewFile);
            }
            log.info("强制删除文件: {}, 结果: {}, 预览图删除结果: {}", originalRelativePath, originalDeleted, previewDeleted);
            return null;
        }

        // 移入回收站模式
        if (!originalFile.exists()) {
            log.warn("文件不存在，无法移入回收站: {}", originalRelativePath);
            return null;
        }

        // 构造回收站路径，通过在原路径前加上"trash"目录来保留目录结构
        String originalTrashPath = Paths.get(StorageConstants.dirName.TRASH, originalRelativePath).toString();
        File originalTrashFile = new File(uploadRootDir, originalTrashPath);
        String previewTrashPath = null;

        try {
            // 创建目标目录并移动文件
            FileUtils.forceMkdir(originalTrashFile.getParentFile());
            FileUtils.moveFile(originalFile, originalTrashFile);

            if (previewFile != null && previewFile.exists()) {
                previewTrashPath = Paths.get(StorageConstants.dirName.TRASH, previewRelativePath).toString();
                File previewTrashFile = new File(uploadRootDir, previewTrashPath);
                FileUtils.forceMkdir(previewTrashFile.getParentFile());
                FileUtils.moveFile(previewFile, previewTrashFile);
            }
        } catch (IOException e) {
            log.error("文件删除失败", e);
            throw new FileException("文件删除失败!" + e.getMessage());
        }

        log.info("文件已移入回收站. 原图新路径: {}, 预览图新路径: {}", originalTrashPath, previewTrashPath);

        return FileTrashInfoDTO.builder()
                .originalTrashPath(originalTrashPath)
                .previewTrashPath(previewTrashPath)
                .build();
    }

    /**
     * 恢复文件
     *
     * @param fileRecord 文件记录
     * @return 是否恢复成功
     */
    @Override
    public boolean restore(FileRecord fileRecord) {
        if (fileRecord == null) {
            throw new FileException(ResponseCode.FILE_OPERATION_ERROR, "文件记录为空");
        }

        LocalFileStorageConfig config = getConfig();
        File uploadRootDir = new File(config.getUploadPath());

        // 获取回收站中的文件路径
        String originalTrashPath = fileRecord.getOriginalTrashPath();
        String previewTrashPath = fileRecord.getPreviewTrashPath();

        // 获取原始文件路径
        String originalRelativePath = fileRecord.getOriginalRelativePath();
        String previewImagePath = fileRecord.getPreviewImagePath();

        if (StringUtils.isBlank(originalTrashPath) || StringUtils.isBlank(originalRelativePath)) {
            log.error("文件恢复失败：回收站路径或原始路径为空，文件ID: {}", fileRecord.getId());
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "此文件无法恢复!可能文件在计算机中已经被删除!");
        }

        try {
            // 1. 恢复主文件
            File trashFile = new File(uploadRootDir, originalTrashPath);
            File originalFile = new File(uploadRootDir, originalRelativePath);

            if (!trashFile.exists()) {
                log.error("回收站中的文件不存在：{}", originalTrashPath);
                throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件在回收站中不存在，无法恢复");
            }

            // 创建原始文件的目标目录
            String originalDir = Paths.get(originalRelativePath).getParent().toString();
            ensureDir(originalDir);

            // 移动文件从回收站到原位置
            FileUtils.moveFile(trashFile, originalFile);
            log.info("主文件恢复成功：{} -> {}", originalTrashPath, originalRelativePath);

            // 2. 恢复预览图（仅当预览图路径不为空时）
            if (StringUtils.isNotBlank(previewTrashPath) && StringUtils.isNotBlank(previewImagePath)) {
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

            log.info("文件恢复成功，文件ID: {}, 原始路径: {}", fileRecord.getId(), originalRelativePath);
            return true;

        } catch (IOException e) {
            log.error("文件恢复失败，文件ID: {}, 错误信息: {}", fileRecord.getId(), e.getMessage(), e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件恢复失败: " + e.getMessage());
        }
    }


    /**
     * 清空回收站
     *
     * @param relativePath 文件相对路径
     * @return 操作是否成功
     */
    @Override
    public boolean deleteTrashFile(String relativePath) {
        LocalFileStorageConfig config = getConfig();
        File file = new File(config.getUploadPath(), relativePath);
        if (!file.exists()) {
            log.warn("回收站文件不存在，删除失败: {}", relativePath);
            return false;
        }
        return FileUtils.deleteQuietly(file);
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
        info.setFileUrl(Paths.get(localFileStorageConfig.getFileDomain(), Constants.RESOURCE_PREFIX,
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
    private UploadedFileInfo buildFileInfo(String originalFileName, File savedFile, String filePath,
                                           String newFileName, String fileType) {
        UploadedFileInfo info = new UploadedFileInfo();
        info.setFileOriginalName(originalFileName);
        info.setFileName(newFileName);
        info.setFileExtension(StorageUtils.getFileExtension(newFileName));
        info.setFileSize(savedFile.length());
        info.setFileType(fileType);
        info.setExtension(StorageUtils.getFileExtension(newFileName));
        info.setFileUrl(Paths.get(localFileStorageConfig.getFileDomain(), Constants.RESOURCE_PREFIX, filePath, newFileName).toString());
        info.setFileRelativePath(Paths.get(filePath, newFileName).toString());
        info.setPreviewImage(Paths.get(localFileStorageConfig.getFileDomain(), Constants.RESOURCE_PREFIX, filePath,
                Constants.PREVIEW, newFileName).toString());
        info.setPreviewImageRelativePath(Paths.get(filePath, Constants.PREVIEW, newFileName).toString());
        return info;
    }


    /**
     * 日期目录可以在这边统一修改{@link StorageConstants}
     */
    private String todayDir() {
        return new SimpleDateFormat(StorageConstants.FILE_UPLOAD_PATH_FORMAT).format(new Date());
    }

    /**
     * 根据目录确保存在,并返回文件夹
     */
    private File ensureDir(String path) throws IOException {
        File dir = new File(localFileStorageConfig.getUploadPath(), path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("无法创建目录: " + dir.getAbsolutePath());
            }
        }
        return dir;
    }

}

