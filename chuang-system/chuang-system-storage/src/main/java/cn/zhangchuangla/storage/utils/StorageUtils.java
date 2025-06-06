package cn.zhangchuangla.storage.utils;

import java.util.UUID;
import cn.zhangchuangla.common.core.constant.StorageConstants;
import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.FileException;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.model.dto.FileTransferDto;
import cn.zhangchuangla.common.core.utils.ImageUtils;
import cn.zhangchuangla.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

/**
 * 存储服务通用工具类
 * 封装存储服务共用的方法，如判断文件类型、生成路径等
 *
 * @author Chuang
 * <p>
 * created on 2025/4/9 18:01
 */
@Slf4j
public class StorageUtils {

    private static final Tika TIKA_INSTANCE = new Tika();
    private static final String URL_SEPARATOR = "/";

    /**
     * 校验图片类型
     *
     * @param fileTransferDto 文件传输对象
     * @return 是否为图片
     */
    public static boolean isImage(FileTransferDto fileTransferDto) {
        if (fileTransferDto == null || fileTransferDto.getBytes() == null
                || fileTransferDto.getOriginalName() == null) {
            return false;
        }
        String fileExtension = StorageUtils.getFileExtensionWithoutDot(fileTransferDto.getOriginalName());
        fileTransferDto.setFileExtension(fileExtension);
        return ImageUtils.isImage(fileExtension);
    }

    /**
     * 验证文件上传参数
     *
     * @param fileTransferDto 文件传输对象
     * @param configObject    配置对象
     */
    public static void validateUploadParams(FileTransferDto fileTransferDto, Object configObject) {
        if (configObject == null) {
            throw new FileException(ResponseCode.FileUploadFailed, "存储服务配置不能为空！");
        }

        if (fileTransferDto == null || fileTransferDto.getBytes() == null
                || fileTransferDto.getOriginalName() == null) {
            throw new FileException(ResponseCode.FileUploadFailed, "文件数据或文件名不能为空！");
        }

    }

    /**
     * 生成文件ContentType
     *
     * @param file 文件
     * @return 返回文件ContentType
     */
    public static String generateFileContentType(byte[] file) {
        return TIKA_INSTANCE.detect(file);
    }

    /**
     * 生成文件的统一存储路径
     *
     * @param fileName 文件名
     * @return 存储路径
     */
    public static String generateFilePath(String fileName) {
        String datePath = StorageUtils.generateYearMonthDir();
        String fileExtension = StorageUtils.getFileExtension(fileName);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return buildFinalPath(datePath, StorageConstants.STORAGE_DIR_FILE, uuid + fileExtension);
    }

    /**
     * 生成图片的原始存储路径
     *
     * @param fileName 文件名
     * @return 原始图片存储路径
     */
    public static String generateOriginalImagePath(String fileName) {
        String datePath = StorageUtils.generateYearMonthDir();
        String fileExtension = StorageUtils.getFileExtension(fileName);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String fileName1 = uuid + fileExtension;
        String originalDir = buildFinalPath(datePath, StorageConstants.STORAGE_DIR_IMAGES,
                StorageConstants.FILE_ORIGINAL_FOLDER);
        return buildFinalPath(originalDir, fileName1);
    }

    /**
     * 构建文件路径
     *
     * @param args 文件路径
     * @return 文件路径
     */
    public static String buildFinalPath(String... args) {
        if (args == null || args.length == 0) {
            return "";
        }
        StringBuilder pathBuilder = new StringBuilder();
        for (String arg : args) {
            if (arg != null && !arg.isEmpty()) {
                // 去除路径中的反斜杠
                String sanitizedArg = arg.replace("\\", "/");
                // 如果路径不以斜杠开头，则添加斜杠
                if (!pathBuilder.isEmpty() && !sanitizedArg.startsWith("/")) {
                    pathBuilder.append("/");
                }
                pathBuilder.append(sanitizedArg);
            }
        }
        return pathBuilder.toString();
    }

