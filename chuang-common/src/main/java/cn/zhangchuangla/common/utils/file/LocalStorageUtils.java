package cn.zhangchuangla.common.utils.file;

import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.constant.StorageConstants;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.utils.FileOperationUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地文件存储工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/3 10:00
 */
@Slf4j
public class LocalStorageUtils extends AbstractStorageUtils {


    @Resource
    private AppConfig appConfig;


    /**
     * 上传文件到本地存储
     *
     * @param fileTransferDto 文件传输对象
     * @param uploadPath      上传根路径
     * @param fileDomain      文件访问域名
     * @return 文件传输对象
     */
    public static FileTransferDto uploadFile(FileTransferDto fileTransferDto, String uploadPath, String fileDomain) {
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
    public static FileTransferDto imageUpload(FileTransferDto fileTransferDto, String uploadPath, String fileDomain) {
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
    private static void saveFile(byte[] data, String uploadPath, String relativePath) {
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
    private static String buildCompleteUrl(String relativePath, String domain) {
        if (StringUtils.isEmpty(domain)) {
            return FileOperationUtils.buildFinalPath(Constants.RESOURCE_PREFIX, relativePath);
        }
        return buildFullUrl(domain, relativePath);
    }

    /**
     * 删除文件
     *
     * @param fileTransferDto 文件传输对象
     * @param isDelete        是否删除
     * @return 操作结果
     */
    /**
     * 删除文件
     *
     * @param rootPath        文件根路径
     * @param fileTransferDto 文件传输对象
     * @param isDelete        是否移动到回收站（true：移动到回收站，false：直接删除）
     * @return 操作结果
     */
    public static boolean removeFile(@NotNull final String rootPath, FileTransferDto fileTransferDto, boolean isDelete) {
        if (fileTransferDto == null || StringUtils.isEmpty(fileTransferDto.getOriginalRelativePath())) {
            log.error("文件信息不完整，无法执行删除操作");
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件信息不完整，无法删除！");
        }

        // 获取原始文件信息
        String originalRelativePath = fileTransferDto.getOriginalRelativePath();
        String originalFilePath = rootPath + File.separator + originalRelativePath;
        String fileName = FileOperationUtils.getFileNameByRelativePath(originalRelativePath);

        // 获取压缩图片路径（如果存在）
        String compressedRelativePath = fileTransferDto.getPreviewImagePath();

        // 获取回收站路径
        String trashDir = rootPath + File.separator + StorageConstants.TRASH_DIR;
        File trashDirectory = new File(trashDir);

        log.info("文件处理 - 原始文件: {}, 目标路径: {}", originalFilePath, isDelete ? trashDir : "直接删除");

        try {
            // 确保回收站目录存在（仅在需要移动到回收站时）
            if (isDelete && !trashDirectory.exists()) {
                Files.createDirectories(trashDirectory.toPath());
            }

            // 处理原始文件
            File originalFile = new File(originalFilePath);
            if (originalFile.exists()) {
                if (isDelete) {
                    // 确保目标文件名在回收站中唯一
                    File targetFile = new File(trashDir + File.separator + fileName);
                    int counter = 0;
                    while (targetFile.exists()) {
                        counter++;
                        targetFile = new File(trashDir + File.separator + counter + "_" + fileName);
                    }
                    // 移动到回收站
                    FileUtils.moveFile(originalFile, targetFile);
                    log.debug("已将原始文件 {} 移动到回收站 {}", originalFilePath, targetFile.getPath());
                } else {
                    // 直接删除
                    FileUtils.delete(originalFile);
                    log.debug("已删除原始文件: {}", originalFilePath);
                }
            } else {
                log.warn("原始文件不存在: {}", originalFilePath);
            }

            // 处理压缩图片文件（如果存在）
            if (StringUtils.isNotEmpty(compressedRelativePath)) {
                String compressedFilePath = rootPath + File.separator + compressedRelativePath;
                File compressedFile = new File(compressedFilePath);

                if (compressedFile.exists()) {
                    if (isDelete) {
                        // 获取压缩图片文件名
                        String compressedFileName = FileOperationUtils.getFileNameByRelativePath(compressedRelativePath);
                        // 确保目标文件名在回收站中唯一
                        File targetCompressedFile = new File(trashDir + File.separator + compressedFileName);
                        int counter = 0;
                        while (targetCompressedFile.exists()) {
                            counter++;
                            targetCompressedFile = new File(trashDir + File.separator + counter + "_" + compressedFileName);
                        }
                        // 移动到回收站
                        FileUtils.moveFile(compressedFile, targetCompressedFile);
                        log.debug("已将压缩图片 {} 移动到回收站 {}", compressedFilePath, targetCompressedFile.getPath());
                    } else {
                        // 直接删除
                        FileUtils.delete(compressedFile);
                        log.debug("已删除压缩图片: {}", compressedFilePath);
                    }
                } else {
                    log.warn("压缩图片文件不存在: {}", compressedFilePath);
                }
            }

            return true;
        } catch (IOException e) {
            log.error("文件操作失败: {}", e.getMessage(), e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件操作失败：" + e.getMessage());
        }
    }

}
