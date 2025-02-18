package cn.zhangchuangla.common.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 文件相关工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/2/18 15:27
 */
public class FileUtils {
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

}
