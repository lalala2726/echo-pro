package cn.zhangchuangla.common.utils;

import cn.zhangchuangla.common.enums.ContentType;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ServiceException;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文件相关工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/2/18 15:27
 */
public class FileOperationUtils {
    /**
     * 校验文件有效性
     */
    public static boolean validateFile(MultipartFile file) {
        return file != null && !file.isEmpty() && file.getOriginalFilename() != null;
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
     * 根据文件类型返回合适的 Content-Type
     */
    public static String determineContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || contentType.equals("application/octet-stream")) {
            String ext = getFileExtension(file.getOriginalFilename()).toLowerCase();
            return switch (ext) {
                case ".jpg", ".jpeg" -> "image/jpeg";
                case ".png" -> "image/png";
                case ".gif" -> "image/gif";
                case ".pdf" -> "application/pdf";
                case ".mp4" -> "video/mp4";
                default -> "application/octet-stream";
            };
        }
        return contentType;
    }

    /**
     * 生成唯一文件名,使用UUID和时间戳结合
     */
    public static String generateUUID() {
        return System.currentTimeMillis() + "_" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
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
     * 生成压缩文件名
     */
    public static String generateCompressedFileName(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return "compressed_" + UUID.randomUUID().toString() + ".jpg";
        }

        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            String nameWithoutExt = originalFilename.substring(0, lastDotIndex);
            String extension = originalFilename.substring(lastDotIndex);
            return nameWithoutExt + "_compressed" + extension;
        } else {
            return originalFilename + "_compressed";
        }
    }

    /**
     * 生成文件名
     *
     * @return 文件名
     */
    public static String generateFileName() {
        return String.format("%s%s", System.currentTimeMillis(), UUIDUtils.simpleUUID().substring(0, 8));
    }

    /**
     * 生成年月格式的目录路径，用于对象存储路径（始终使用 / 作为分隔符）
     *
     * @return 例如 "2025/04"
     */
    public static String generateYearMonthDir() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM");
        return format.format(new Date());
    }


    /**
     * 生成文件相对路径
     *
     * @param targetDir     目标目录
     * @param fileName      文件名
     * @param fileExtension 文件扩展
     * @return 文件相对路径
     */
    public static String generateFileRelativePath(String targetDir, String fileName, String fileExtension) {
        return ("/" + targetDir + "/" + fileName + fileExtension)
                .replace('\\', '/');
    }

    /**
     * 生成文件ContentType
     *
     * @param fileName 文件名
     * @return 返回文件ContentType
     */
    public static String generateFileContentType(String fileName) {
        return ContentType.fromExtension(fileName).toString();
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
                if (pathBuilder.length() > 0 && !sanitizedArg.startsWith("/")) {
                    pathBuilder.append("/");
                }
                pathBuilder.append(sanitizedArg);
            }
        }
        return pathBuilder.toString();
    }

    /**
     * 为保持向后兼容性，保留旧方法名称
     *
     * @param args 文件路径
     * @return 文件路径
     */
    public static String BuildFinalPath(String... args) {
        return buildFinalPath(args);
    }
}
