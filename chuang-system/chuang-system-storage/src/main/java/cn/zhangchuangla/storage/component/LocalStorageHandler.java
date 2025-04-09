package cn.zhangchuangla.storage.component;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.constant.StorageConstants;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.utils.FileOperationUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 本地文件存储组件
 * 替代原本的本地存储工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/3 10:00
 */
@Slf4j
@Component
public class LocalStorageHandler extends AbstractStorageHandler {


    /**
     * 上传文件到本地存储
     *
     * @param fileTransferDto 文件传输对象
     * @param uploadPath      上传根路径
     * @param fileDomain      文件访问域名
     * @return 文件传输对象
     */
    public FileTransferDto uploadFile(FileTransferDto fileTransferDto, String uploadPath, String fileDomain) {
        if (fileTransferDto == null || fileTransferDto.getBytes() == null
                || fileTransferDto.getOriginalName() == null) {
            throw new FileException(ResponseCode.FileUploadFailed, "文件数据或文件名不能为空！");
        }

        // 填充文件基础信息
        fillFileTransferInfo(fileTransferDto, StorageConstants.LOCAL, "local-storage");

        // 如果是图片类型，则调用图片上传方法
        if (isImage(fileTransferDto)) {
            return imageUpload(fileTransferDto, uploadPath, fileDomain);
        }

        String fileName = fileTransferDto.getOriginalName();
        byte[] data = fileTransferDto.getBytes();

        // 生成存储路径
        String relativePath = generateFilePath(fileName);

        // 保存文件
        saveFile(data, uploadPath, relativePath);

        // 构建URL
        String fileUrl = buildCompleteUrl(relativePath, fileDomain);

        return createEnhancedFileTransferResponse(fileUrl, relativePath, null, null, fileTransferDto);
    }

    /**
     * 上传图片到本地存储
     * 同时上传原图和压缩图，分别保存在不同目录
     *
     * @param fileTransferDto 文件传输对象
     * @param uploadPath      上传根路径
     * @param fileDomain      文件访问域名
     * @return 增强后的文件传输对象
     */
    public FileTransferDto imageUpload(FileTransferDto fileTransferDto, String uploadPath, String fileDomain) {
        if (fileTransferDto == null || fileTransferDto.getBytes() == null
                || fileTransferDto.getOriginalName() == null) {
            throw new FileException(ResponseCode.FileUploadFailed, "文件数据或文件名不能为空！");
        }

        // 填充文件基础信息
        fillFileTransferInfo(fileTransferDto, StorageConstants.LOCAL, "local-storage");

        // 验证是否为图片类型
        if (!isImage(fileTransferDto)) {
            throw new FileException(ResponseCode.FileUploadFailed, "非图片类型文件不能使用图片上传接口！");
        }

        String fileName = fileTransferDto.getOriginalName();
        byte[] originalData = fileTransferDto.getBytes();

        try {
            // 生成存储路径
            String originalRelativePath = generateOriginalImagePath(fileName);
            String compressedRelativePath = generateCompressedImagePath(fileName);

            // 保存原图
            saveFile(originalData, uploadPath, originalRelativePath);
            String originalUrl = buildCompleteUrl(originalRelativePath, fileDomain);

            // 压缩并保存
            byte[] compressedData = compressImage(originalData);
            saveFile(compressedData, uploadPath, compressedRelativePath);
            String compressedUrl = buildCompleteUrl(compressedRelativePath, fileDomain);

            return createEnhancedFileTransferResponse(
                    originalUrl, originalRelativePath,
                    compressedUrl, compressedRelativePath,
                    fileTransferDto);

        } catch (Exception e) {
            log.error("图片处理及保存失败", e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "图片上传失败：" + e.getMessage());
        }
    }

