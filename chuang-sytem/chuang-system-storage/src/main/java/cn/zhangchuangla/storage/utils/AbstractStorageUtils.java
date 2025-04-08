package cn.zhangchuangla.storage.utils;

import cn.zhangchuangla.common.constant.StorageConstants;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.common.utils.ImageUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 存储工具类抽象基类
 * 封装各存储服务的共用方法
 *
 * @author Chuang
 * <p>
 * created on 2025/4/3 15:00
 */
@Slf4j
public abstract class AbstractStorageUtils {

    /**
     * 校验图片类型
     *
     * @param fileTransferDto 文件传输对象
     * @return 是否为图片
     */
    protected static boolean isImage(FileTransferDto fileTransferDto) {
        if (fileTransferDto == null || fileTransferDto.getBytes() == null
                || fileTransferDto.getOriginalName() == null) {
            return false;
        }
        String fileExtension = fileTransferDto.getFileExtension();
        return ImageUtils.isImage(fileExtension);
    }

    /**
     * 验证文件上传参数
     *
     * @param fileTransferDto 文件传输对象
     * @param configObject    配置对象
     * @return 文件名
     */
    protected static String validateUploadParams(FileTransferDto fileTransferDto, Object configObject) {
        if (configObject == null) {
            throw new FileException(ResponseCode.FileUploadFailed, "存储服务配置不能为空！");
        }

        if (fileTransferDto == null || fileTransferDto.getBytes() == null
                || fileTransferDto.getOriginalName() == null) {
            throw new FileException(ResponseCode.FileUploadFailed, "文件数据或文件名不能为空！");
        }

        return fileTransferDto.getOriginalName();
    }

    /**
     * 生成文件的统一存储路径
     *
     * @param fileName 文件名
     * @return 存储路径
     */
    protected static String generateFilePath(String fileName) {
        String datePath = FileUtils.generateYearMonthDir();
        String fileExtension = FileUtils.getFileExtension(fileName);
        String uuid = FileUtils.generateUUID();
        return FileUtils.buildFinalPath(datePath, StorageConstants.STORAGE_DIR_FILE, uuid + fileExtension);
    }

    /**
     * 生成图片的原始存储路径
     *
     * @param fileName 文件名
     * @return 原始图片存储路径
     */
    protected static String generateOriginalImagePath(String fileName) {
        String datePath = FileUtils.generateYearMonthDir();
        String fileExtension = FileUtils.getFileExtension(fileName);
        String uuid = FileUtils.generateUUID();
        String fileName1 = uuid + fileExtension;
        String originalDir = FileUtils.buildFinalPath(datePath, StorageConstants.STORAGE_DIR_IMAGES,
                StorageConstants.FILE_ORIGINAL_FOLDER);
        return FileUtils.buildFinalPath(originalDir, fileName1);
    }

    /**
     * 生成图片的压缩存储路径
     *
     * @param fileName 文件名
     * @return 压缩图片存储路径
     */
    protected static String generateCompressedImagePath(String fileName) {
        String datePath = FileUtils.generateYearMonthDir();
        String fileExtension = FileUtils.getFileExtension(fileName);
        String uuid = FileUtils.generateUUID();
        String fileName1 = uuid + fileExtension;
        String compressedDir = FileUtils.buildFinalPath(datePath, StorageConstants.STORAGE_DIR_IMAGES,
                StorageConstants.FILE_PREVIEW_FOLDER);
        return FileUtils.buildFinalPath(compressedDir, fileName1);
    }

    /**
     * 压缩图片
     *
     * @param originalData 原始图片数据
     * @return 压缩后的图片数据
     */
    protected static byte[] compressImage(byte[] originalData) {
        try {
            return ImageUtils.compressImage(originalData, 800, 800, 0.7f);
        } catch (IOException e) {
            log.error("图片压缩失败", e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "图片压缩失败：" + e.getMessage());
        }
    }