    /**
     * 生成按年月组织的目录路径
     * 格式: yyyy/MM
     *
     * @return 年月目录路径，如 2023/05
     */
    public static String generateYearMonthDir() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        // 月份从0开始，所以+1
        int month = now.get(Calendar.MONTH) + 1;
        return String.format("%d/%02d", year, month);
    }

    /**
     * 获取文件扩展名（带点）
     */
    public static String getFileExtension(String fileName) {
        return (fileName != null && fileName.contains(".")) ? fileName.substring(fileName.lastIndexOf(".")) : "";
    }

    /**
     * 获取文件扩展名（不带点）
     */
    public static String getFileExtensionWithoutDot(String fileName) {
        return (fileName != null && fileName.contains(".")) ? fileName.substring(fileName.lastIndexOf(".") + 1) : "";
    }

    /**
     * 计算文件的MD5值
     *
     * @param fileBytes 文件字节数组
     * @return 返回MD5值
     */
    public static String calculateMD5(byte[] fileBytes) {
        if (fileBytes == null) {
            throw new ServiceException(ResponseCode.PARAM_NOT_NULL, "文件不能为空！");
        }
        try {
            // 获取MD5算法的MessageDigest实例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算MD5摘要
            byte[] digest = md.digest(fileBytes);
            // 将byte数组转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                // 将每个byte转为两位十六进制数
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new ServiceException(ResponseCode.SYSTEM_ERROR, "计算MD5值失败：" + e.getMessage());
        }
    }

    /**
     * 从相对路径获取文件名
     *
     * @param relativePath 相对路径
     * @return 文件名（包含扩展名）
     */
    public static String getFileNameByRelativePath(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return "";
        }

        // 替换Windows路径分隔符为Unix风格
        String path = relativePath.replace('\\', '/');

        // 获取最后一个斜杠后的内容作为文件名
        int lastSlashIndex = path.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex < path.length() - 1) {
            return path.substring(lastSlashIndex + 1);
        }

        // 如果没有斜杠，整个字符串就是文件名
        return path;
    }

    /**
     * 生成图片的压缩存储路径
     *
     * @param fileName 文件名
     * @return 压缩图片存储路径
     */
    public static String generateCompressedImagePath(String fileName) {
        String datePath = generateYearMonthDir();
        String fileExtension = getFileExtension(fileName);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String fileName1 = uuid + fileExtension;
        String compressedDir = buildFinalPath(datePath, StorageConstants.STORAGE_DIR_IMAGES,
                StorageConstants.FILE_PREVIEW_FOLDER);
        return buildFinalPath(compressedDir, fileName1);
    }

    /**
     * 压缩图片
     *
     * @param originalData 原始图片数据
     * @return 压缩后的图片数据
     */
    public static byte[] compressImage(byte[] originalData) {
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
    public static String buildFullUrl(String domain, String relativePath) {
        return buildFinalPath(domain, relativePath);
    }

    /**
     * 填充文件传输对象的基本信息
     *
     * @param fileTransferDto 文件传输对象
     * @param storageType     存储类型
     * @param bucketName      桶名称
     */
    public static void fillFileTransferInfo(FileTransferDto fileTransferDto, String storageType,
                                            String bucketName) {
        if (fileTransferDto == null || fileTransferDto.getBytes() == null
                || fileTransferDto.getOriginalName() == null) {
            throw new FileException(ResponseCode.FileUploadFailed, "文件数据或文件名不能为空！");
        }

        byte[] data = fileTransferDto.getBytes();
        String fileName = fileTransferDto.getOriginalName();

        // 填充文件基础信息
        fileTransferDto.setFileExtension(getFileExtensionWithoutDot(fileName));
        fileTransferDto.setContentType(StorageUtils.generateFileContentType(data));
        fileTransferDto.setFileMd5(calculateMD5(data));

        // 计算并格式化文件大小
        long fileSizeBytes = data.length;
        String formattedSize = formatFileSize(fileSizeBytes);
        fileTransferDto.setFileSize(formattedSize);

        // 设置存储相关信息
        fileTransferDto.setStorageType(storageType);
        fileTransferDto.setBucketName(bucketName);

    }

    /**
     * 格式化文件大小为人类可读格式
     *
     * @param sizeInBytes 文件大小（字节）
     * @return 格式化后的文件大小字符串
     */
    public static String formatFileSize(long sizeInBytes) {
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
    public static FileTransferDto createEnhancedFileTransferResponse(
            String originalUrl, String originalRelativePath,
            String compressedUrl, String compressedPath,
            FileTransferDto fileTransferDto) {

        // 保留原有信息
        fileTransferDto.setOriginalFileUrl(originalUrl);
        fileTransferDto.setOriginalRelativePath(originalRelativePath);
        fileTransferDto.setPreviewImageUrl(compressedUrl);
        fileTransferDto.setPreviewImagePath(compressedPath);

        return fileTransferDto;
    }

    /**
     * 生成回收站路径
     *
     * @param fileName  文件名
     * @param subFolder 子文件夹名称
     * @return 回收站路径
     */
    public static String generateTrashPath(String fileName, String subFolder) {
        String yearMonthDir = StorageUtils.generateYearMonthDir();
        return StorageConstants.TRASH_DIR + "/" +
                subFolder + "/" +
                yearMonthDir + "/" +
                System.currentTimeMillis() + "_" + fileName;
    }

    /**
     * 检查文件是否存在
     * 通用方法，记录警告日志并返回存在状态
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param existsFunc 检查是否存在的函数式接口
     * @return 文件是否存在
     */
    public static boolean checkObjectExistsWithLogging(String bucketName, String objectName, ExistsFunction existsFunc) {
        try {
            boolean exists = existsFunc.exists();
            if (!exists) {
                log.warn("文件不存在: {}/{}", bucketName, objectName);
            }
            return exists;
        } catch (Exception e) {
            log.warn("检查文件是否存在时出错: {}", e.getMessage());
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "检查文件是否存在失败: " + e.getMessage());
        }
    }

    /**
     * 验证文件恢复所需的参数
     *
     * @param fileTransferDto 文件传输对象
     * @param configObject    存储配置对象
     */
    public static void validateRecoveryParams(FileTransferDto fileTransferDto, Object configObject) {
        if (fileTransferDto == null) {
            throw new FileException(ResponseCode.FILE_OPERATION_ERROR, "文件传输对象不能为空，无法进行恢复");
        }

        if (configObject == null) {
            throw new FileException(ResponseCode.FILE_OPERATION_ERROR, "存储配置不能为空，无法进行恢复");
        }

        // 验证必要的路径信息
        if (StringUtils.isEmpty(fileTransferDto.getOriginalRelativePath()) ||
                StringUtils.isEmpty(fileTransferDto.getOriginalTrashPath())) {
            throw new FileException(ResponseCode.FILE_OPERATION_ERROR,
                    "文件信息不完整，缺少原始路径或回收站路径");
        }
    }

    /**
     * 验证文件删除参数
     *
     * @param fileTransferDto 文件传输对象
     * @param configObject    存储配置对象
     */
    public static void validateRemoveParams(FileTransferDto fileTransferDto, Object configObject) {
        if (fileTransferDto == null || configObject == null ||
                StringUtils.isEmpty(fileTransferDto.getOriginalRelativePath())) {
            log.error("文件信息不完整，无法删除");
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件信息不完整，无法删除！");
        }
    }

    /**
     * 记录文件操作类型日志
     *
     * @param storageType        存储类型名称
     * @param originalObjectName 原始对象名称
     * @param previewObjectName  预览对象名称
     * @param hasPreviewImage    是否有预览图
     * @param enableTrash        是否启用回收站
     */
    public static void logFileOperationType(String storageType, String originalObjectName,
                                            String previewObjectName, boolean hasPreviewImage, boolean enableTrash) {
        log.info("开始处理{}文件 - 原始对象: {}, 预览图: {}, 操作模式: {}",
                storageType,
                originalObjectName,
                hasPreviewImage ? previewObjectName : "无",
                enableTrash ? "移至回收站" : "直接删除");
    }

    /**
     * 处理文件恢复过程中的错误
     *
     * @param hasError 是否有错误发生
     * @throws IOException IO异常
     */
    public static void handleRecoveryErrors(boolean hasError) throws IOException {
        if (hasError) {
            throw new IOException("文件恢复过程中发生错误，部分文件可能未恢复成功");
        }
    }

    /**
     * 根据日期和原始文件名生成包含子目录的唯一文件路径
     *
     * @param subPath        可选的基础子路径（例如："user-uploads", "images"）。可以为 null 或空字符串
     * @param uniqueFileName 文件的唯一名称（例如：带时间戳/UUID）
     * @return 相对路径字符串，例如："user-uploads/2023/05/17/uniqueFileName.jpg" 或 "2023/05/17/uniqueFileName.jpg"
     */
    public static String generatePath(String subPath, String uniqueFileName) {
        java.time.LocalDate today = java.time.LocalDate.now();
        String year = String.valueOf(today.getYear());
        String month = String.format("%02d", today.getMonthValue());
        String day = String.format("%02d", today.getDayOfMonth());

        StringBuilder pathBuilder = new StringBuilder();
        if (org.springframework.util.StringUtils.hasText(subPath)) {
            pathBuilder.append(sanitizePath(subPath)).append(URL_SEPARATOR);
        }
        pathBuilder.append(year).append(URL_SEPARATOR);
        pathBuilder.append(month).append(URL_SEPARATOR);
        pathBuilder.append(day).append(URL_SEPARATOR);
        pathBuilder.append(sanitizePath(uniqueFileName));

        return pathBuilder.toString();
    }

    /**
     * 生成压缩图/缩略图的存储路径
     *
     * @param uniqueFileName 文件的唯一名称
     * @return 路径示例："images/thumbnail/2023/05/17/uniqueFileName.jpg"
     */
    public static String generateThumbnailImagePath(String uniqueFileName) {
        return generatePath("images/thumbnail", uniqueFileName);
    }

    /**
     * 对路径片段进行安全处理以防止目录遍历问题
     * 移除首尾的斜杠并规范化路径分隔符
     *
     * @param pathSegment 需要处理的路径片段
     * @return 处理后的路径片段
     */
    private static String sanitizePath(String pathSegment) {
        if (!org.springframework.util.StringUtils.hasText(pathSegment)) {
            return "";
        }
        // 将路径分隔符统一转换为正斜杠
        String normalized = FilenameUtils.normalizeNoEndSeparator(pathSegment.replace("\\", URL_SEPARATOR), true);
        // 移除开头的斜杠以避免在相对路径中被解释为绝对路径
        if (normalized.startsWith(URL_SEPARATOR)) {
            normalized = normalized.substring(1);
        }
        return normalized;
    }

    /**
     * 将基础URL/域名与相对路径拼接成完整URL
     *
     * @param baseOrDomain 基础URL或域名（例如："https://cdn.example.com", "/resources"）
     * @param relativePath 文件的相对路径（例如："images/pic.jpg"）
     * @return 完整的URL
     */
    public static String concatUrl(String baseOrDomain, String relativePath) {
        String sanitizedBase = baseOrDomain != null ? baseOrDomain.trim() : "";
        String sanitizedRelativePath = relativePath != null ? relativePath.trim() : "";

        // 移除base末尾的斜杠
        if (sanitizedBase.endsWith(URL_SEPARATOR)) {
            sanitizedBase = sanitizedBase.substring(0, sanitizedBase.length() - 1);
        }
        // 移除相对路径开头的斜杠
        if (sanitizedRelativePath.startsWith(URL_SEPARATOR)) {
            sanitizedRelativePath = sanitizedRelativePath.substring(1);
        }
        return sanitizedBase + URL_SEPARATOR + sanitizedRelativePath;
    }

    /**
     * 函数式接口：检查对象是否存在
     */
    @FunctionalInterface
    public interface ExistsFunction {
        boolean exists() throws Exception;
    }

}