    /**
     * 保存文件到本地
     */
    private void saveFile(byte[] data, String uploadPath, String relativePath) {
        String filePath = uploadPath + File.separator + relativePath;
        Path path = Paths.get(filePath);
        Path directory = path.getParent();
        try {
            // 创建目录
            if (directory != null) {
                Files.createDirectories(directory);
            }

            // 写入文件
            Files.write(path, data);
        } catch (IOException e) {
            log.error("文件写入失败", e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件写入失败！");
        }
    }

    /**
     * 构建完整的URL
     */
    private String buildCompleteUrl(String relativePath, String domain) {
        if (StringUtils.isEmpty(domain)) {
            return FileOperationUtils.buildFinalPath(Constants.RESOURCE_PREFIX, relativePath);
        }
        return buildFullUrl(domain, relativePath);
    }

    /**
     * 删除文件
     * <p>
     * 根据配置决定是移动到回收站还是直接删除
     *
     * @param rootPath        文件根路径
     * @param fileTransferDto 文件传输对象
     * @param enableTrash     是否启用回收站（true：移动到回收站，false：直接删除）
     * @return 操作结果
     */
    public boolean removeFile(@NotNull final String rootPath, @NotNull FileTransferDto fileTransferDto, boolean enableTrash) {
        // 参数校验
        if (fileTransferDto == null || StringUtils.isEmpty(fileTransferDto.getOriginalRelativePath())) {
            log.error("文件信息不完整，无法执行删除操作");
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件信息不完整，无法删除！");
        }

        // 原文件信息
        String originalRelativePath = fileTransferDto.getOriginalRelativePath();
        String originalFilePath = rootPath + File.separator + originalRelativePath;
        String originalFileName = FileOperationUtils.getFileNameByRelativePath(originalRelativePath);

        // 预览图信息（可能不存在）
        String previewImagePath = fileTransferDto.getPreviewImagePath();
        boolean hasPreviewImage = StringUtils.hasText(previewImagePath);

        // 记录操作类型
        log.info("开始处理文件 - 原始文件: {}, 预览图: {}, 操作模式: {}",
                originalFilePath,
                hasPreviewImage ? (rootPath + File.separator + previewImagePath) : "无",
                enableTrash ? "移至回收站" : "直接删除");

        try {
            // 如果启用回收站，确保回收站目录结构存在
            if (enableTrash) {
                createTrashDirectories(rootPath);
            }

            // 1. 处理原始文件
            File originalFile = new File(originalFilePath);
            if (!originalFile.exists()) {
                log.warn("原始文件不存在: {}", originalFilePath);
            } else {
                if (enableTrash) {
                    // 移动到回收站
                    String originalTrashRelativePath = moveFileToTrash(rootPath, originalFile, originalFileName, StorageConstants.FILE_ORIGINAL_FOLDER);
                    fileTransferDto.setOriginalTrashPath(originalTrashRelativePath);
                    log.debug("已将原始文件移动到回收站路径: {}", originalTrashRelativePath);
                } else {
                    // 直接删除文件
                    FileUtils.delete(originalFile);
                    log.debug("已永久删除原始文件: {}", originalFilePath);
                }
            }

            // 2. 处理预览图文件（如果存在）
            if (hasPreviewImage) {
                String previewFilePath = rootPath + File.separator + previewImagePath;
                File previewFile = new File(previewFilePath);

                if (!previewFile.exists()) {
                    log.warn("预览图文件不存在: {}", previewFilePath);
                } else {
                    if (enableTrash) {
                        // 获取预览图文件名
                        String previewFileName = FileOperationUtils.getFileNameByRelativePath(previewImagePath);

                        // 移动到回收站
                        String previewTrashRelativePath = moveFileToTrash(rootPath, previewFile, previewFileName, StorageConstants.FILE_PREVIEW_FOLDER);
                        fileTransferDto.setPreviewTrashPath(previewTrashRelativePath);
                        log.debug("已将预览图文件移动到回收站路径: {}", previewTrashRelativePath);
                    } else {
                        // 直接删除预览图
                        FileUtils.delete(previewFile);
                        log.debug("已永久删除预览图文件: {}", previewFilePath);
                    }
                }
            }

            return true;
        } catch (IOException e) {
            log.error("文件操作失败: {}", e.getMessage(), e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件操作失败：" + e.getMessage());
        }
    }

    /**
     * 创建回收站目录结构
     *
     * @param rootPath 根路径
     * @throws IOException 如果创建目录失败
     */
    private void createTrashDirectories(String rootPath) throws IOException {
        // 回收站主目录
        Path trashPath = Paths.get(rootPath, StorageConstants.TRASH_DIR);
        if (!Files.exists(trashPath)) {
            log.info("创建回收站主目录: {}", trashPath);
            Files.createDirectories(trashPath);
        }

        // 原始文件回收站目录
        Path trashOriginalPath = Paths.get(rootPath, StorageConstants.TRASH_DIR, StorageConstants.FILE_ORIGINAL_FOLDER);
        if (!Files.exists(trashOriginalPath)) {
            log.info("创建原始文件回收站目录: {}", trashOriginalPath);
            Files.createDirectories(trashOriginalPath);
        }

        // 预览图回收站目录
        Path trashPreviewPath = Paths.get(rootPath, StorageConstants.TRASH_DIR, StorageConstants.FILE_PREVIEW_FOLDER);
        if (!Files.exists(trashPreviewPath)) {
            log.info("创建预览图回收站目录: {}", trashPreviewPath);
            Files.createDirectories(trashPreviewPath);
        }
    }

    /**
     * 将文件移动到回收站
     *
     * @param rootPath   根路径
     * @param sourceFile 源文件
     * @param fileName   文件名
     * @param subFolder  子文件夹（original/preview）
     * @return 回收站中的相对路径
     * @throws IOException 如果移动文件失败
     */
    private String moveFileToTrash(String rootPath, File sourceFile, String fileName, String subFolder) throws IOException {
        // 生成回收站中的路径（按年月目录组织）
        String yearMonthDir = FileOperationUtils.generateYearMonthDir();
        String trashRelativePath =
                StorageConstants.TRASH_DIR + "/" +
                        subFolder + "/" +
                        yearMonthDir + "/" +
                        System.currentTimeMillis() + "_" + fileName;

        // 创建目标文件对象
        File targetFile = new File(rootPath + File.separator + trashRelativePath);

        // 确保目标目录存在
        Files.createDirectories(targetFile.getParentFile().toPath());

        // 移动文件
        log.info("将文件移动到回收站: {} -> {}", sourceFile.getPath(), targetFile.getPath());
        FileUtils.moveFile(sourceFile, targetFile);

        return trashRelativePath;
    }

    /**
     * 从回收站恢复文件
     * <p>
     * 恢复原文件和预览图（如果存在）到原来的位置
     *
     * @param uploadPath      上传根路径
     * @param fileTransferDto 文件传输对象
     * @return 恢复结果
     */
    public boolean recoverFile(String uploadPath, FileTransferDto fileTransferDto) {
        // 参数校验
        if (fileTransferDto == null) {
            throw new FileException(ResponseCode.FILE_OPERATION_ERROR, "文件传输对象不能为空，无法进行恢复");
        }

        // 验证必要的路径信息
        if (StringUtils.isEmpty(fileTransferDto.getOriginalRelativePath()) ||
                StringUtils.isEmpty(fileTransferDto.getOriginalTrashPath())) {
            throw new FileException(ResponseCode.FILE_OPERATION_ERROR,
                    "文件信息不完整，缺少原始路径或回收站路径");
        }

        boolean success = true;
        boolean hasError = false;

        try {
            // 1. 恢复原始文件
            Path originalTrashPath = Paths.get(uploadPath, fileTransferDto.getOriginalTrashPath());
            Path originalDestPath = Paths.get(uploadPath, fileTransferDto.getOriginalRelativePath());

            if (!Files.exists(originalTrashPath)) {
                log.warn("回收站中的原始文件不存在: {}", originalTrashPath);
                hasError = true;
                success = false;
            } else {
                try {
                    // 确保目标目录存在
                    if (originalDestPath.getParent() != null) {
                        Files.createDirectories(originalDestPath.getParent());
                    }

                    // 将原始文件从回收站移回原位置
                    Files.move(originalTrashPath, originalDestPath, StandardCopyOption.REPLACE_EXISTING);
                    log.info("原始文件已从回收站恢复: {} -> {}", originalTrashPath, originalDestPath);
                } catch (IOException e) {
                    log.error("恢复原始文件失败: {} -> {}, 错误: {}", originalTrashPath, originalDestPath, e.getMessage());
                    throw e; // 原始文件恢复失败，直接抛出异常
                }
            }

            // 2. 恢复预览图（如果存在）
            String previewTrashPath = fileTransferDto.getPreviewTrashPath();
            String previewImagePath = fileTransferDto.getPreviewImagePath();

            if (StringUtils.hasText(previewTrashPath) && StringUtils.hasText(previewImagePath)) {
                Path previewTrash = Paths.get(uploadPath, previewTrashPath);
                Path previewDest = Paths.get(uploadPath, previewImagePath);

                if (!Files.exists(previewTrash)) {
                    log.warn("回收站中的预览图文件不存在: {}", previewTrash);
                    // 预览图不存在不影响整体恢复成功状态，因为它是可选的
                } else {
                    try {
                        // 确保目标目录存在
                        if (previewDest.getParent() != null) {
                            Files.createDirectories(previewDest.getParent());
                        }

                        // 将预览图从回收站移回原位置
                        Files.move(previewTrash, previewDest, StandardCopyOption.REPLACE_EXISTING);
                        log.info("预览图文件已从回收站恢复: {} -> {}", previewTrash, previewDest);
                    } catch (IOException e) {
                        log.error("恢复预览图文件失败: {} -> {}, 错误: {}", previewTrash, previewDest, e.getMessage());
                        // 预览图恢复失败不应中断整个恢复过程，但应记录错误
                    }
                }
            }

            if (hasError) {
                throw new IOException("文件恢复过程中发生错误，部分文件可能未恢复成功");
            }

            return success;
        } catch (IOException e) {
            log.error("恢复文件失败: {}", e.getMessage(), e);
            throw new FileException(ResponseCode.FILE_OPERATION_ERROR, "恢复文件失败: " + e.getMessage());
        }
    }
}