    /**
     * 构建完整URL
     *
     * @param domain       域名
     * @param relativePath 相对路径
     * @return 完整URL
     */
    protected static String buildFullUrl(String domain, String relativePath) {
        return FileUtils.buildFinalPath(domain, relativePath);
    }

    /**
     * 创建标准的FileTransferDto响应对象
     *
     * @param originalUrl          原始文件URL
     * @param originalRelativePath 原始文件相对路径
     * @param compressedUrl        压缩文件URL (可为null)
     * @param compressedPath       压缩文件相对路径 (可为null)
     * @return 文件传输对象
     */
    protected static FileTransferDto createFileTransferResponse(
            String originalUrl, String originalRelativePath,
            String compressedUrl, String compressedPath) {
        return FileTransferDto.builder()
                .originalFileUrl(originalUrl)
                .originalRelativePath(originalRelativePath)
                .compressedFileUrl(compressedUrl)
                .compressedRelativePath(compressedPath)
                .build();
    }

    /**
     * 填充文件传输对象的基本信息
     *
     * @param fileTransferDto 文件传输对象
     * @param storageType     存储类型
     * @param bucketName      桶名称
     * @return 填充后的文件传输对象
     */
    protected static FileTransferDto fillFileTransferInfo(FileTransferDto fileTransferDto, String storageType,
                                                          String bucketName) {
        if (fileTransferDto == null || fileTransferDto.getBytes() == null
                || fileTransferDto.getOriginalName() == null) {
            throw new FileException(ResponseCode.FileUploadFailed, "文件数据或文件名不能为空！");
        }

        byte[] data = fileTransferDto.getBytes();
        String fileName = fileTransferDto.getOriginalName();

        // 填充文件基础信息
        fileTransferDto.setFileExtension(FileUtils.getFileExtensionWithoutDot(fileName));
        fileTransferDto.setContentType(FileUtils.generateFileContentType(fileName));
        fileTransferDto.setFileMd5(FileUtils.calculateMD5(data));

        // 计算并格式化文件大小
        long fileSizeBytes = data.length;
        String formattedSize = formatFileSize(fileSizeBytes);
        fileTransferDto.setFileSize(formattedSize);

        // 设置存储相关信息
        fileTransferDto.setStorageType(storageType);
        fileTransferDto.setBucketName(bucketName);

        return fileTransferDto;
    }

    /**
     * 格式化文件大小为人类可读格式
     *
     * @param sizeInBytes 文件大小（字节）
     * @return 格式化后的文件大小字符串
     */
    protected static String formatFileSize(long sizeInBytes) {
        if (sizeInBytes < 1024) {
            return sizeInBytes + " B";
        } else if (sizeInBytes < 1024 * 1024) {
            return String.format("%.2f KB", sizeInBytes / 1024.0);
        } else if (sizeInBytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", sizeInBytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", sizeInBytes / (1024.0 * 1024 * 1024));
        }
    }

    /**
     * 创建增强版的FileTransferDto响应对象，填充所有必要的字段
     *
     * @param originalUrl          原始文件URL
     * @param originalRelativePath 原始文件相对路径
     * @param compressedUrl        压缩文件URL (可为null)
     * @param compressedPath       压缩文件相对路径 (可为null)
     * @param fileTransferDto      原始文件传输对象，包含基础信息
     * @return 增强的文件传输对象
     */
    protected static FileTransferDto createEnhancedFileTransferResponse(
            String originalUrl, String originalRelativePath,
            String compressedUrl, String compressedPath,
            FileTransferDto fileTransferDto) {

        // 保留原有信息
        fileTransferDto.setOriginalFileUrl(originalUrl);
        fileTransferDto.setOriginalRelativePath(originalRelativePath);
        fileTransferDto.setCompressedFileUrl(compressedUrl);
        fileTransferDto.setCompressedRelativePath(compressedPath);

        return fileTransferDto;
    }
}
